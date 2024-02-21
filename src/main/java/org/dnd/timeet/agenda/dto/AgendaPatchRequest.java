package org.dnd.timeet.agenda.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Schema(description = "안건 수정 요청")
@Getter
@Setter
@NoArgsConstructor
public class AgendaPatchRequest {

    @Schema(description = "안건 제목", example = "브레인스토밍")
    private String title;

    @DateTimeFormat(pattern = "HH:mm:ss")
    @Schema(description = "안건 소요 시간", example = "01:20:00")
    private LocalTime allocatedDuration;
}
