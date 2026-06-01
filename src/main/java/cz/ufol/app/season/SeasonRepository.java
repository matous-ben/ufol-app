package cz.ufol.app.season;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface RocnikRepository extends JpaRepository<Rocnik, Long> {
    Optional<Rocnik> findByAktivniTrue();
    List<Rocnik> findAllByOrderByRokOdDesc();
    boolean existsByNazevIgnoreCase(String nazev);

    @Modifying
    @Transactional
    @Query("UPDATE Rocnik r SET r.aktivni = false")
    void deactivateAll();
}
