package cz.ufol.app.admin;

import cz.ufol.app.match.Zapas;
import cz.ufol.app.match.ZapasRepository;
import cz.ufol.app.season.Rocnik;
import cz.ufol.app.season.RocnikService;
import cz.ufol.app.team.TymService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final ZapasRepository zapasRepository;
    private final TymService tymService;
    private final RocnikService rocnikService;

    @Transactional(readOnly = true)
    public DashboardData getDashboardData() {
        Rocnik aktivniRocnik = rocnikService.findByAktivniTrue().orElse(null);

        long zapasyBezVysledku = 0;
        long odehraneZapasy = 0;
        List<Zapas> posledniZapasy = Collections.emptyList();
        if (aktivniRocnik != null) {
            var naplanovane = zapasRepository.findByRocnikAndOdehranFalseOrderByDatumCasAsc(aktivniRocnik);
            var odehrane = zapasRepository.findByRocnikAndOdehranTrueOrderByDatumCasDesc(aktivniRocnik);
            zapasyBezVysledku = naplanovane.size();
            odehraneZapasy = odehrane.size();
            posledniZapasy = odehrane.stream().limit(5).toList();
        }

        long aktivniTymy = tymService.findAllAktivni().size();
        return new DashboardData(zapasyBezVysledku, odehraneZapasy, aktivniTymy, aktivniRocnik, posledniZapasy);
    }

    public record DashboardData(
            long zapasyBezVysledku,
            long odehraneZapasy,
            long aktivniTymy,
            Rocnik aktivniRocnik,
            List<Zapas> posledniZapasy
    ) {
    }
}
