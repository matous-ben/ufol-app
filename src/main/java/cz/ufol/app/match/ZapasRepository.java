package cz.ufol.app.match;

import cz.ufol.app.season.Rocnik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ZapasRepository extends JpaRepository<Zapas, Long> {
    List<Zapas> findByRocnik(Rocnik rocnik);
    List<Zapas> findByRocnikAndOdehranFalseOrderByDatumCasAsc(Rocnik rocnik);
    List<Zapas> findByRocnikAndOdehranTrueOrderByDatumCasDesc(Rocnik rocnik);
    List<Zapas> findTop5ByOdehranTrueOrderByDatumCasDesc();

    List<Zapas> findByRocnikAndOdehranTrue(Rocnik rocnik);

    List<Zapas> findTop3ByOdehranFalseOrderByDatumCasAsc();

    // reseni problemu N+1 pomoci JOIN FETCH
    @Query("SELECT z FROM Zapas z " +
            "JOIN FETCH z.domaciTym dt " +
            "JOIN FETCH dt.univerzita " +
            "JOIN FETCH z.hosteTym ht " +
            "JOIN FETCH ht.univerzita " +
            "WHERE z.rocnik = :rocnik AND z.odehran = true")
    List<Zapas> findByRocnikAndOdehranTrueWithDetails(@Param("rocnik") Rocnik rocnik);
}
