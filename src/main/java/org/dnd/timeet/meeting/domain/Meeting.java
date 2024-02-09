package org.dnd.timeet.meeting.domain;


import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dnd.timeet.agenda.domain.Agenda;
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

    @Column(nullable = false, name = "start_time")
    private LocalDateTime startTime;

    // 종료시간, 초기값 null
    @Column(name = "end_time")
    private LocalDateTime endTime;

    // 예상 소요 시간
    @Column(nullable = false, name = "total_estimated_duration")
    private LocalTime totalEstimatedDuration;

    // 실제 소요 시간, 초기값 null
    @Column(name = "total_actual_duration")
    private LocalTime totalActualDuration;

    @Column(nullable = true, length = 255)
    private String location;

    @Column(nullable = true, length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MeetingStatus status = MeetingStatus.SCHEDULED;

    @Column(nullable = false, name = "img_num")
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


    public void startMeeting() {
        this.status = MeetingStatus.INPROGRESS;
    }

    public void endMeeting() {
        this.endTime = LocalDateTime.now();
        this.status = MeetingStatus.COMPLETED;

        long durationInSeconds = Duration.between(startTime, endTime).getSeconds();
        this.totalActualDuration = LocalTime.ofSecondOfDay(durationInSeconds);
    }

    public void cancelMeeting() {
        this.status = MeetingStatus.CANCELED;
        this.delete();
    }

    public void updateStartTime(LocalDateTime startTime) {
        if (this.status != MeetingStatus.SCHEDULED) {
            throw new BadRequestError(BadRequestError.ErrorCode.WRONG_REQUEST_TRANSMISSION,
                Collections.singletonMap("Meeting", "MeetingStatus is not SCHEDULED"));
        }
        this.startTime = startTime;
    }

}

