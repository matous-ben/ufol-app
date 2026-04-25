package cz.ufol.app.admin;

import cz.ufol.app.team.Tym;
import cz.ufol.app.team.TymRepository;
import cz.ufol.app.university.UniverzitaRepository;
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

    private final TymRepository tymRepository;
    private final UniverzitaRepository univerzitaRepository;

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
        model.addAttribute("tymy", tymRepository.findAllByOrderByNazevAsc());
        model.addAttribute("activePage", "tymy");
        return "admin/tymy/list";
    }

    @GetMapping("/novy")
    public String createForm(Model model) {
        model.addAttribute("tym", new Tym());
        model.addAttribute("univerzity", univerzitaRepository.findAllByOrderByNazevAsc());
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

        // 1. Proactive validation
        if (tymRepository.existsByNazevIgnoreCase(nazev.trim())) {
            redirectAttributes.addFlashAttribute("error", "Tým s názvem '" + nazev.trim() + "' již existuje.");
            // Return back to the form, so they can try again
            return "redirect:/admin/tymy/novy";
        }

        var univerzita = univerzitaRepository.findById(univerzitaId).orElseThrow();
        var tym = Tym.builder()
                .nazev(nazev.trim())
                .univerzita(univerzita)
                .aktivni(aktivni)
                .build();

        tymRepository.save(tym);
        redirectAttributes.addFlashAttribute("success", "Tým byl úspěšně přidán.");
        return "redirect:/admin/tymy";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("tym", tymRepository.findById(id).orElseThrow());
        model.addAttribute("univerzity", univerzitaRepository.findAllByOrderByNazevAsc());
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

        // 1. Proactive validation (ignoring the team being edited)
        if (tymRepository.existsByNazevIgnoreCaseAndIdNot(nazev.trim(), id)) {
            redirectAttributes.addFlashAttribute("error", "Tým s názvem '" + nazev.trim() + "' již existuje.");
            return "redirect:/admin/tymy/" + id + "/edit";
        }

        var tym = tymRepository.findById(id).orElseThrow();
        var univerzita = univerzitaRepository.findById(univerzitaId).orElseThrow();

        tym.setNazev(nazev.trim());
        tym.setUniverzita(univerzita);
        tym.setAktivni(aktivni);

        tymRepository.save(tym);
        redirectAttributes.addFlashAttribute("success", "Tým byl upraven.");
        return "redirect:/admin/tymy";
    }

    @PostMapping("/{id}/smazat")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        tymRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Tým byl smazán.");
        return "redirect:/admin/tymy";
    }
}
