package cz.ufol.app.match;

import cz.ufol.app.season.Rocnik;
import cz.ufol.app.season.RocnikRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ZapasService {

    private final ZapasRepository zapasRepository;
    private final RocnikRepository rocnikRepository;

    // Vrati vsechny naplanovane zapasy v aktivni sezone
    // serazene podle data vzestupne (nejblizsi nejdrvie)
    public List<Zapas> findNaplanovane() {
        Optional<Rocnik> aktivniRocnik = rocnikRepository.findByAktivniTrue();
        if (aktivniRocnik.isEmpty()) return Collections.emptyList();
        return zapasRepository
                .findByRocnikAndOdehranFalseOrderByDatumCasAsc(aktivniRocnik.get());
    }

    // Vrati vsechny odehrane zapasy v aktivni sezonu
    // serazene podle data sestupne
    public List<Zapas> findOdehrane() {
        Optional<Rocnik> aktivniRocnik = rocnikRepository.findByAktivniTrue();
        if (aktivniRocnik.isEmpty()) return Collections.emptyList();
        return zapasRepository
                .findByRocnikAndOdehranTrueOrderByDatumCasDesc(aktivniRocnik.get());
    }
}
