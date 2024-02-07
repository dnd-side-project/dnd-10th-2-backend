package org.dnd.timeet.meeting.domain;


import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dnd.timeet.common.domain.AuditableEntity;
import org.dnd.timeet.common.exception.BadRequestError;
import org.hibernate.annotations.Where;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "meeting")
@AttributeOverride(name = "id", column = @Column(name = "meeting_id"))
@Where(clause = "is_deleted=false")
public class Meeting extends AuditableEntity {

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false)
    private LocalDateTime startTime;

    // 종료시간, 초기값 null
    private LocalDateTime endTime;

    // 예상 소요 시간
    @Column(nullable = false)
    private LocalTime totalEstimatedDuration;

    // 실제 소요 시간, 초기값 null
    private LocalTime totalActualDuration;

    @Column(nullable = true, length = 255)
    private String location;

    @Column(nullable = true, length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MeetingStatus status = MeetingStatus.SCHEDULED;

    @Column(nullable = false)
    private Integer imgNum;

    @Builder
    public Meeting(String title, LocalDateTime startTime, LocalTime totalEstimatedDuration, String location,
        String description, Integer imgNum) {
        this.title = title;
        this.startTime = startTime;
        this.totalEstimatedDuration = totalEstimatedDuration;
        this.location = location;
        this.description = description;
        this.imgNum = imgNum;
    }


    // 회의를 시작하는 메서드
    public void startMeeting() {
        this.status = MeetingStatus.INPROGRESS;
    }

    // 회의를 종료하는 메서드
    public void endMeeting() {
        this.endTime = LocalDateTime.now();
        this.status = MeetingStatus.COMPLETED;

        long durationInSeconds = Duration.between(startTime, endTime).getSeconds();
        this.totalActualDuration = LocalTime.ofSecondOfDay(durationInSeconds);
    }

    // 회의를 취소하는 메서드
    public void cancelMeeting() {
        this.status = MeetingStatus.CANCELED;
        this.delete();
    }

    // 회의 시작 시간 수정하는 메서드
    public void updateStartTime(LocalDateTime startTime) {
        if (this.status != MeetingStatus.SCHEDULED) {
            throw new BadRequestError(BadRequestError.ErrorCode.WRONG_REQUEST_TRANSMISSION,
                Collections.singletonMap("Meeting", "MeetingStatus is not SCHEDULED"));
        }
        this.startTime = startTime;
    }
}

