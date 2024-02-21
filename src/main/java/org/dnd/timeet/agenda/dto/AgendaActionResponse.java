package org.dnd.timeet.agenda.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.dnd.timeet.agenda.domain.Agenda;
import org.dnd.timeet.agenda.domain.AgendaStatus;
import org.dnd.timeet.agenda.domain.AgendaType;
import org.dnd.timeet.common.utils.DateTimeUtils;
import org.dnd.timeet.common.utils.DurationUtils;

@Schema(description = "안건 제어 응답")
@Getter
@Setter
public class AgendaActionResponse {

    private Long agendaId;
    private AgendaStatus status;
    private String title;
    private AgendaType type;

    private String currentDuration; // 현재까지 진행된 시간
    private String remainingDuration; // 남은 시간

    private String timestamp; // 수정 시간

    public AgendaActionResponse(Agenda agenda, Duration currentDuration, Duration remainingDuration) {
        this.agendaId = agenda.getId();
        this.status = agenda.getStatus();
        this.title = agenda.getTitle();
        this.type = agenda.getType();

        this.currentDuration = DurationUtils.formatDuration(currentDuration);
        this.remainingDuration = DurationUtils.formatDuration(remainingDuration);
        this.timestamp = DateTimeUtils.formatLocalDateTime(LocalDateTime.now());
    }

}
