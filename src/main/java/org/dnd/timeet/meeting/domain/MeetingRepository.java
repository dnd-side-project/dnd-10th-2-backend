package org.dnd.timeet.meeting.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    @Query("select m from Meeting m left join fetch m.participants p left join fetch p.member where m.id = :meetingId")
    Optional<Meeting> findByIdWithParticipantsAndMembers(@Param("meetingId") Long meetingId);

    @Query("SELECT m FROM Meeting m WHERE m.status = 'INPROGRESS'")
    List<Meeting> findMeetingsByStatusInProgress();
}
