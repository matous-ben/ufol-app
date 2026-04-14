package cz.ufol.app.university;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UniverzitaRepository extends JpaRepository<Univerzita, Long> {
    List<Univerzita> findAllByOrderByNazevAsc();
}
