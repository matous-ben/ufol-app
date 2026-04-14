package cz.ufol.app.player;

import cz.ufol.app.season.Rocnik;
import cz.ufol.app.team.Tym;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistraceRepository extends JpaRepository<Registrace, Long> {
    List<Registrace> findByTymAndRocnik(Tym tym, Rocnik rocnik);
    boolean existsByHracIdAndRocnikId(Long hracId, Long rocnikId);
}
