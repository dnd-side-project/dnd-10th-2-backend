package org.dnd.timeet.agenda.application;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.agenda.domain.Agenda;
import org.dnd.timeet.agenda.domain.AgendaRepository;
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
}
