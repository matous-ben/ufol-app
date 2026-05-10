package cz.ufol.app.home;

import cz.ufol.app.match.Zapas;
import cz.ufol.app.match.ZapasService;
import cz.ufol.app.standings.StandingsRowDTO;
import cz.ufol.app.standings.StandingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final StandingsService standingsService;
    private final ZapasService zapasService;

    public List<StandingsRowDTO> getMiniStandings() {
        List<StandingsRowDTO> fullStandings = standingsService.calculateStandings();
        return fullStandings.subList(0, Math.min(4, fullStandings.size()));
    }

    public List<Zapas> getUpcomingMatches() {
        return zapasService.findTop3Naplanovane();
    }
}
