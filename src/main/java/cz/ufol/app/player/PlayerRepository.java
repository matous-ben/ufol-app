package cz.ufol.app.player;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HracRepository extends JpaRepository<Hrac, Long> {
    List<Hrac> findAllByOrderByPrijmeniAscJmenoAsc();
}
