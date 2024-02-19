package org.dnd.timeet.meeting.application;

import static org.dnd.timeet.common.utils.TimeUtils.calculateTimeDiff;
import static org.dnd.timeet.common.utils.TimeUtils.formatDuration;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.agenda.domain.AgendaRepository;
import org.dnd.timeet.agenda.domain.AgendaStatus;
import org.dnd.timeet.agenda.domain.AgendaType;
import org.dnd.timeet.agenda.dto.AgendaReportInfoResponse;
import org.dnd.timeet.common.exception.BadRequestError;
import org.dnd.timeet.common.exception.NotFoundError;
import org.dnd.timeet.common.exception.NotFoundError.ErrorCode;
import org.dnd.timeet.meeting.domain.Meeting;
import org.dnd.timeet.meeting.domain.MeetingRepository;
import org.dnd.timeet.meeting.dto.MeetingCreateRequest;
import org.dnd.timeet.meeting.dto.MeetingRemainingTimeResponse;
import org.dnd.timeet.meeting.dto.MeetingReportInfoResponse;
import org.dnd.timeet.member.domain.Member;
import org.dnd.timeet.participant.domain.Participant;
import org.dnd.timeet.participant.domain.ParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // final 의존성 주입
@Transactional // DB 변경 작업에 사용
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final ParticipantRepository participantRepository;
    private final AgendaRepository agendaRepository;
    private final MeetingScheduler meetingScheduler;


    public Meeting createMeeting(MeetingCreateRequest createDto, Member member) {
        Meeting meeting = createDto.toEntity(member);
        meeting = meetingRepository.save(meeting);

        Participant participant = new Participant(meeting, member);
        participantRepository.save(participant);

        // 스케줄러를 통해 회의 시작 시간에 회의 시작
        meetingScheduler.scheduleMeetingStart(meeting.getId(), meeting.getStartTime());

        return meeting;
    }

    public void endMeeting(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new NotFoundError(NotFoundError.ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MeetingId", "Meeting not found")));

        meeting.endMeeting();
        // 회의에서 진행중인 안건들도 모두 종료되도록 하기
        agendaRepository.findByMeetingId(meetingId)
            .forEach(agenda -> {
                if (agenda.getStatus().equals(AgendaStatus.INPROGRESS) ||
                    agenda.getStatus().equals(AgendaStatus.PAUSED)) {
                    agenda.complete();
                } else if (agenda.getStatus().equals(AgendaStatus.PENDING)) {
                    agenda.cancel();
                }
            });
    }

    public Meeting addParticipantToMeeting(Long meetingId, Member member) {
        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new NotFoundError(NotFoundError.ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MeetingId", "Meeting not found")));

        // 멤버가 이미 회의에 참가하고 있는지 확인
        boolean alreadyParticipating = meeting.getParticipants().stream()
            .anyMatch(participant -> participant.getMember().equals(member));

        if (alreadyParticipating) {
            // 에러 메세지 발생
            throw new BadRequestError(BadRequestError.ErrorCode.DUPLICATE_RESOURCE,
                Collections.singletonMap("Member", "Member already participating in the meeting"));
        }

        // Participant 인스턴스 생성 및 저장
        Participant participant = new Participant(meeting, member);
        participantRepository.save(participant);

        // 양방향 연관관계 설정
        meeting.getParticipants().add(participant);
        member.getParticipations().add(participant);

        return meeting;
    }

    public MeetingReportInfoResponse createReport(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new NotFoundError(NotFoundError.ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MeetingId", "Meeting not found")));

        List<AgendaReportInfoResponse> agendaReportInfoResponses = agendaRepository.findByMeetingId(meetingId).stream()
            .filter(agenda -> agenda.getType() == AgendaType.AGENDA && agenda.getStatus() == AgendaStatus.COMPLETED)
            .map(AgendaReportInfoResponse::from)
            .collect(Collectors.toList());

        return MeetingReportInfoResponse.builder()
            .totalDiff(
                formatDuration(
                    calculateTimeDiff(meeting.getTotalActualDuration(), meeting.getTotalEstimatedDuration())))
            .agendas(agendaReportInfoResponses)
            .memos("회의록입니다.")
            .build();

    }

    @Transactional(readOnly = true)
    public Meeting findById(Long id) {
        return meetingRepository.findById(id)
            .orElseThrow(() -> new NotFoundError(NotFoundError.ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MeetingId", "Meeting not found")));
    }

    public void removeParticipant(Long meetingId, Member member) {
        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new NotFoundError(NotFoundError.ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MeetingId", "Meeting not found")));

        if (meeting.getHostMember().equals(member)) {
            meeting.assignNewHostRandomly();
        }

    }

    public void cancelMeeting(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new NotFoundError(NotFoundError.ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MeetingId", "Meeting not found")));

        meeting.cancelMeeting();
    }

    @Transactional(readOnly = true)
    public List<Member> getMeetingMembers(Long meetingId) {
        Meeting meeting = meetingRepository.findByIdWithParticipantsAndMembers(meetingId)
            .orElseThrow(() -> new NotFoundError(NotFoundError.ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MeetingId", "Meeting not found")));

        return meeting.getParticipants().stream()
            .map(Participant::getMember)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MeetingRemainingTimeResponse getRemainingTime(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new NotFoundError(ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MeetingId", "Meeting not found")));

        return MeetingRemainingTimeResponse.from(meeting);
    }

}
