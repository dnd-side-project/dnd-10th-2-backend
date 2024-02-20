package org.dnd.timeet.agenda.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.dnd.timeet.agenda.domain.Agenda;
import org.dnd.timeet.common.utils.DurationUtils;

@Schema(description = "안건 수정 응답")
@Getter
@Setter
public class AgendaPatchResponse {

    @Schema(description = "안건 id", example = "12")
    private Long agendaId;

    @Schema(description = "안건 제목", example = "안건 1")
    private String title;

    @NotNull(message = "안건 소요 시간은 반드시 입력되어야 합니다")
    @Schema(description = "안건 소요 시간", example = "01:20:00")
    private String allocatedDuration;

    public AgendaPatchResponse(Agenda agenda) {
        this.agendaId = agenda.getId();
        this.title = agenda.getTitle();
        this.allocatedDuration = DurationUtils.formatDuration(agenda.getAllocatedDuration());
    }

}