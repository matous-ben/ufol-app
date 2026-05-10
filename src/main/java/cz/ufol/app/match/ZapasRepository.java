package cz.ufol.app.match;

import cz.ufol.app.season.Rocnik;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ZapasRepository extends JpaRepository<Zapas, Long> {
    @EntityGraph(attributePaths = {"domaciTym", "domaciTym.univerzita", "hosteTym", "hosteTym.univerzita", "rocnik", "mistoKonani"})
    List<Zapas> findByRocnik(Rocnik rocnik);
    @EntityGraph(attributePaths = {"domaciTym", "domaciTym.univerzita", "hosteTym", "hosteTym.univerzita", "rocnik", "mistoKonani"})
    List<Zapas> findByRocnikAndOdehranFalseOrderByDatumCasAsc(Rocnik rocnik);
    @EntityGraph(attributePaths = {"domaciTym", "domaciTym.univerzita", "hosteTym", "hosteTym.univerzita", "rocnik", "mistoKonani"})
    List<Zapas> findByRocnikAndOdehranTrueOrderByDatumCasDesc(Rocnik rocnik);
    @EntityGraph(attributePaths = {"domaciTym", "domaciTym.univerzita", "hosteTym", "hosteTym.univerzita", "rocnik", "mistoKonani"})
    List<Zapas> findTop5ByOdehranTrueOrderByDatumCasDesc();

    @EntityGraph(attributePaths = {"domaciTym", "domaciTym.univerzita", "hosteTym", "hosteTym.univerzita", "rocnik", "mistoKonani"})
    List<Zapas> findByRocnikAndOdehranTrue(Rocnik rocnik);

    @EntityGraph(attributePaths = {"domaciTym", "domaciTym.univerzita", "hosteTym", "hosteTym.univerzita", "rocnik", "mistoKonani"})
    List<Zapas> findTop3ByOdehranFalseOrderByDatumCasAsc();

    @EntityGraph(attributePaths = {"domaciTym", "domaciTym.univerzita", "hosteTym", "hosteTym.univerzita", "rocnik", "mistoKonani"})
    Optional<Zapas> findById(Long id);

    // reseni problemu N+1 pomoci JOIN FETCH
    @Query("SELECT z FROM Zapas z " +
            "JOIN FETCH z.domaciTym dt " +
            "JOIN FETCH dt.univerzita " +
            "JOIN FETCH z.hosteTym ht " +
            "JOIN FETCH ht.univerzita " +
            "WHERE z.rocnik = :rocnik AND z.odehran = true")
    List<Zapas> findByRocnikAndOdehranTrueWithDetails(@Param("rocnik") Rocnik rocnik);
}
