package org.dnd.timeet.agenda.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.dnd.timeet.agenda.domain.Agenda;
import org.dnd.timeet.common.utils.DurationUtils;

@Schema(description = "안건 정보 응답")
@Getter
@Setter
public class AgendaInfoResponse {

    @Schema(description = "안건 id", example = "12")
    private Long agendaId;

    @Schema(description = "안건 제목", example = "안건 1")
    private String title;

    @Schema(description = "안건 종류", example = "AGENDA")
    private String type;

    @Schema(description = "현재까지 소요된 시간", example = "00:36")
    private String currentDuration;

    @Schema(description = "남은 시간", example = "00:24")
    private String remainingDuration;

    @Schema(description = "안건 상태", example = "INPROGRESS")
    private String status;

    public AgendaInfoResponse(Agenda agenda, Duration currentDuration, Duration remainingDuration) {
        this.agendaId = agenda.getId();
        this.title = agenda.getTitle();
        this.type = agenda.getType().name();
        this.currentDuration = DurationUtils.formatDuration(currentDuration);
        this.remainingDuration = DurationUtils.formatDuration(remainingDuration);
        this.status = agenda.getStatus().name();
    }
}