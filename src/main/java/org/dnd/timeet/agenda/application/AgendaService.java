package org.dnd.timeet.agenda.application;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.agenda.domain.Agenda;
import org.dnd.timeet.agenda.domain.AgendaAction;
import org.dnd.timeet.agenda.domain.AgendaRepository;
import org.dnd.timeet.agenda.domain.AgendaStatus;
import org.dnd.timeet.agenda.dto.AgendaActionRequest;
import org.dnd.timeet.agenda.dto.AgendaActionResponse;
import org.dnd.timeet.agenda.dto.AgendaCreateRequest;
import org.dnd.timeet.agenda.dto.AgendaInfoResponse;
import org.dnd.timeet.agenda.dto.AgendaPatchRequest;
import org.dnd.timeet.agenda.dto.AgendaPatchResponse;
import org.dnd.timeet.common.exception.BadRequestError;
import org.dnd.timeet.common.exception.NotFoundError;
import org.dnd.timeet.common.exception.NotFoundError.ErrorCode;
import org.dnd.timeet.common.utils.DurationUtils;
import org.dnd.timeet.meeting.domain.Meeting;
import org.dnd.timeet.meeting.domain.MeetingRepository;
import org.dnd.timeet.member.domain.Member;
import org.dnd.timeet.participant.domain.ParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // final 의존성 주입
@Transactional // DB 변경 작업에 사용
public class AgendaService {

    private final MeetingRepository meetingRepository;
    private final AgendaRepository agendaRepository;
    private final ParticipantRepository participantRepository;

