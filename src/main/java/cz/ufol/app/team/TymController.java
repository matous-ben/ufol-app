package cz.ufol.app.team;

import cz.ufol.app.player.HracService;
import cz.ufol.app.season.RocnikRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Tag(name = "Týmy", description = "Přehled týmů")
public class TymController {

    private final TymService tymService;
    private final TymRepository tymRepository;
    private final RocnikRepository rocnikRepository;
    private final HracService hracService;

    @GetMapping("/tymy")
    @Operation(summary = "Zobrazit všechny týmy")
    @ApiResponse(
            responseCode = "200",
            description = "Úspěšně vyrenderovaná HTML stránka",
            content = @Content(
                    mediaType = "text/html",
                    schema = @Schema(type = "string")
            )
    )
    public String tymy(Model model) {
        model.addAttribute("tymy", tymService.findAllAktivni());
        model.addAttribute("activePage", "tymy");
        return "public/tymy";
    }

    @GetMapping("/tymy/{id}")
    public String tymDetail(@PathVariable Long id, Model model) {
        var tym = tymRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var aktivniRocnik = rocnikRepository.findByAktivniTrue().orElse(null);
        var hracStats = aktivniRocnik != null
                ? hracService.najdiStatistikyTymuProRocnik(tym, aktivniRocnik)
                : List.of();

        model.addAttribute("tym", tym);
        model.addAttribute("hracStats", hracStats);
        model.addAttribute("activePage", "tymy");
        return "public/tym-detail";
    }
}
