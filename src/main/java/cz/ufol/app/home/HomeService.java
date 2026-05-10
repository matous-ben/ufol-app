package cz.ufol.app.home;

import cz.ufol.app.match.ZapasService;
import cz.ufol.app.match.Zapas;
import cz.ufol.app.standings.StandingsRowDTO;
import cz.ufol.app.standings.StandingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final StandingsService standingsService;
    private final ZapasService zapasService;

    @Transactional(readOnly = true)
    public List<StandingsRowDTO> findMiniStandings() {
        List<StandingsRowDTO> fullStandings = standingsService.calculateStandings();
        return fullStandings.subList(0, Math.min(4, fullStandings.size()));
    }

    @Transactional(readOnly = true)
    public List<Zapas> findUpcomingMatches() {
        return zapasService.findTop3Naplanovane();
    }
}
