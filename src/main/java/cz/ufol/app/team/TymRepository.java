package cz.ufol.app.team;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TymRepository extends JpaRepository<Tym, Long> {
    List<Tym> findByAktivniTrue();
    List<Tym> findAllByOrderByNazevAsc();

    // Pro vytvoření nového týmu
    boolean existsByNazevIgnoreCase(String nazev);

    // Pro editaci existujícího týmu (ignoruje shodu s vlastním ID)
    boolean existsByNazevIgnoreCaseAndIdNot(String nazev, Long id);
}
