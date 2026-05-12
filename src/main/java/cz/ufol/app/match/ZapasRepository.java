package cz.ufol.app.match;

import cz.ufol.app.season.Rocnik;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ZapasRepository extends JpaRepository<Zapas, Long> {
    @Override
    @EntityGraph(attributePaths = {"rocnik", "domaciTym", "domaciTym.univerzita", "hosteTym", "hosteTym.univerzita", "mistoKonani"})
    List<Zapas> findAll();

    @Override
    @EntityGraph(attributePaths = {"rocnik", "domaciTym", "domaciTym.univerzita", "hosteTym", "hosteTym.univerzita", "mistoKonani"})
    Optional<Zapas> findById(Long id);

    @EntityGraph(attributePaths = {"rocnik", "domaciTym", "domaciTym.univerzita", "hosteTym", "hosteTym.univerzita", "mistoKonani"})
    List<Zapas> findByRocnik(Rocnik rocnik);

    @EntityGraph(attributePaths = {"rocnik", "domaciTym", "domaciTym.univerzita", "hosteTym", "hosteTym.univerzita", "mistoKonani"})
    List<Zapas> findByRocnikAndOdehranFalseOrderByDatumCasAsc(Rocnik rocnik);

    @EntityGraph(attributePaths = {"rocnik", "domaciTym", "domaciTym.univerzita", "hosteTym", "hosteTym.univerzita", "mistoKonani"})
    List<Zapas> findByRocnikAndOdehranTrueOrderByDatumCasDesc(Rocnik rocnik);

    @EntityGraph(attributePaths = {"rocnik", "domaciTym", "domaciTym.univerzita", "hosteTym", "hosteTym.univerzita", "mistoKonani"})
    List<Zapas> findTop5ByOdehranTrueOrderByDatumCasDesc();

    @EntityGraph(attributePaths = {"rocnik", "domaciTym", "domaciTym.univerzita", "hosteTym", "hosteTym.univerzita", "mistoKonani"})
    List<Zapas> findByRocnikAndOdehranTrue(Rocnik rocnik);

    @EntityGraph(attributePaths = {"rocnik", "domaciTym", "domaciTym.univerzita", "hosteTym", "hosteTym.univerzita", "mistoKonani"})
    List<Zapas> findTop3ByOdehranFalseOrderByDatumCasAsc();
}
