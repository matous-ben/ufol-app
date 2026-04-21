package cz.ufol.app.team;

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
@Tag(name = "Týmy", description = "Přehled týmů")
public class TymController {

    private final TymService tymService;

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
}
