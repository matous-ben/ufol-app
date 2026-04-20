package cz.ufol.app.team;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class TymController {

    private final TymService tymService;

    @GetMapping("/tymy")
    public String tymy(Model model) {
        model.addAttribute("tymy", tymService.findAllAktivni());
        model.addAttribute("activePage", "tymy");
        return "public/tymy";
    }
}
