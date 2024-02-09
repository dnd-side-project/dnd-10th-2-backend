package org.dnd.timeet.meeting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dnd.timeet.meeting.domain.Meeting;
import org.dnd.timeet.member.domain.Member;
import org.springframework.format.annotation.DateTimeFormat;


@Schema(description = "회의 생성 요청")
@Getter
@Setter
@NoArgsConstructor
public class MeetingCreateRequest {

    @NotNull(message = "회의 제목은 반드시 입력되어야 합니다")
    @Schema(description = "회의 제목", example = "2차 업무 회의")
    private String title;

    @Schema(description = "회의 장소", example = "스타벅스 강남역점")
    private String location;

    @NotNull(message = "회의 시작 시간은 반드시 입력되어야 합니다")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Schema(description = "회의 시작 시간", example = "2024-01-11T13:20")
    private LocalDateTime startTime;

    @Schema(description = "회의 목표", example = "2개의 사안 모두 해결하기")
    private String description;

    @NotNull(message = "예상 소요 시간은 반드시 입력되어야 합니다")
    @DateTimeFormat(pattern = "HH:mm")
    @Schema(description = "예상 소요 시간", example = "02:00")
    private LocalTime estimatedTotalDuration;

    @NotNull(message = "썸네일은 반드시 입력되어야 합니다")
    @Schema(description = "썸네일 이미지 번호", example = "1")
    private Integer imageNum;

    public Meeting toEntity(Member member) {
        return Meeting.builder()
            .hostMember(member)
            .title(this.title)
            .location(this.location)
            .startTime(startTime)
            .description(this.description)
            .totalEstimatedDuration(this.estimatedTotalDuration)
            .imgNum(imageNum)
            .build();
    }
}
