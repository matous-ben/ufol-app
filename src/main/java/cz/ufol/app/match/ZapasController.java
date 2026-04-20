package cz.ufol.app.match;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ZapasController {

    private final ZapasService zapasService;

    @GetMapping("/zapasy")
    public String zapasy(Model model) {
        model.addAttribute("naplanovane", zapasService.findNaplanovane());
        model.addAttribute("odehrane", zapasService.findOdehrane());
        model.addAttribute("activePage", "zapasy");
        return "public/zapasy";
    }
}
