package cz.ufol.app.home;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("standings", homeService.getMiniStandings());
        model.addAttribute("upcomingMatches", homeService.getUpcomingMatches());
        model.addAttribute("activePage", "home");
        return "public/index";
    }
}
