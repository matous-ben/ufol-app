package cz.ufol.app.season;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RocnikRepository extends JpaRepository<Rocnik, Long> {
    Optional<Rocnik> findByAktivniTrue();
    List<Rocnik> findAllByOrderByRokOdDesc();
}
