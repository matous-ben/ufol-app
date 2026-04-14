package cz.ufol.app.match;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UcastVZapaseRepository extends JpaRepository<UcastVZapase, Long> {
    List<UcastVZapase> findByZapas(Zapas zapas);
}
