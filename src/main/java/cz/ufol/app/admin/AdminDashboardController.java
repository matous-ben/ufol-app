package cz.ufol.app.admin;

import cz.ufol.app.match.ZapasRepository;
import cz.ufol.app.season.RocnikRepository;
import cz.ufol.app.team.TymRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Administrátorský dashboard — vyžaduje přihlášení")
public class AdminDashboardController {

    private final ZapasRepository zapasRepository;
    private final TymRepository tymRepository;
    private final RocnikRepository rocnikRepository;

    @GetMapping
    @Operation(summary = "Admin dashboard", description = "Přehled statistik ligy")
    public String dashboard(Model model) {
        long zapasyBezVysledku = zapasRepository.findAll()
                .stream().filter(z -> !z.isOdehran()).count();

        model.addAttribute("zapasyBezVysledku", zapasyBezVysledku);
        model.addAttribute("odehraneZapasy",
                zapasRepository.findTop5ByOdehranTrueOrderByDatumCasDesc().size());
        model.addAttribute("aktivniTymy",
                tymRepository.findByAktivniTrue().size());
        model.addAttribute("aktivniRocnik",
                rocnikRepository.findByAktivniTrue().orElse(null));
        model.addAttribute("posledniZapasy",
                zapasRepository.findTop5ByOdehranTrueOrderByDatumCasDesc());
        model.addAttribute("activePage", "dashboard");

        return "admin/dashboard";
    }
}
