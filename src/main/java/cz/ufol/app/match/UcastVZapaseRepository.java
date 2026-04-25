package cz.ufol.app.match;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import cz.ufol.app.player.Registrace;

public interface UcastVZapaseRepository extends JpaRepository<UcastVZapase, Long> {
    List<UcastVZapase> findByZapas(Zapas zapas);
    void deleteByZapas(Zapas zapas);
    List<UcastVZapase> findByRegistraceIn(List<Registrace> registrace);
    void deleteByRegistraceIn(List<Registrace> registrace);
}
