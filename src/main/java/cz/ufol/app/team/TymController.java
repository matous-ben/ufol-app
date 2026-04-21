package cz.ufol.app.team;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Tag(name = "Týmy", description = "Přehled týmů")
public class TymController {

    private final TymService tymService;

    @GetMapping("/tymy")
    @Operation(summary = "Zobrazit všechny týmy")
    public String tymy(Model model) {
        model.addAttribute("tymy", tymService.findAllAktivni());
        model.addAttribute("activePage", "tymy");
        return "public/tymy";
    }
}
