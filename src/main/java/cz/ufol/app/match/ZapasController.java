package cz.ufol.app.match;

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
@Tag(name = "Zápasy", description = "Program a výsledky zápasů")
public class ZapasController {

    private final ZapasService zapasService;

    @GetMapping("/zapasy")
    @Operation(
            summary = "Zobrazit všechny zápasy",
            description = "Vrátí naplánované a odehrané zápasy aktivního ročníku"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Úspěšně vyrenderovaná HTML stránka",
            content = @Content(
                    mediaType = "text/html",
                    schema = @Schema(type = "string")
            )
    )
    public String zapasy(Model model) {
        model.addAttribute("naplanovane", zapasService.findNaplanovane());
        model.addAttribute("odehrane", zapasService.findOdehrane());
        model.addAttribute("activePage", "zapasy");
        return "public/zapasy";
    }
}
