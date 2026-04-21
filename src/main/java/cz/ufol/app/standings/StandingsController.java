package cz.ufol.app.standings;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Tag(name = "Tabulka", description = "Ligová tabulka")
public class StandingsController {

    private final StandingsService standingsService;

    @GetMapping("/tabulka")
    @Operation(
            summary = "Zobrazit ligovou tabulku",
            description = "Vrátí aktuální tabulku aktivního ročníku seřazenou podle bodů"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Úspěšně vyrenderovaná HTML stránka",
            content = @Content(
                    mediaType = "text/html",
                    schema = @Schema(type = "string")
            )
    )
    public String tabulka(Model model) {
        model.addAttribute("standings", standingsService.calculateStandings());
        model.addAttribute("activePage", "tabulka");
        return "public/tabulka";
    }
}
