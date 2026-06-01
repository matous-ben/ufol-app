package cz.ufol.app.home;

import cz.ufol.app.match.MatchService;
import cz.ufol.app.standings.StandingsRowDTO;
import cz.ufol.app.standings.StandingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final StandingsService standingsService;
    private final MatchService matchService;

    @GetMapping("/")
    public String home(Model model) {
        List<StandingsRowDTO> fullStandings = standingsService.calculateStandings();

        // omezime vystup jen na prvni 4 teams v tabulce
        List<StandingsRowDTO> miniStandings = fullStandings.subList(
                0, Math.min(4,  fullStandings.size())
        );

        model.addAttribute("standings", miniStandings);
        model.addAttribute("upcomingMatches", matchService.findTop3UpcomingForHome());
        model.addAttribute("activePage", "home");
        return "home";
    }
}
