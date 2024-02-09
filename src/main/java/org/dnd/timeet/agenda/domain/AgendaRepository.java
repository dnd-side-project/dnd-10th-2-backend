package org.dnd.timeet.agenda.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {

//    Optional<Timer> findByUserId(Long id);
}
