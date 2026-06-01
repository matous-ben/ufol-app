package cz.ufol.app.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    @EntityGraph(attributePaths = {"university"})
    List<Team> findByActiveTrue();

    @EntityGraph(attributePaths = {"university"})
    List<Team> findAllByOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
