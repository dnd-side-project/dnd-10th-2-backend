package org.dnd.timeet.participant.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Optional<Participant> findByMeetingIdAndMemberId(Long meetingId, Long memberId);

    boolean existsByMeetingIdAndMemberId(Long meetingId, Long memberId);
}
