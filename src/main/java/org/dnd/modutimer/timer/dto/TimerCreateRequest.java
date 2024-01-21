package org.dnd.modutimer.timer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dnd.modutimer.timer.domain.Duration;
import org.dnd.modutimer.timer.domain.Timer;
import org.dnd.modutimer.timer.domain.TimerStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Getter
@Setter
@NoArgsConstructor
public class TimerCreateRequest {
    @NotNull(message = "시작 시간은 반드시 입력되어야 합니다")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // Format : 2021-12-31T15:30:45
    @Schema(description = "타이머 시작시간", example = "2024-01-11T15:30:45")
    private LocalDateTime startTime;

    @NotNull(message = "종료 시간은 반드시 입력되어야 합니다")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "타이머 종료시간", example = "2024-01-11T20:20:15")
    private LocalDateTime endTime;

    @NotNull(message = "타이머 상태는 반드시 입력되어야 합니다")
    @Schema(description = "타이머 상태", example = "STOPPED")
    private TimerStatus status;

    public Timer toEntity() {
        return Timer.builder()
                .duration(new Duration(startTime.toInstant(ZoneOffset.UTC).toEpochMilli(),
                        endTime.toInstant(ZoneOffset.UTC).toEpochMilli()))
                .status(this.status)
                .build();
    }
}
