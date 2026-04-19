package cz.ufol.app.standings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class StandingsController {

    private final StandingsService standingsService;

    @GetMapping("/tabulka")
    public String tabulka(Model model) {
        model.addAttribute("standings", standingsService.calculateStandings());
        model.addAttribute("activePage", "tabulka");
        return "public/tabulka";
    }
}
