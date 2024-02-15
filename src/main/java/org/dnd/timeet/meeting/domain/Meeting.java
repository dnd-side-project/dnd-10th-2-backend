package org.dnd.timeet.meeting.domain;


import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dnd.timeet.common.domain.AuditableEntity;
import org.dnd.timeet.common.exception.BadRequestError;
import org.dnd.timeet.common.exception.BadRequestError.ErrorCode;
import org.dnd.timeet.member.domain.Member;
import org.dnd.timeet.participant.domain.Participant;
import org.hibernate.annotations.Where;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "meeting")
@AttributeOverride(name = "id", column = @Column(name = "meeting_id"))
@Where(clause = "is_deleted=false")
public class Meeting extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_member_id")
    private Member hostMember; // 방장

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, name = "start_time")
    private LocalDateTime startTime;

    // 종료시간, 초기값 null
    @Column(name = "end_time")
    private LocalDateTime endTime;

    // 예상 소요 시간
    @Column(nullable = false, name = "total_estimated_duration")
    private Duration totalEstimatedDuration;

    // 실제 소요 시간, 초기값 null
    @Column(name = "total_actual_duration")
    private Duration totalActualDuration;

    @Column(nullable = true, length = 255)
    private String location;

    @Column(nullable = true, length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MeetingStatus status = MeetingStatus.SCHEDULED;

    @Column(nullable = false, name = "img_num")
    private Integer imgNum;

    @OneToMany(mappedBy = "meeting", fetch = FetchType.EAGER)
    private Set<Participant> participants = new HashSet<>();

    @Builder
    public Meeting(Member hostMember, String title, LocalDateTime startTime, Duration totalEstimatedDuration,
                   String location, String description, Integer imgNum) {
        this.hostMember = hostMember;
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

        if (this.status == MeetingStatus.COMPLETED) {
            throw new BadRequestError(ErrorCode.WRONG_REQUEST_TRANSMISSION,
                Collections.singletonMap("MeetingId", "Meeting already completed"));
        }

        this.status = MeetingStatus.COMPLETED;

        Duration duration = Duration.between(startTime, endTime);

        if (duration.getSeconds() > 24 * 3600) { // 하루를 초과하는 경우
            throw new BadRequestError(ErrorCode.WRONG_REQUEST_TRANSMISSION,
                Collections.singletonMap("Meeting", "Meeting duration exceeds one day"));
        }

        this.totalActualDuration = duration;
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

    public void assignHostMember(Member hostMember) {
        this.hostMember = hostMember;
    }

    // 새 방장을 랜덤으로 지정하는 메서드
    public void assignNewHostRandomly() {
        if (this.participants.isEmpty()) {
            this.hostMember = null; // 참가자가 없을 경우 방장도 없음
            return;
        }

        List<Member> participantsList = this.participants.stream()
            .map(Participant::getMember)
            .collect(Collectors.toList());

        // 랜덤 객체를 사용하여 참가자 목록에서 랜덤하게 하나를 선택
        Random random = new Random();
        int index = random.nextInt(participantsList.size());
        Member newHost = participantsList.get(index);

        // 새로운 방장 지정
        this.assignHostMember(newHost);
    }

    // 현재까지 진행된 시간 계산
    public Duration calculateCurrentDuration() {
        switch (this.status) {
            case SCHEDULED:
                return Duration.ZERO;
            case INPROGRESS:
                return Duration.between(this.startTime, LocalDateTime.now());
            case COMPLETED:
                return this.totalActualDuration;
            case CANCELED:
                throw new BadRequestError(ErrorCode.WRONG_REQUEST_TRANSMISSION,
                    Collections.singletonMap("Meeting", "Meeting is CANCELED"));
            default:
                throw new BadRequestError(ErrorCode.WRONG_REQUEST_TRANSMISSION,
                    Collections.singletonMap("Meeting", "MeetingStatus is not valid"));
        }
    }

    // 남은 시간 계산 메서드
    public Duration calculateRemainingTime() {
        switch (this.status) {
            case SCHEDULED:
                return this.totalEstimatedDuration;
            case INPROGRESS:
                return this.totalEstimatedDuration.minus(calculateCurrentDuration());
            case COMPLETED:
                return Duration.ZERO;
            case CANCELED:
                throw new BadRequestError(ErrorCode.WRONG_REQUEST_TRANSMISSION,
                    Collections.singletonMap("Meeting", "Meeting is CANCELED"));
            default:
                throw new BadRequestError(ErrorCode.WRONG_REQUEST_TRANSMISSION,
                    Collections.singletonMap("Meeting", "MeetingStatus is not valid"));
        }
    }

}

