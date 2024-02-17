package org.dnd.timeet.timer.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TimerRepository extends JpaRepository<Timer, Long> {

//    Optional<Timer> findByUserId(Long id);
}