    public Long createAgenda(Long meetingId, AgendaCreateRequest createDto, Member member) {
        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new NotFoundError(ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MeetingId", "Meeting not found")));

        // 회의에 참가한 멤버인지 확인
        boolean isParticipantExists = participantRepository.existsByMeetingIdAndMemberId(meetingId, member.getId());
        if (!isParticipantExists) {
            throw new BadRequestError(BadRequestError.ErrorCode.VALIDATION_FAILED,
                Collections.singletonMap("MemberId", "Member is not a participant of the meeting"));
        }

        Agenda agenda = createDto.toEntity(meeting);
        agenda = agendaRepository.save(agenda);

        // 회의 시간 추가
        addMeetingTotalActualDuration(meetingId,
            DurationUtils.convertLocalTimeToDuration(createDto.getAllocatedDuration()));

        return agenda.getId();
    }

    @Transactional(readOnly = true)
    public List<Agenda> findAll(Long meetingId) {
        return agendaRepository.findByMeetingId(meetingId);
    }

    public AgendaActionResponse changeAgendaStatus(Long meetingId, Long agendaId, AgendaActionRequest actionRequest) {
        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new NotFoundError(ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MeetingId", "Meeting not found")));

        Agenda agenda = agendaRepository.findByIdAndMeetingId(agendaId, meetingId)
            .orElseThrow(() -> new NotFoundError(ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("AgendaId", "Agenda not found")));

        String actionString = actionRequest.getAction().toUpperCase();
        AgendaAction action;

        try {
            action = AgendaAction.valueOf(actionString);
        } catch (IllegalArgumentException e) {
            throw new BadRequestError(BadRequestError.ErrorCode.VALIDATION_FAILED,
                Collections.singletonMap("Action", "Invalid action"));
        }

        // 안건 시작 요청 전 첫 번째 안건이 시작되었는지 확인
        if (action == AgendaAction.START && agenda.getOrderNum() != 1) {
            // 첫 번째 안건의 시작 여부 확인
            boolean isFirstAgendaStarted = agendaRepository.existsByMeetingIdAndOrderNumAndStatus(
                meetingId, 1, AgendaStatus.COMPLETED);

            if (!isFirstAgendaStarted) {
                throw new BadRequestError(BadRequestError.ErrorCode.WRONG_REQUEST_TRANSMISSION,
                    Collections.singletonMap("AgendaOrder", "First agenda has not been started yet"));
            }
        }

        switch (action) {
            case START -> {
                // 첫번째 안건의 시작 시간을 회의 시작 시간으로 설정
                meeting.updateStartTimeOnFirstAgendaStart(agenda);
                agenda.start();
            }
            case PAUSE -> agenda.pause();
            case RESUME -> agenda.resume();
            case END -> agenda.complete();
            case MODIFY -> {
                LocalTime modifiedDuration = LocalTime.parse(actionRequest.getModifiedDuration());
                Duration duration = DurationUtils.convertLocalTimeToDuration(modifiedDuration);
                agenda.extendDuration(duration);
                // 회의 시간 추가
                addMeetingTotalActualDuration(meetingId, duration);
            }
            default -> throw new BadRequestError(BadRequestError.ErrorCode.VALIDATION_FAILED,
                Collections.singletonMap("Action", "Invalid action"));
        }
        // 변경 사항 저장
        Agenda savedAgenda = agendaRepository.save(agenda);
        meetingRepository.save(meeting);

        Duration currentDuration = savedAgenda.calculateCurrentDuration();
        Duration remainingDuration = agenda.calculateRemainingTime();

        return new AgendaActionResponse(savedAgenda, currentDuration, remainingDuration);
    }

    public void cancelAgenda(Long meetingId, Long agendaId) {
        Agenda agenda = agendaRepository.findByIdAndMeetingId(agendaId, meetingId)
            .orElseThrow(() -> new NotFoundError(ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("AgendaId", "Agenda not found")));
        if (agenda.getStatus() != AgendaStatus.PENDING) {
            throw new BadRequestError(BadRequestError.ErrorCode.WRONG_REQUEST_TRANSMISSION,
                Collections.singletonMap("AgendaStatus", "Agenda is not PENDING status"));
        }
        agenda.cancel();

        subtractMeetingTotalActualDuration(meetingId, agenda.getAllocatedDuration());
    }

    public void addMeetingTotalActualDuration(Long meetingId, Duration additionalDuration) {
        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new NotFoundError(ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MeetingId", "Meeting not found")));

        Duration newTotalDuration = meeting.getTotalActualDuration().plus(additionalDuration);
        meeting.updateTotalActualDuration(newTotalDuration);
        meetingRepository.save(meeting);
    }

    public void subtractMeetingTotalActualDuration(Long meetingId, Duration subtractedDuration) {
        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new NotFoundError(ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MeetingId", "Meeting not found")));

        Duration newTotalDuration = meeting.getTotalActualDuration().minus(subtractedDuration);
        meeting.updateTotalActualDuration(newTotalDuration);
        meetingRepository.save(meeting);
    }

    public AgendaInfoResponse findAgendas(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new NotFoundError(ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MeetingId", "Meeting not found")));
        List<Agenda> agendaList = agendaRepository.findByMeetingId(meetingId);

        return new AgendaInfoResponse(meeting, agendaList);
    }

    public AgendaPatchResponse patchAgenda(Long meetingId, Long agendaId, AgendaPatchRequest patchRequest) {
        // 회의 존재 여부만 확인
        boolean meetingExists = meetingRepository.existsById(meetingId);
        if (!meetingExists) {
            throw new NotFoundError(ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MeetingId", "Meeting not found"));
        }

        Agenda agenda = agendaRepository.findByIdAndMeetingId(agendaId, meetingId)
            .orElseThrow(() -> new NotFoundError(ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("AgendaId", "Agenda not found")));
        if (agenda.getStatus() != AgendaStatus.PENDING) {
            throw new BadRequestError(BadRequestError.ErrorCode.WRONG_REQUEST_TRANSMISSION,
                Collections.singletonMap("AgendaStatus", "Agenda is not PENDING status"));
        }

        agenda.update(patchRequest.getTitle(),
            DurationUtils.convertLocalTimeToDuration(patchRequest.getAllocatedDuration()));

        return new AgendaPatchResponse(agenda);
    }
}
