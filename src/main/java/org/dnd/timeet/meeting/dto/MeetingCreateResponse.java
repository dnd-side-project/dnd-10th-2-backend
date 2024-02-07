package org.dnd.timeet.meeting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dnd.timeet.meeting.domain.Meeting;


@Schema(description = "회의 생성 응답")
@Getter
@Setter
@NoArgsConstructor
public class MeetingCreateResponse {

    @Schema(description = "회의 id", example = "12L")
    private Long meetingId;

//    @Schema(description = "회의 공유 url", example = "http://localhost:8080/meetings/12L")
//    private String shareUrl;

    @Builder
//    public MeetingCreateResponse(Long meetingId, String shareUrl) {
    public MeetingCreateResponse(Long meetingId) {
        this.meetingId = meetingId;
//        this.shareUrl = shareUrl;
    }

    // 매개변수로부터 객체를 생성하는 팩토리 메서드
    public static MeetingCreateResponse from(Meeting meeting) {
        return MeetingCreateResponse.builder()
            .meetingId(meeting.getId())
//            .shareUrl(link.getUri().toString())
            .build();
    }
}
