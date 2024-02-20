package org.dnd.timeet.agenda.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "안건 순서 변경 요청")
@Getter
@Setter
@NoArgsConstructor
public class AgendaOrderRequest {

    @Schema(description = "안건 ID 리스트", example = "[2,1,3]")
    private List<Long> agendaIds;

}
