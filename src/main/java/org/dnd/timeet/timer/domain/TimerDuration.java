package org.dnd.timeet.timer.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimerDuration { // VO(값 객체)

    private long startTime;
    private long endTime;

    public TimerDuration(long startTime, long endTime) {
        if (endTime < startTime) {
            throw new IllegalArgumentException("종료 시간은 시작 시간보다 뒤여야 합니다.");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long toMillis() {
        return endTime - startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimerDuration)) {
            return false;
        }
        TimerDuration timerDuration = (TimerDuration) o;
        return startTime == timerDuration.startTime && endTime == timerDuration.endTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime);
    }

    @Override
    public String toString() {
        return "Duration{" +
            "startTime=" + startTime +
            ", endTime=" + endTime +
            ", durationInMillis=" + toMillis() +
            '}';
    }
}

