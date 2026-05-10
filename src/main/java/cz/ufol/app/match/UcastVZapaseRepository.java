package cz.ufol.app.match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

import cz.ufol.app.player.Registrace;

public interface UcastVZapaseRepository extends JpaRepository<UcastVZapase, Long> {
    @EntityGraph(attributePaths = {"registrace", "registrace.hrac", "zapas"})
    List<UcastVZapase> findByZapas(Zapas zapas);
    void deleteByZapas(Zapas zapas);
    @EntityGraph(attributePaths = {"registrace", "registrace.hrac", "zapas"})
    List<UcastVZapase> findByRegistraceIn(List<Registrace> registrace);
    void deleteByRegistraceIn(List<Registrace> registrace);
}
