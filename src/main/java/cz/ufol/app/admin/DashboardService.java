package cz.ufol.app.admin;

import cz.ufol.app.match.Zapas;
import cz.ufol.app.match.ZapasRepository;
import cz.ufol.app.season.Rocnik;
import cz.ufol.app.season.RocnikRepository;
import cz.ufol.app.team.TymRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ZapasRepository zapasRepository;
    private final TymRepository tymRepository;
    private final RocnikRepository rocnikRepository;

    public record DashboardData(
            long zapasyBezVysledku,
            long odehraneZapasy,
            int aktivniTymy,
            Rocnik aktivniRocnik,
            List<Zapas> posledniZapasy
    ) {}

    @Transactional(readOnly = true)
    public DashboardData getDashboardData() {
        var aktivniRocnik = rocnikRepository.findByAktivniTrue().orElse(null);
        if (aktivniRocnik == null) {
            return new DashboardData(0, 0, tymRepository.findByAktivniTrue().size(), null, List.of());
        }

        var naplanovane = zapasRepository.findByRocnikAndOdehranFalseOrderByDatumCasAsc(aktivniRocnik);
        var odehrane = zapasRepository.findByRocnikAndOdehranTrueOrderByDatumCasDesc(aktivniRocnik);
        return new DashboardData(
                naplanovane.size(),
                odehrane.size(),
                tymRepository.findByAktivniTrue().size(),
                aktivniRocnik,
                odehrane.stream().limit(5).toList()
        );
    }
}
