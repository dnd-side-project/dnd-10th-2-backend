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
import java.time.LocalTime;
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

    // 예상 소요 시간
    @Column(nullable = false, name = "estimated_duration")
    private LocalTime estimatedDuration;

    // 연장된 총 시간
    @Column(nullable = true, name = "extended_duration")
    private LocalTime extendedDuration;

    // 실제 소요 시간
    @Column(nullable = true, name = "actual_duration")
    private LocalTime actualDuration;

    @Column(nullable = false, name = "order_num")
    private Integer orderNum;

    @Enumerated(EnumType.STRING)
    private AgendaStatus status = AgendaStatus.PENDING;


    @Builder
    public Agenda(Long id, Meeting meeting, String title, AgendaType type, LocalTime estimatedDuration,
                  Integer orderNum, AgendaStatus status) {
        this.id = id;
        this.meeting = meeting;
        this.title = title;
        this.type = type;
        this.estimatedDuration = estimatedDuration;
        this.orderNum = orderNum;
        this.status = status;
    }

    public void start() {
        if (this.status != AgendaStatus.PENDING) {
            throw new BadRequestError(BadRequestError.ErrorCode.WRONG_REQUEST_TRANSMISSION,
                Collections.singletonMap("AgendaStatus", "Agenda can only be started from PENDING status"));
        }
        this.status = AgendaStatus.INPROGRESS;
    }

    public void pause() {
        if (this.status != AgendaStatus.INPROGRESS) {
            throw new BadRequestError(BadRequestError.ErrorCode.WRONG_REQUEST_TRANSMISSION,
                Collections.singletonMap("AgendaStatus", "Agenda can only be paused from INPROGRESS status."));
        }
        this.status = AgendaStatus.PAUSED;
    }

    public void resume() {
        if (this.status != AgendaStatus.PAUSED || this.status != AgendaStatus.COMPLETED){
            throw new BadRequestError(BadRequestError.ErrorCode.WRONG_REQUEST_TRANSMISSION,
                Collections.singletonMap("AgendaStatus", "Agenda can only be resumed from PAUSED status."));
        }
        this.status = AgendaStatus.INPROGRESS;
    }

    public void extendDuration(LocalTime extension) {
        if (this.extendedDuration == null) {
            this.extendedDuration = extension;
        } else {
            this.extendedDuration = this.extendedDuration.plusHours(extension.getHour())
                .plusMinutes(extension.getMinute());
        }
    }

    public void complete() {
        if (this.status == AgendaStatus.COMPLETED) {
            throw new BadRequestError(BadRequestError.ErrorCode.WRONG_REQUEST_TRANSMISSION,
                Collections.singletonMap("AgendaStatus", "Agenda is already completed."));
        }
        this.status = AgendaStatus.COMPLETED;
        this.actualDuration = calculateActualDuration(); // 실제 소요 시간 계산
    }

    private LocalTime calculateActualDuration() {
        if (this.extendedDuration != null) {
            return this.estimatedDuration.plusHours(this.extendedDuration.getHour())
                .plusMinutes(this.extendedDuration.getMinute());
        }
        return this.estimatedDuration;
    }

}

