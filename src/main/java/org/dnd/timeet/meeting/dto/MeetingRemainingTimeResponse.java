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
public class MeetingRemainingTimeResponse {

    @Schema(description = "회의 id", example = "12L")
    private Long meetingId;

    @Schema(description = "회의 상태", example = "SCHEDULED")
    private String meetingStatus;

    @Schema(description = "회의 남은 시간", example = "00:03:00")
    private String remainingTime;

    @Builder
    public MeetingRemainingTimeResponse(Long meetingId, String meetingStatus, String remainingTime) {
        this.meetingId = meetingId;
        this.meetingStatus = meetingStatus;
        this.remainingTime = remainingTime;
    }

    public static MeetingRemainingTimeResponse from(Meeting meeting) { // 매개변수로부터 객체를 생성하는 팩토리 메서드
        return MeetingRemainingTimeResponse
            .builder()
            .meetingId(meeting.getId())
            .meetingStatus(meeting.getStatus().name())
            .remainingTime(DurationUtils.formatDuration(meeting.calculateRemainingTime()))
            .build();
    }
}