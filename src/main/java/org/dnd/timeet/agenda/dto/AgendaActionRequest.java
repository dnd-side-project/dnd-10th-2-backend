package org.dnd.timeet.agenda.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "안건 제어 요청")
@Getter
@Setter
@NoArgsConstructor
public class AgendaActionRequest {

    private String action;
    private String modifiedDuration;

}
