package org.dnd.timeet.agenda.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dnd.timeet.agenda.domain.Agenda;
import org.dnd.timeet.agenda.domain.AgendaType;
import org.dnd.timeet.meeting.domain.Meeting;
import org.springframework.format.annotation.DateTimeFormat;


@Schema(description = "안건 생성 요청")
@Getter
@Setter
@NoArgsConstructor
public class AgendaCreateRequest {

    @NotNull(message = "안건 제목은 반드시 입력되어야 합니다")
    @Schema(description = "안건 제목", example = "브레인스토밍")
    private String title;

    @NotNull(message = "안건 타입은 반드시 입력되어야 합니다")
    @Schema(description = "AGENDA | BREAK", example = "AGENDA")
    private String type;

    @NotNull(message = "안건 소요 시간은 반드시 입력되어야 합니다")
    @DateTimeFormat(pattern = "HH:mm")
    @Schema(description = "안건 소요 시간", example = "01:20")
    private LocalTime estimatedDuration;

    @NotNull(message = "안건 순서는 반드시 입력되어야 합니다")
    @Schema(description = "안건 순서", example = "1")
    private Integer orderNum;

    public Agenda toEntity(Meeting meeting) {
        return Agenda.builder()
            .meeting(meeting)
            .title(this.title)
            .type(this.type.equals("AGENDA") ? AgendaType.AGENDA : AgendaType.BREAK)
            .estimatedDuration(this.estimatedDuration)
            .orderNum(this.orderNum)
            .build();
    }
}
