package org.dnd.timeet.agenda.application;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.agenda.domain.Agenda;
import org.dnd.timeet.agenda.domain.AgendaRepository;
import org.dnd.timeet.agenda.dto.AgendaCreateRequest;
import org.dnd.timeet.common.exception.NotFoundError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // final 의존성 주입
@Transactional // DB 변경 작업에 사용
public class AgendaService {

    private final AgendaRepository meetingRepository;

    public Agenda createMeeting(AgendaCreateRequest createDto) {
        Agenda meeting = createDto.toEntity();
        // 복잡한 비즈니스 로직은 도메인 메서드를 이용하여 Service에서 처리
        return meetingRepository.save(meeting);
    }

    @Transactional(readOnly = true)
    public Agenda findById(Long id) {
        return meetingRepository.findById(id)
            .orElseThrow(() -> new NotFoundError(NotFoundError.ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MeetingId", "Meeting not found")));
    }


}
