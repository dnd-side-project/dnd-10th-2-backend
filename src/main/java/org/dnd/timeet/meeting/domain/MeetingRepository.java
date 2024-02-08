package org.dnd.timeet.meeting.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

//    Optional<Timer> findByUserId(Long id);
}
