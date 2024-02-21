package org.dnd.timeet.meeting.application;

import static org.dnd.timeet.common.utils.TimeUtils.calculateTimeDiff;
import static org.dnd.timeet.common.utils.TimeUtils.formatDuration;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.agenda.domain.AgendaRepository;
import org.dnd.timeet.agenda.domain.AgendaStatus;
import org.dnd.timeet.agenda.domain.AgendaType;
import org.dnd.timeet.agenda.dto.AgendaReportInfoResponse;
import org.dnd.timeet.common.exception.ForbiddenError;
import org.dnd.timeet.common.exception.NotFoundError;
import org.dnd.timeet.common.exception.NotFoundError.ErrorCode;
import org.dnd.timeet.meeting.domain.Meeting;
import org.dnd.timeet.meeting.domain.MeetingRepository;
import org.dnd.timeet.meeting.dto.MeetingCreateRequest;
import org.dnd.timeet.meeting.dto.MeetingCurrentDurationResponse;
import org.dnd.timeet.meeting.dto.MeetingMemberInfoResponse;
import org.dnd.timeet.meeting.dto.MeetingMemberInfoResponse.MeetingMemberDetailResponse;
import org.dnd.timeet.meeting.dto.MeetingReportInfoResponse;
import org.dnd.timeet.member.domain.Member;
import org.dnd.timeet.member.domain.MemberRepository;
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
    private final MemberRepository memberRepository;


    public Meeting createMeeting(MeetingCreateRequest createDto, Member member) {
        Meeting meeting = createDto.toEntity(member);
        meeting = meetingRepository.save(meeting);

        Participant participant = new Participant(meeting, member);
        participantRepository.save(participant);

        return meeting;
    }

    public void endMeeting(Long meetingId, Long memberId) {
        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new NotFoundError(NotFoundError.ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MeetingId", "Meeting not found")));
        // 회의의 방장이 존재하는지 확인
        if (meeting.getHostMember() == null) {
            throw new NotFoundError(NotFoundError.ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("HostMemberId", "Host member not found"));
        }
        // 회의의 방장인지 확인
        if (!Objects.equals(meeting.getHostMember().getId(), memberId)) {
            throw new ForbiddenError(ForbiddenError.ErrorCode.ROLE_BASED_ACCESS_ERROR,
                Collections.singletonMap("MemberId", "Member is not the host of the meeting"));
        }

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

        participantRepository.save(meeting.addParticipant(member));

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
    public MeetingMemberInfoResponse getMeetingMembers(Long meetingId, Long memberId) {
        Meeting meeting = meetingRepository.findByIdWithParticipantsAndMembers(meetingId)
            .orElseThrow(() -> new NotFoundError(ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MeetingId", "Meeting not found")));

        boolean memberExists = memberRepository.existsById(memberId);
        if (!memberExists) {
            throw new NotFoundError(ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MemberId", "Member not found"));
        }

        // 회의에 참가자가 없는 경우 빈 리스트 반환
        if (meeting.getParticipants().isEmpty()) {
            return new MeetingMemberInfoResponse(Collections.emptyList(), null, false);
        }

        // 참가자 목록을 Member 객체의 리스트로 변환하여 반환
        List<MeetingMemberDetailResponse> memberList = meeting.getParticipants().stream()
            .map(Participant::getMember)
            .filter(member -> !member.equals(meeting.getHostMember())) // 방장 제외
            .map(MeetingMemberDetailResponse::new)
            .toList();

        MeetingMemberDetailResponse hostResponse;
        // 방장 정보 추출
        if (meeting.getHostMember() == null) {
            hostResponse = new MeetingMemberDetailResponse(null);

            return new MeetingMemberInfoResponse(memberList, hostResponse, false);
        } else {
            Member hostMember = memberRepository.findById(meeting.getHostMember().getId())
                .orElseThrow(() -> new NotFoundError(ErrorCode.RESOURCE_NOT_FOUND,
                    Collections.singletonMap("HostMemberId", "Host member not found")));
            hostResponse = new MeetingMemberDetailResponse(hostMember);

            return new MeetingMemberInfoResponse(memberList, hostResponse, hostMember.getId().equals(memberId));
        }

    }

    @Transactional(readOnly = true)
    public MeetingCurrentDurationResponse getCurrentDuration(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new NotFoundError(ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MeetingId", "Meeting not found")));

        return MeetingCurrentDurationResponse.from(meeting);
    }

    public void leaveMeeting(Long meetingId, Long memberId) {
        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new NotFoundError(ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MeetingId", "Meeting not found")));

        boolean memberExists = memberRepository.existsById(memberId);
        if (!memberExists) {
            throw new NotFoundError(ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MemberId", "Member not found"));
        }

        Participant participant = participantRepository.findByMeetingIdAndMemberId(meetingId, memberId)
            .orElseThrow(() -> new NotFoundError(NotFoundError.ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("ParticipantId", "Participant not found")));

        // 방장이 나가는 경우 새로운 방장 지정
        if (meeting.getHostMember() == null || meeting.getHostMember().getId().equals(memberId)) {
            meeting.assignNewHostRandomly();
            meetingRepository.save(meeting);
        }

        participant.removeParticipant();
        participantRepository.save(participant);
    }
}
