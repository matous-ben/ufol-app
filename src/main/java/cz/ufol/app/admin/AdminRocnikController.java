package cz.ufol.app.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/rocniky")
@RequiredArgsConstructor
@Tag(name = "Admin - ročníky", description = "Správa ročníků — vyžaduje přihlášení")
public class AdminRocnikController {

    private final AdminRocnikService adminRocnikService;

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
    public String list(Model model) {
        model.addAttribute("rocniky", adminRocnikService.findAllByOrderByRokOdDesc());
        model.addAttribute("activePage", "rocniky");
        return "admin/rocniky/list";
    }

    @GetMapping("/novy")
    public String createForm(Model model) {
        model.addAttribute("rocniky", adminRocnikService.findAllByOrderByRokOdDesc());
        model.addAttribute("activePage", "rocniky");
        return "admin/rocniky/form";
    }

    @PostMapping("/novy")
    public String create(@RequestParam String nazev,
                         @RequestParam Integer rokOd,
                         @RequestParam Integer rokDo,
                         RedirectAttributes redirectAttributes) {
        String error = adminRocnikService.create(nazev, rokOd, rokDo);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            return "redirect:/admin/rocniky/novy";
        }
        redirectAttributes.addFlashAttribute("success", "Ročník byl vytvořen.");
        return "redirect:/admin/rocniky";
    }

    @PostMapping("/{id}/aktivovat")
    public String aktivovat(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String result = adminRocnikService.aktivovat(id);
        redirectAttributes.addFlashAttribute(
                result.equals("Ročník nebyl nalezen.") ? "error" : "success",
                result
        );
        return "redirect:/admin/rocniky";
    }

    @PostMapping("/{id}/archivovat")
    public String archivovat(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String result = adminRocnikService.archivovat(id);
        redirectAttributes.addFlashAttribute(
                result.equals("Ročník nebyl nalezen.") ? "error" : "success",
                result
        );
        return "redirect:/admin/rocniky";
    }

    @PostMapping("/{id}/smazat")
    public String smazat(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String result = adminRocnikService.smazat(id);
        boolean error = result.equals("Ročník nebyl nalezen.")
                || result.startsWith("Aktivní ročník")
                || result.startsWith("Ročník nelze smazat");
        redirectAttributes.addFlashAttribute(error ? "error" : "success", result);

        return "redirect:/admin/rocniky";
    }
}
