package cz.ufol.app.admin;

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

@Controller
@RequestMapping("/admin/rocniky")
@RequiredArgsConstructor
@Tag(name = "Admin - ročníky", description = "Správa ročníků — vyžaduje přihlášení")
public class AdminRocnikController {

    @GetMapping
    @Operation(summary = "Admin dashboard - ročníky", description = "Správa jednotlivých ročníků")
    @ApiResponse(
            responseCode = "200",
            description = "Úspěšně vyrenderovaná HTML stránka",
            content = @Content(
                    mediaType = "text/html",
                    schema = @Schema(type = "string")
            )
    )
    public String zapasy(Model model) {
        model.addAttribute("activePage", "rocniky");
        return "admin/rocniky";
    }
}
