package cz.ufol.app.admin;

import cz.ufol.app.team.Tym;
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

    private final AdminTymService adminTymService;

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
        model.addAttribute("tymy", adminTymService.findAllTymy());
        model.addAttribute("activePage", "tymy");
        return "admin/tymy/list";
    }

    @GetMapping("/novy")
    public String createForm(Model model) {
        model.addAttribute("tym", new Tym());
        model.addAttribute("univerzity", adminTymService.findAllUniverzity());
        model.addAttribute("activePage", "tymy");

        // Add the specific action URL for creating
        model.addAttribute("formAction", "/admin/tymy/novy");

        return "admin/tymy/form";
    }

    @PostMapping("/novy")
    public String create(@RequestParam String nazev,
                         @RequestParam Long univerzitaId,
                         @RequestParam(defaultValue = "true") boolean aktivni,
                         RedirectAttributes redirectAttributes) {
        String error = adminTymService.create(nazev, univerzitaId, aktivni);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            return "redirect:/admin/tymy/novy";
        }
        redirectAttributes.addFlashAttribute("success", "Tým byl úspěšně přidán.");
        return "redirect:/admin/tymy";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("tym", adminTymService.findTymById(id));
        model.addAttribute("univerzity", adminTymService.findAllUniverzity());
        model.addAttribute("activePage", "tymy");

        // Add the specific action URL for editing
        model.addAttribute("formAction", "/admin/tymy/" + id + "/edit");

        return "admin/tymy/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @RequestParam String nazev,
                       @RequestParam Long univerzitaId,
                       @RequestParam(defaultValue = "false") boolean aktivni,
                       RedirectAttributes redirectAttributes) {
        String error = adminTymService.edit(id, nazev, univerzitaId, aktivni);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            return "redirect:/admin/tymy/" + id + "/edit";
        }
        redirectAttributes.addFlashAttribute("success", "Tým byl upraven.");
        return "redirect:/admin/tymy";
    }

    @PostMapping("/{id}/smazat")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminTymService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Tým byl smazán.");
        return "redirect:/admin/tymy";
    }
}
