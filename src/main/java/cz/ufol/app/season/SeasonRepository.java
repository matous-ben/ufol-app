package cz.ufol.app.season;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface SeasonRepository extends JpaRepository<Season, Long> {
    Optional<Season> findByActiveTrue();
    List<Season> findAllByOrderByYearFromDesc();
    boolean existsByNameIgnoreCase(String name);

    @Modifying
    @Transactional
    @Query("UPDATE Season r SET r.active = false")
    void deactivateAll();
}
