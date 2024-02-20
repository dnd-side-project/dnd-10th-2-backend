package org.dnd.timeet.participant.domain;


import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dnd.timeet.common.domain.AuditableEntity;
import org.dnd.timeet.meeting.domain.Meeting;
import org.dnd.timeet.member.domain.Member;
import org.hibernate.annotations.Where;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "participant")
@AttributeOverride(name = "id", column = @Column(name = "participant_id"))
@Where(clause = "is_deleted=false")
public class Participant extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Participant(Meeting meeting, Member member) {
        this.meeting = meeting;
        this.member = member;

        meeting.getParticipants().add(this);
        member.getParticipations().add(this);
    }

    public void removeParticipant() {
        if (this.meeting != null) {
            this.meeting.getParticipants().remove(this);
            this.meeting = null;
        }
        if (this.member != null) {
            this.member.getParticipations().remove(this);
            this.member = null;
        }
        this.delete();
    }

}

