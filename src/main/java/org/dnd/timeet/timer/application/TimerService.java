package org.dnd.timeet.timer.application;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.common.exception.NotFoundError;
import org.dnd.timeet.timer.domain.TimerDuration;
import org.dnd.timeet.timer.domain.Timer;
import org.dnd.timeet.timer.domain.TimerRepository;
import org.dnd.timeet.timer.dto.TimerCreateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // final 의존성 주입
@Transactional // DB 변경 작업에 사용
public class TimerService {

    private final TimerRepository timerRepository;

    public Timer createTimer(TimerCreateRequest createDto) {
        Timer timer = createDto.toEntity(); // DTO : Entity 객체로 변환하는 역할
        // 복잡한 비즈니스 로직은 Service에서 처리(도메인 이용하여)
        return timerRepository.save(timer);
    }

    @Transactional(readOnly = true)
    public Timer findById(Long id) {
        return timerRepository.findById(id)
            .orElseThrow(() -> new NotFoundError(NotFoundError.ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("TimerId", "Timer not found")));
    }

    @Transactional(readOnly = true)
    public List<Timer> findAll() { // TODO : User에 따라 타이머 조회하도록 변경하기
        return timerRepository.findAll();
    }

    public void startTimer(Long timerId) {
        Timer timer = findById(timerId);
        timer.startTimer(); // 도메인 영역에서 처리(DDD)
    }

    public void stopTimer(Long timerId) {
        Timer timer = findById(timerId);
        timer.stopTimer();
    }

    public void changeDuration(Long timerId, TimerDuration newTimerDuration) {
        Timer timer = findById(timerId);
        timer.changeDuration(newTimerDuration);
    }

    public void deleteTimer(Long timerId) {
        Timer timer = timerRepository.findById(timerId)
            .orElseThrow(() ->
                new NotFoundError(NotFoundError.ErrorCode.RESOURCE_NOT_FOUND,
                    Collections.singletonMap("TimerId", "Timer not found")));
        timer.delete(); // soft 삭제 로직 호출
    }
}
