package org.dnd.timeet.agenda.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    List<Agenda> findByMeetingId(Long meetingId);

    Optional<Agenda> findByIdAndMeetingId(Long agendaId, Long meetingId);

    boolean existsByMeetingIdAndOrderNum(Long meetingId, Integer orderNum);

    boolean existsByMeetingIdAndOrderNumAndStatus(Long meetingId, Integer orderNum, AgendaStatus status);

}
