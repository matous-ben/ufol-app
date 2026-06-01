package cz.ufol.app.admin;

import cz.ufol.app.team.TymService;
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
@RequestMapping("/admin/tymy")
@RequiredArgsConstructor
@Tag(name = "Admin - týmy", description = "Správa týmů — vyžaduje přihlášení")
public class AdminTymController {

    private final TymService tymService;

    @GetMapping
    @Operation(summary = "Admin dashboard - týmy", description = "Správa jednotlivých týmů")
    @ApiResponse(
            responseCode = "200",
            description = "Úspěšně vyrenderovaná HTML stránka",
            content = @Content(
                    mediaType = "text/html",
                    schema = @Schema(type = "string")
            )
    )
    public String list(Model model) {
        model.addAttribute("tymy", tymService.findAllAdminTymy());
        model.addAttribute("activePage", "tymy");
        return "admin/tymy/list";
    }

    @GetMapping("/novy")
    public String createForm(Model model) {
        var data = tymService.getCreateFormData();
        model.addAttribute("tym", data.tym());
        model.addAttribute("univerzity", data.univerzity());
        model.addAttribute("activePage", "tymy");
        model.addAttribute("formAction", data.formAction());
        return "admin/tymy/form";
    }

    @PostMapping("/novy")
    public String create(@RequestParam String nazev,
                         @RequestParam Long univerzitaId,
                         @RequestParam(defaultValue = "true") boolean aktivni,
                         RedirectAttributes redirectAttributes) {
        var result = tymService.createAdminTym(nazev, univerzitaId, aktivni);
        redirectAttributes.addFlashAttribute(result.flashType(), result.flashMessage());
        return "redirect:" + result.redirectPath();
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var data = tymService.getEditFormData(id);
        model.addAttribute("tym", data.tym());
        model.addAttribute("univerzity", data.univerzity());
        model.addAttribute("activePage", "tymy");
        model.addAttribute("formAction", data.formAction());
        return "admin/tymy/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @RequestParam String nazev,
                       @RequestParam Long univerzitaId,
                       @RequestParam(defaultValue = "false") boolean aktivni,
                       RedirectAttributes redirectAttributes) {
        var result = tymService.updateAdminTym(id, nazev, univerzitaId, aktivni);
        redirectAttributes.addFlashAttribute(result.flashType(), result.flashMessage());
        return "redirect:" + result.redirectPath();
    }

    @PostMapping("/{id}/smazat")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var result = tymService.deleteAdminTym(id);
        redirectAttributes.addFlashAttribute(result.flashType(), result.flashMessage());
        return "redirect:" + result.redirectPath();
    }
}
