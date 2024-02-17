package org.dnd.timeet.timer.domain;


import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dnd.timeet.common.domain.AuditableEntity;
import org.hibernate.annotations.Where;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "timer")
@AttributeOverride(name = "id", column = @Column(name = "timer_id"))
@Where(clause = "is_deleted=false") // 삭제가 되지 않는 것만 조회
public class Timer extends AuditableEntity {

    @Enumerated(EnumType.STRING)
    private TimerStatus status;

    @Embedded
    @Column(nullable = false, name = "timer_duration")
    private TimerDuration timerDuration;

    @Builder
    public Timer(TimerDuration timerDuration, TimerStatus status) {
        this.timerDuration = timerDuration;
        this.status = status;
    }

    public void startTimer() {
        if (this.status == TimerStatus.STOPPED) {
            long startTime = System.currentTimeMillis();
            this.timerDuration = new TimerDuration(startTime, startTime); // 시작 시간과 종료 시간을 동일하게 설정
            this.status = TimerStatus.RUNNING;
        } else {
            // 이미 실행 중인 경우 처리 (예외 던지기 or 로깅)
        }
    }

    public void stopTimer() {
        if (this.status == TimerStatus.RUNNING) {
            long endTime = System.currentTimeMillis();
            this.timerDuration = new TimerDuration(this.timerDuration.getStartTime(), endTime); // 종료 시간 업데이트
            this.status = TimerStatus.STOPPED;
        } else {
            // 이미 멈춘 경우 처리 (예외 던지기 or 로깅)
        }
    }

    public void changeDuration(TimerDuration newTimerDuration) {
        if (this.status == TimerStatus.STOPPED) { // 타이머가 멈췄을 경우에만 지속시간 변경 가능
            this.timerDuration = newTimerDuration;
        } else {
            // 이미 실행 중인 경우 변경 불가능 처리 (예외 던지기 or 로깅)
        }
    }
}

