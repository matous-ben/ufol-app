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
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Administrátorský dashboard — vyžaduje přihlášení")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

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
        AdminDashboardService.DashboardData dashboardData = adminDashboardService.getDashboardData();
        model.addAttribute("zapasyBezVysledku", dashboardData.zapasyBezVysledku());
        model.addAttribute("odehraneZapasy", dashboardData.odehraneZapasy());
        model.addAttribute("aktivniTymy", dashboardData.aktivniTymy());
        model.addAttribute("aktivniRocnik", dashboardData.aktivniRocnik());
        model.addAttribute("posledniZapasy", dashboardData.posledniZapasy());
        model.addAttribute("activePage", "dashboard");
        return "admin/dashboard";
    }
}
