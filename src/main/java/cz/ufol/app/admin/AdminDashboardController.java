package cz.ufol.app.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @GetMapping
    public String helloAdmin(Model model) {
        model.addAttribute("message", "Hello Admin!");
        return "admin/dashboard";
    }
}
