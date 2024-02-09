package org.dnd.timeet.participant.domain;

import org.dnd.timeet.participant.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

//    Optional<Timer> findByUserId(Long id);
}
