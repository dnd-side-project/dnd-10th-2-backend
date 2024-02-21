package org.dnd.timeet.agenda.domain;


import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dnd.timeet.common.domain.AuditableEntity;
import org.dnd.timeet.common.exception.BadRequestError;
import org.dnd.timeet.meeting.domain.Meeting;
import org.hibernate.annotations.Where;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "agenda")
@AttributeOverride(name = "id", column = @Column(name = "agenda_id"))
@Where(clause = "is_deleted=false")
public class Agenda extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY) //외래키
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @Column(nullable = false, length = 255)
    private String title;

    @Enumerated(EnumType.STRING)
    private AgendaType type;

    // 할당된 총 시간
    @Column(name = "allocated_duration", nullable = false)
    private Duration allocatedDuration = Duration.ZERO;

    // 안건 시작 시간
    @Column(name = "start_time")
    private LocalDateTime startTime;

    // 안건 일시정지 시간
    @Column(name = "pause_time")
    private LocalDateTime pauseTime;

    // 일시정지된 시간 누적
    @Column(name = "paused_duration")
    private Duration pausedDuration = Duration.ZERO;

    // 안건 총 소요시간
    @Column(name = "total_duration")
    private Duration totalDuration;

    @Column(nullable = false, name = "order_num")
    private Integer orderNum;

    @Enumerated(EnumType.STRING)
    private AgendaStatus status = AgendaStatus.PENDING;

    @Builder
    public Agenda(Meeting meeting, String title, AgendaType type, Duration allocatedDuration, Integer orderNum) {
        this.meeting = meeting;
        this.title = title;
        this.type = type;
        this.allocatedDuration = allocatedDuration;
        this.orderNum = orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public void start() {
        validateTransition(AgendaStatus.PENDING);
        this.startTime = LocalDateTime.now();
        this.status = AgendaStatus.INPROGRESS;
    }

    public void pause() {
        validateTransition(AgendaStatus.INPROGRESS);
        this.pauseTime = LocalDateTime.now(); // 일시정지 시간 설정
        this.status = AgendaStatus.PAUSED;
    }

    public void resume() {
        validateTransition(AgendaStatus.PAUSED);
        // 일시정지된 시간 누적
        this.pausedDuration = this.pausedDuration.plus(Duration.between(pauseTime, LocalDateTime.now()));
        this.pauseTime = null;
        this.status = AgendaStatus.INPROGRESS;
    }

    public void extendDuration(Duration extension) {
        if (this.status != AgendaStatus.COMPLETED) {
            this.allocatedDuration = this.allocatedDuration.plus(extension);
        }
    }

    public void complete() {
        validateTransition(AgendaStatus.INPROGRESS, AgendaStatus.PAUSED);

        this.totalDuration = calculateCurrentDuration();
        this.status = AgendaStatus.COMPLETED;
    }

    // 현재까지 진행된 시간 계산
    public Duration calculateCurrentDuration() {
        LocalDateTime now = LocalDateTime.now();

        if (this.status == AgendaStatus.PENDING || this.status == AgendaStatus.CANCELED) {
            return Duration.ZERO;
        } else if (this.status == AgendaStatus.COMPLETED) {
            return this.totalDuration;
        } else { // INPROGRESS 또는 PAUSED 상태
            Duration passedTime;
            // PAUSED 상태라면, 마지막 일시정지 시간부터 현재까지의 시간을 뺀다
            if (this.status == AgendaStatus.PAUSED && this.pauseTime != null) {
                passedTime = Duration.between(this.startTime, this.pauseTime).minus(this.pausedDuration);
            } else { // INPROGRESS 상태라면, 시작 시간부터 현재까지의 시간을 뺀다
                passedTime = Duration.between(this.startTime, now).minus(this.pausedDuration);
            }
            return passedTime;
        }
    }

    // 남은 시간 계산 메서드
    public Duration calculateRemainingTime() {
        if (this.status == AgendaStatus.PENDING) {
            return this.allocatedDuration;
        } else if (this.status == AgendaStatus.COMPLETED || this.status == AgendaStatus.CANCELED) {
            return Duration.ZERO;
        } else { // INPROGRESS 또는 PAUSED 상태
            // 현재까지 진행된 시간 계산
            Duration passedTime = calculateCurrentDuration();
            // 할당된 총 시간에서 현재까지 진행된 시간을 뺀다
            return this.allocatedDuration.minus(passedTime);
        }
    }

    private void validateTransition(AgendaStatus... validPreviousStatuses) {
        for (AgendaStatus validStatus : validPreviousStatuses) {
            if (this.status == validStatus) {
                return;
            }
        }
        throw new BadRequestError(BadRequestError.ErrorCode.WRONG_REQUEST_TRANSMISSION,
            Collections.singletonMap("AgendaStatus", "Invalid status transition"));
    }

    public void cancel() {
        this.status = AgendaStatus.CANCELED;
        this.delete();
    }

    public void update(String title, Duration allocatedDuration) {
        this.title = title;
        this.allocatedDuration = allocatedDuration;
    }

}

