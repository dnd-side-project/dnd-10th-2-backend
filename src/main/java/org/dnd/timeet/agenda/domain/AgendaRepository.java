package org.dnd.timeet.agenda.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    List<Agenda> findByMeetingId(Long meetingId);
}
