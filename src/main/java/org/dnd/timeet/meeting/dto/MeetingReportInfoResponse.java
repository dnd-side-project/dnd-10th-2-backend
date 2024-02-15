package org.dnd.timeet.meeting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.dnd.timeet.agenda.dto.AgendaReportInfoResponse;

@Schema(description = "회의 리포트 응답")
@Getter
@Setter
public class MeetingReportInfoResponse {

    @Schema(description = "소요 시간 차이 (실제 소요 시간 - 예상 소요 시간)", example = "+01:30")
    private String totalDiff;

    @Schema(description = "안건 정보")
    private List<AgendaReportInfoResponse> agendas;

    //TODO: 회의 메모 추가
    @Schema(description = "회의 메모", example = "회의 내용 메모")
    private String memos;

    @Builder
    public MeetingReportInfoResponse(String totalDiff, List<AgendaReportInfoResponse> agendas, String memos) {
        this.totalDiff = totalDiff;
        this.agendas = agendas;
        this.memos = memos;
    }
}
