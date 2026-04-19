package cz.ufol.app.home;

import cz.ufol.app.match.ZapasRepository;
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
    private final ZapasRepository zapasRepository;

    @GetMapping("/")
    public String home(Model model) {
        List<StandingsRowDTO> fullStandings = standingsService.calculateStandings();

        // omezime vystup jen na prvni 4 tymy v tabulce
        List<StandingsRowDTO> miniStandings = fullStandings.subList(
                0, Math.min(4,  fullStandings.size())
        );

        model.addAttribute("standings", miniStandings);
        model.addAttribute("upcomingMatches",
                zapasRepository.findTop3ByOdehranFalseOrderByDatumCasAsc());
        model.addAttribute("activePage", "home");
        return "public/index";
    }
}
