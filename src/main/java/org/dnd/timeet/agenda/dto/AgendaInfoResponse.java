package org.dnd.timeet.agenda.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Duration;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.dnd.timeet.agenda.domain.Agenda;
import org.dnd.timeet.common.utils.DurationUtils;
import org.dnd.timeet.meeting.domain.Meeting;

@Schema(description = "안건 정보 응답")
@Getter
@Setter
public class AgendaInfoResponse {

    @Schema(description = "회의 id", example = "12L")
    private Long meetingId;

    @Schema(description = "회의 남은 시간", example = "00:03:00")
    private String remainingTime;

    private List<AgendaResponse> agendaResponse;

    public AgendaInfoResponse(Meeting meeting, List<Agenda> agendaList) {
        this.meetingId = meeting.getId();
        this.remainingTime = DurationUtils.formatDuration(meeting.calculateRemainingTime());
        this.agendaResponse = agendaList.stream()
            .map(agenda -> new AgendaResponse(agenda, agenda.calculateCurrentDuration(),
                agenda.calculateRemainingTime()))
            .toList();
    }

    @Schema(description = "안건 정보 응답")
    @Getter
    @Setter
    public class AgendaResponse {

        @Schema(description = "안건 id", example = "12")
        private Long agendaId;

        @Schema(description = "안건 제목", example = "안건 1")
        private String title;

        @Schema(description = "안건 종류", example = "AGENDA")
        private String type;

        @Schema(description = "현재까지 소요된 시간", example = "00:36:00")
        private String currentDuration;

        @Schema(description = "남은 시간", example = "00:24:00")
        private String remainingDuration;

        @Schema(description = "안건 상태", example = "INPROGRESS")
        private String status;

        @Schema(description = "안건 순서 번호", example = "1")
        private Integer orderNum;

        public AgendaResponse(Agenda agenda, Duration currentDuration, Duration remainingDuration) {
            this.agendaId = agenda.getId();
            this.title = agenda.getTitle();
            this.type = agenda.getType().name();
            this.currentDuration = DurationUtils.formatDuration(currentDuration);
            this.remainingDuration = DurationUtils.formatDuration(remainingDuration);
            this.status = agenda.getStatus().name();
            this.orderNum = agenda.getOrderNum();
        }
    }
}