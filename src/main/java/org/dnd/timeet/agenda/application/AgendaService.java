package org.dnd.timeet.agenda.application;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.agenda.domain.Agenda;
import org.dnd.timeet.agenda.domain.AgendaRepository;
import org.dnd.timeet.agenda.dto.AgendaCreateRequest;
import org.dnd.timeet.common.exception.NotFoundError;
import org.dnd.timeet.common.exception.NotFoundError.ErrorCode;
import org.dnd.timeet.meeting.domain.Meeting;
import org.dnd.timeet.meeting.domain.MeetingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // final 의존성 주입
@Transactional // DB 변경 작업에 사용
public class AgendaService {

    private final MeetingRepository meetingRepository;
    private final AgendaRepository agendaRepository;

    public Agenda createAgenda(Long meetingId, AgendaCreateRequest createDto) {
        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new NotFoundError(ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MeetingId", "Meeting not found")));
        Agenda agenda = createDto.toEntity(meeting);

        return agendaRepository.save(agenda);
    }

    @Transactional(readOnly = true)
    public Agenda findById(Long id) {
        return agendaRepository.findById(id)
            .orElseThrow(() -> new NotFoundError(NotFoundError.ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MeetingId", "Meeting not found")));
    }
}
