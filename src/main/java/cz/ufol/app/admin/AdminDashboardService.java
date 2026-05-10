package cz.ufol.app.admin;

import cz.ufol.app.match.Zapas;
import cz.ufol.app.match.ZapasRepository;
import cz.ufol.app.season.Rocnik;
import cz.ufol.app.season.RocnikRepository;
import cz.ufol.app.team.TymRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final ZapasRepository zapasRepository;
    private final TymRepository tymRepository;
    private final RocnikRepository rocnikRepository;

    @Transactional(readOnly = true)
    public DashboardData getDashboardData() {
        var aktivniRocnik = rocnikRepository.findByAktivniTrue().orElse(null);

        long zapasyBezVysledku = 0;
        long odehraneZapasy = 0;
        List<Zapas> posledniZapasy = Collections.emptyList();
        if (aktivniRocnik != null) {
            var naplanovane = zapasRepository
                    .findByRocnikAndOdehranFalseOrderByDatumCasAsc(aktivniRocnik);
            var odehrane = zapasRepository
                    .findByRocnikAndOdehranTrueOrderByDatumCasDesc(aktivniRocnik);
            zapasyBezVysledku = naplanovane.size();
            odehraneZapasy = odehrane.size();
            posledniZapasy = odehrane.stream().limit(5).toList();
        }

        return new DashboardData(
                zapasyBezVysledku,
                odehraneZapasy,
                tymRepository.findByAktivniTrue().size(),
                aktivniRocnik,
                posledniZapasy
        );
    }

    public record DashboardData(
            long zapasyBezVysledku,
            long odehraneZapasy,
            int aktivniTymy,
            Rocnik aktivniRocnik,
            List<Zapas> posledniZapasy
    ) {}
}
