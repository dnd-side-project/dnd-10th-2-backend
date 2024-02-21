package org.dnd.timeet.meeting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.dnd.timeet.common.utils.DurationUtils;
import org.dnd.timeet.meeting.domain.Meeting;

@Schema(description = "회의 정보 응답")
@Getter
@Setter
public class MeetingCurrentDurationResponse {

    @Schema(description = "회의 id", example = "12L")
    private Long meetingId;

    @Schema(description = "회의 상태", example = "SCHEDULED")
    private String meetingStatus;

    @Schema(description = "회의 소요 시간", example = "00:03:00")
    private String currentDuration;

    @Builder
    public MeetingCurrentDurationResponse(Long meetingId, String meetingStatus, String currentDuration) {
        this.meetingId = meetingId;
        this.meetingStatus = meetingStatus;
        this.currentDuration = currentDuration;
    }

    public static MeetingCurrentDurationResponse from(Meeting meeting) { // 매개변수로부터 객체를 생성하는 팩토리 메서드
        return MeetingCurrentDurationResponse
            .builder()
            .meetingId(meeting.getId())
            .meetingStatus(meeting.getStatus().name())
            .currentDuration(DurationUtils.formatDuration(meeting.calculateCurrentDuration()))
            .build();
    }
}