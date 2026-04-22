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
        return "admin/tymy/form";
    }

    @PostMapping("/novy")
    public String create(@RequestParam String nazev,
                         @RequestParam Long univerzitaId,
                         @RequestParam(defaultValue = "true") boolean aktivni,
                         RedirectAttributes redirectAttributes) {
        var univerzita = univerzitaRepository.findById(univerzitaId).orElseThrow();
        var tym = Tym.builder()
                .nazev(nazev)
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
        return "admin/tymy/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @RequestParam String nazev,
                       @RequestParam Long univerzitaId,
                       @RequestParam(defaultValue = "false") boolean aktivni,
                       RedirectAttributes redirectAttributes) {
        var tym = tymRepository.findById(id).orElseThrow();
        var univerzita = univerzitaRepository.findById(univerzitaId).orElseThrow();
        tym.setNazev(nazev);
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
