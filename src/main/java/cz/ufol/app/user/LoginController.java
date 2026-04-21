package cz.ufol.app.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Tag(name = "Login page", description = "Přihlašovací formulář do admin dashboardu")
public class LoginController {

    @GetMapping("/login")
    @Operation(summary = "Login page")
    public String loginPage() {
        return "login";
    }
}
