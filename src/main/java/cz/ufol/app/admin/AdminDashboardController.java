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

    private final DashboardService dashboardService;

    @GetMapping
    public String dashboard(Model model) {
        var data = dashboardService.getDashboardData();
        model.addAttribute("zapasyBezVysledku", data.zapasyBezVysledku());
        model.addAttribute("odehraneZapasy", data.odehraneZapasy());
        model.addAttribute("aktivniTymy", data.aktivniTymy());
        model.addAttribute("aktivniRocnik", data.aktivniRocnik());
        model.addAttribute("posledniZapasy", data.posledniZapasy());
        model.addAttribute("activePage", "dashboard");
        return "admin/dashboard";
    }
}
