package cz.ufol.app.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface TymRepository extends JpaRepository<Tym, Long> {
    @EntityGraph(attributePaths = "univerzita")
    List<Tym> findByAktivniTrue();
    @EntityGraph(attributePaths = "univerzita")
    List<Tym> findAllByOrderByNazevAsc();

    // Pro vytvoření nového týmu
    boolean existsByNazevIgnoreCase(String nazev);

    // Pro editaci existujícího týmu (ignoruje shodu s vlastním ID)
    boolean existsByNazevIgnoreCaseAndIdNot(String nazev, Long id);
}
