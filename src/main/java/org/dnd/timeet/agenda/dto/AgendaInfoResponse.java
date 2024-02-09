package org.dnd.timeet.agenda.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.dnd.timeet.agenda.domain.Agenda;

@Schema(description = "회의 정보 응답")
@Getter
@Setter
public class AgendaInfoResponse {

    @Schema(description = "안건 id", example = "12")
    private Long agendaId;

    @Schema(description = "안건 제목", example = "안건1")
    private String title;

    @Schema(description = "안건 종류", example = "AGENDA")
    private String type;

    @Schema(description = "예상 소요시간", example = "01:00")
    private LocalTime estimatedDuration;

    @Schema(description = "실제 소요시간", example = "01:30")
    private LocalTime actualDuration;

    @Schema(description = "안건 상태", example = "INPROGRESS")
    private String status;

    @Builder
    public AgendaInfoResponse(Long agendaId, String title, String type, LocalTime estimatedDuration, LocalTime actualDuration, String status) {
        this.agendaId = agendaId;
        this.title = title;
        this.type = type;
        this.estimatedDuration = estimatedDuration;
        this.actualDuration = actualDuration;
        this.status = status;
    }


    public static AgendaInfoResponse from(Agenda agenda) { // 매개변수로부터 객체를 생성하는 팩토리 메서드
        return AgendaInfoResponse.builder()
            .agendaId(agenda.getId())
            .title(agenda.getTitle())
            .type(agenda.getType().name())
            .estimatedDuration(agenda.getEstimatedDuration())
            .actualDuration(agenda.getActualDuration())
            .status(agenda.getStatus().name())
            .build();
    }
}