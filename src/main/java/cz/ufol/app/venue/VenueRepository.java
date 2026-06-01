package cz.ufol.app.venue;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MistoKonaniRepository extends JpaRepository<MistoKonani, Long> {
    List<MistoKonani> findAllByOrderByNazevAsc();
}
