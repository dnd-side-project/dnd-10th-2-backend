package org.dnd.timeet.agenda.application;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.agenda.domain.Agenda;
import org.dnd.timeet.agenda.domain.AgendaRepository;
import org.dnd.timeet.agenda.dto.AgendaActionRequest;
import org.dnd.timeet.agenda.dto.AgendaCreateRequest;
import org.dnd.timeet.common.exception.BadRequestError;
import org.dnd.timeet.common.exception.NotFoundError;
import org.dnd.timeet.common.exception.NotFoundError.ErrorCode;
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

    public Agenda createAgenda(Long meetingId, AgendaCreateRequest createDto, Member member) {
        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new NotFoundError(ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MeetingId", "Meeting not found")));
        participantRepository.findByMeetingIdAndMemberId(meetingId, member.getId())
            .orElseThrow(() -> new BadRequestError(BadRequestError.ErrorCode.VALIDATION_FAILED,
                Collections.singletonMap("MemberId", "Member is not a participant of the meeting")));

        Agenda agenda = createDto.toEntity(meeting);

        return agendaRepository.save(agenda);
    }

    @Transactional(readOnly = true)
    public List<Agenda> findAll(Long meetingId) {
        return agendaRepository.findByMeetingId(meetingId);
    }

    public Agenda changeAgendaStatus(Long meetingId, Long agendaId, AgendaActionRequest actionRequest) {
        Agenda agenda = agendaRepository.findByIdAndMeetingId(agendaId, meetingId)
            .orElseThrow(() -> new NotFoundError(ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("AgendaId", "Agenda not found")));

        switch (actionRequest.getAction()) {
            case "pause":
                agenda.pause();
                break;
            case "resume":
                agenda.resume();
                break;
            case "end":
                agenda.complete();
                break;
            case "modify":
                LocalTime modifiedDuration = LocalTime.parse(actionRequest.getModifiedDuration());
                agenda.extendDuration(modifiedDuration);
                break;
            default:
                throw new BadRequestError(BadRequestError.ErrorCode.VALIDATION_FAILED,
                    Collections.singletonMap("Action", "Invalid action"));
        }

        return agendaRepository.save(agenda); // 변경된 안건 상태로 응답 객체 생성 및 반환
    }

    public void cancelAgenda(Long meetingId, Long agendaId) {
        Agenda agenda = agendaRepository.findByIdAndMeetingId(agendaId, meetingId)
            .orElseThrow(() -> new NotFoundError(ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("AgendaId", "Agenda not found")));
        agenda.delete();
    }


}
