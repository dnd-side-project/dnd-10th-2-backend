package org.dnd.timeet.meeting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.dnd.timeet.meeting.domain.Meeting;
import org.dnd.timeet.timer.domain.Timer;
import org.dnd.timeet.timer.domain.TimerStatus;

@Schema(description = "회의 정보 응답")
@Getter
@Setter
public class MeetingInfoResponse {

    @Schema(description = "회의 id", example = "12L")
    private Long meetingId;

    @Schema(description = "회의 제목", example = "2차 회의")
    private String title;

    @Schema(description = "회의 목", example = "2개의 사안 모두 해결하기")
    private String description;

    @Builder
    public MeetingInfoResponse(Long meetingId, String title, String description) {
        this.meetingId = meetingId;
        this.title = title;
        this.description = description;
    }


    public static MeetingInfoResponse from(Meeting meeting) { // 매개변수로부터 객체를 생성하는 팩토리 메서드
        return MeetingInfoResponse.builder()
            .meetingId(meeting.getId())
            .title(meeting.getTitle())
            .description(meeting.getDescription())
            .build();
    }
}