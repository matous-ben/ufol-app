package cz.ufol.app.admin;

import cz.ufol.app.match.Zapas;
import cz.ufol.app.match.ZapasRepository;
import cz.ufol.app.season.RocnikRepository;
import cz.ufol.app.team.TymRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Administrátorský dashboard — vyžaduje přihlášení")
public class AdminDashboardController {

    private final ZapasRepository zapasRepository;
    private final TymRepository tymRepository;
    private final RocnikRepository rocnikRepository;

    @GetMapping
    @Operation(summary = "Admin dashboard", description = "Přehled statistik ligy a rychle akce")
    @ApiResponse(
            responseCode = "200",
            description = "Úspěšně vyrenderovaná HTML stránka",
            content = @Content(
                    mediaType = "text/html",
                    schema = @Schema(type = "string")
            )
    )
    public String dashboard(Model model) {
        var aktivniRocnik = rocnikRepository.findByAktivniTrue().orElse(null);

        long zapasyBezVysledku = 0;
        long odehraneZapasy = 0;
        List<Zapas> posledniZapasy = java.util.Collections.emptyList();
        if (aktivniRocnik != null) {
            var naplanovane = zapasRepository
                    .findByRocnikAndOdehranFalseOrderByDatumCasAsc(aktivniRocnik);
            var odehrane = zapasRepository
                    .findByRocnikAndOdehranTrueOrderByDatumCasDesc(aktivniRocnik);
            zapasyBezVysledku = naplanovane.size();
            odehraneZapasy = odehrane.size();
            posledniZapasy = odehrane.stream().limit(5).toList();        }

        model.addAttribute("zapasyBezVysledku", zapasyBezVysledku);
        model.addAttribute("odehraneZapasy", odehraneZapasy);
        model.addAttribute("aktivniTymy", tymRepository.findByAktivniTrue().size());
        model.addAttribute("aktivniRocnik", aktivniRocnik);
        model.addAttribute("posledniZapasy", posledniZapasy);
        model.addAttribute("activePage", "dashboard");
        return "admin/dashboard";
    }
}
