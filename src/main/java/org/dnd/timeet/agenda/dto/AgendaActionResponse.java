package org.dnd.timeet.agenda.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dnd.timeet.agenda.domain.AgendaStatus;

@Schema(description = "안건 제어 응답")
@Getter
@Setter
public class AgendaActionResponse {
    private Long agendaId;
    private AgendaStatus status;

    public AgendaActionResponse(Long agendaId, AgendaStatus status) {
        this.agendaId = agendaId;
        this.status = status;
    }

}
