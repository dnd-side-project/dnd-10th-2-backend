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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dnd.timeet.common.domain.AuditableEntity;
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
    @Column(nullable = false, name = "extended_duration")
    private LocalTime extendedDuration;

    // 실제 소요 시간
    @Column(nullable = false, name = "actual_duration")
    private LocalTime actualDuration;

    @Column(nullable = false, name = "order_num")
    private Integer orderNum;

    @Enumerated(EnumType.STRING)
    private AgendaStatus status;


    @Builder
    public Agenda(Meeting meeting, String title, AgendaType type, LocalTime estimatedDuration,
                  LocalTime extendedDuration,
                  LocalTime actualDuration, Integer orderNum, AgendaStatus status) {
        this.meeting = meeting;
        this.title = title;
        this.type = type;
        this.estimatedDuration = estimatedDuration;
        this.extendedDuration = extendedDuration;
        this.actualDuration = actualDuration;
        this.orderNum = orderNum;
        this.status = status;
    }

    public void assignToMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

}

