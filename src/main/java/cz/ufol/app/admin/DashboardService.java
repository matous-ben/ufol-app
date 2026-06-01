package cz.ufol.app.admin;

import cz.ufol.app.match.Match;
import cz.ufol.app.match.MatchRepository;
import cz.ufol.app.season.Season;
import cz.ufol.app.season.SeasonRepository;
import cz.ufol.app.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final SeasonRepository seasonRepository;

    public record DashboardData(
            long zapasyBezVysledku,
            long odehraneZapasy,
            int aktivniTymy,
            Season aktivniRocnik,
            List<Match> posledniZapasy
    ) {}

    @Transactional(readOnly = true)
    public DashboardData getDashboardData() {
        var aktivniRocnik = seasonRepository.findByActiveTrue().orElse(null);
        if (aktivniRocnik == null) {
            return new DashboardData(0, 0, teamRepository.findByActiveTrue().size(), null, List.of());
        }

        var naplanovane = matchRepository.findBySeasonAndPlayedFalseOrderByMatchDatetimeAsc(aktivniRocnik);
        var odehrane = matchRepository.findBySeasonAndPlayedTrueOrderByMatchDatetimeDesc(aktivniRocnik);
        return new DashboardData(
                naplanovane.size(),
                odehrane.size(),
                teamRepository.findByActiveTrue().size(),
                aktivniRocnik,
                odehrane.stream().limit(5).toList()
        );
    }
}
