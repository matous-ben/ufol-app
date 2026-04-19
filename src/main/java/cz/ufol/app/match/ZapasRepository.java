package cz.ufol.app.match;

import cz.ufol.app.season.Rocnik;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZapasRepository extends JpaRepository<Zapas, Long> {
    List<Zapas> findByRocnik(Rocnik rocnik);
    List<Zapas> findByRocnikAndOdehranFalseOrderByDatumCasAsc(Rocnik rocnik);
    List<Zapas> findByRocnikAndOdehranTrueOrderByDatumCasDesc(Rocnik rocnik);
    List<Zapas> findTop5ByOdehranTrueOrderByDatumCasDesc();

    List<Zapas> findByRocnikAndOdehranTrue(Rocnik rocnik);

    List<Zapas> findTop3ByOdehranFalseOrderByDatumCasAsc();
}
