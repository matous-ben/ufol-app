package cz.ufol.app.event;

import cz.ufol.app.match.UcastVZapase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UdalostZapasuRepository extends JpaRepository<UdalostZapasu, Long> {
    List<UdalostZapasu> findByUcastVZapase(UcastVZapase ucast);
    List<UdalostZapasu> findByUcastVZapase_ZapasIdAndTypUdalosti_Kod(Long zapasId, String kod);
}
