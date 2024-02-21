package org.dnd.timeet.agenda.dto;

import static org.dnd.timeet.common.utils.TimeUtils.calculateTimeDiff;
import static org.dnd.timeet.common.utils.TimeUtils.formatDuration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.dnd.timeet.agenda.domain.Agenda;

@Schema(description = "회의 정보 응답 (리포트 생성용)")
@Getter
@Setter
public class AgendaReportInfoResponse {

    @Schema(description = "안건 id", example = "12")
    private Long agendaId;

    @Schema(description = "안건 제목", example = "안건1")
    private String title;

    @Schema(description = "소요 시간 차이 (실제 소요 시간 - 예상 소요 시간)", example = "+01:30:00")
    private String diff;


    @Builder
    public AgendaReportInfoResponse(Long agendaId, String title,
                                    String diff) {
        this.agendaId = agendaId;
        this.title = title;
        this.diff = diff;
    }


    public static AgendaReportInfoResponse from(Agenda agenda) { // 매개변수로부터 객체를 생성하는 팩토리 메서드
        return AgendaReportInfoResponse.builder()
            .agendaId(agenda.getId())
            .title(agenda.getTitle())
            .diff(formatDuration(calculateTimeDiff(agenda.getTotalDuration(), agenda.getAllocatedDuration())))
            .build();
    }
}