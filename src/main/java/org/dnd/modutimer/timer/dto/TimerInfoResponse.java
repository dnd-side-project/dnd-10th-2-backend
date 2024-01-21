package org.dnd.modutimer.timer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.dnd.modutimer.timer.domain.Timer;
import org.dnd.modutimer.timer.domain.TimerStatus;

@Getter
@Setter
public class TimerInfoResponse {
    @Schema(description = "타이머 id", example = "12L")
    private Long id;
    @Schema(description = "타이머 상태", example = "STOPPED")
    private TimerStatus status;
    @Schema(description = "타이머 시작시간", example = "2024-01-11T15:30:45")
    private long startTime;
    @Schema(description = "타이머 종료시간", example = "2024-01-11T20:20:15")
    private long endTime;

    @Builder
    public TimerInfoResponse(
            final Long id,
            final TimerStatus status,
            final long startTime,
            final long endTime
    ) {
        this.id = id;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static TimerInfoResponse from(Timer timer) { // 매개변수로부터 객체를 생성하는 팩토리 메서드
        return TimerInfoResponse.builder()
                .id(timer.getId())
                .status(timer.getStatus())
                .startTime(timer.getDuration().getStartTime())
                .endTime(timer.getDuration().getEndTime())
                .build();
    }
}

