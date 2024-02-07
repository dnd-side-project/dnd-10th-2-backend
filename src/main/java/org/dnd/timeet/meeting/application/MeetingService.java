package org.dnd.timeet.meeting.application;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.common.exception.NotFoundError;

import org.dnd.timeet.meeting.domain.Meeting;
import org.dnd.timeet.meeting.domain.MeetingRepository;

import org.dnd.timeet.meeting.dto.MeetingCreateRequest;
import org.dnd.timeet.timer.domain.Timer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // final 의존성 주입
@Transactional // DB 변경 작업에 사용
public class MeetingService {

    private final MeetingRepository meetingRepository;

    public Meeting createMeeting(MeetingCreateRequest createDto) {
        Meeting meeting = createDto.toEntity();
        // 복잡한 비즈니스 로직은 도메인 메서드를 이용하여 Service에서 처리
        return meetingRepository.save(meeting);
    }

    @Transactional(readOnly = true)
    public Meeting findById(Long id) {
        return meetingRepository.findById(id)
            .orElseThrow(() -> new NotFoundError(NotFoundError.ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MeetingId", "Meeting not found")));
    }


}
