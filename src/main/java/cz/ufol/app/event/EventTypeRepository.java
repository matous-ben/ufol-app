package cz.ufol.app.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypUdalostiRepository extends JpaRepository<EventType, Long> {
    Optional<EventType> findByKod(String kod);
}
