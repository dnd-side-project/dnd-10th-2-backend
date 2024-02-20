package org.dnd.timeet.meeting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Duration;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.dnd.timeet.common.utils.DurationUtils;
import org.dnd.timeet.meeting.domain.Meeting;

@Schema(description = "회의 정보 응답")
@Getter
@Setter
public class MeetingInfoResponse {

    @Schema(description = "회의 id", example = "12L")
    private Long meetingId;

    @Schema(description = "회의 제목", example = "2차 회의")
    private String title;

    @Schema(description = "회의 공지사항", example = "2개의 사안 모두 해결하기")
    private String description;

    @Schema(description = "회의 상태", example = "SCHEDULED")
    private String meetingStatus;

    @Schema(description = "회의 방장 멤버 ID", example = "13")
    private Long hostMemberId;

    @Schema(description = "회의 시작 일자", example = "2024-01-11T13:20")
    private String startTime;

    @Schema(description = "예상 소요시간", example = "03:00:00")
    private String totalEstimatedDuration;

    @Schema(description = "회의 남은 시간", example = "00:03:00")
    private String remainingTime;

    @Schema(description = "회의 실제 소요 시간", example = "03:03:00")
    private String actualTotalDuration;

    @Schema(description = "썸네일 이미지 번호", example = "1")
    private Integer imgNum;

    @Builder
    public MeetingInfoResponse(Long meetingId, String title, String description, String meetingStatus,
                               Long hostMemberId,
                               String startTime, Duration totalEstimatedDuration, Duration remainingTime,
                               Duration actualTotalDuration, Integer imgNum) {
        this.meetingId = meetingId;
        this.title = title;
        this.description = description;
        this.meetingStatus = meetingStatus;
        this.hostMemberId = hostMemberId;
        this.startTime = startTime;
        this.totalEstimatedDuration = DurationUtils.formatDuration(totalEstimatedDuration);
        this.remainingTime = DurationUtils.formatDuration(remainingTime);
        this.actualTotalDuration = DurationUtils.formatDuration(actualTotalDuration);
        this.imgNum = imgNum;
    }

    public static MeetingInfoResponse from(Meeting meeting) { // 매개변수로부터 객체를 생성하는 팩토리 메서드
        return MeetingInfoResponse.builder()
            .meetingId(meeting.getId())
            .title(meeting.getTitle())
            .description(meeting.getDescription())
            .meetingStatus(meeting.getStatus().name())
            .hostMemberId(meeting.getHostMember() == null ? null : meeting.getHostMember().getId())
            .startTime(meeting.getStartTime().toString())
            .totalEstimatedDuration(meeting.getTotalEstimatedDuration())
            .remainingTime(meeting.calculateRemainingTime())
            .actualTotalDuration(meeting.calculateCurrentDuration())
            .imgNum(meeting.getImgNum())
            .build();
    }
}