package cz.ufol.app.admin;

import cz.ufol.app.season.Rocnik;
import cz.ufol.app.season.RocnikRepository;
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

    private final RocnikRepository rocnikRepository;

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
        model.addAttribute("rocniky", rocnikRepository.findAllByOrderByRokOdDesc());
        model.addAttribute("activePage", "rocniky");
        return "admin/rocniky";
    }

    @GetMapping("/novy")
    public String createForm(Model model) {
        model.addAttribute("rocniky", rocnikRepository.findAllByOrderByRokOdDesc());
        model.addAttribute("activePage", "rocniky");
        return "admin/rocniky";
    }

    @PostMapping("/novy")
    public String create(@RequestParam String nazev,
                         @RequestParam Integer rokOd,
                         @RequestParam Integer rokDo,
                         RedirectAttributes redirectAttributes) {

        String trimmedNazev = nazev != null ? nazev.trim() : "";

        if (trimmedNazev.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Název ročníku je povinný.");
            return "redirect:/admin/rocniky";
        }

        if (rokOd == null || rokDo == null) {
            redirectAttributes.addFlashAttribute("error", "Rok od i rok do jsou povinné.");
            return "redirect:/admin/rocniky";
        }

        if (rokOd < 2000 || rokOd > 2100 || rokDo < 2000 || rokDo > 2100) {
            redirectAttributes.addFlashAttribute("error", "Rok musí být v intervalu 2000-2100.");
            return "redirect:/admin/rocniky";
        }

        if (rokDo <= rokOd) {
            redirectAttributes.addFlashAttribute("error", "Rok do musí být větší než rok od.");
            return "redirect:/admin/rocniky";
        }

        if (rocnikRepository.existsByNazevIgnoreCase(trimmedNazev)) {
            redirectAttributes.addFlashAttribute("error", "Ročník s tímto názvem již existuje.");
            return "redirect:/admin/rocniky";
        }

        var rocnik = Rocnik.builder()
                .nazev(trimmedNazev)
                .rokOd(rokOd)
                .rokDo(rokDo)
                .aktivni(false)
                .build();

        rocnikRepository.save(rocnik);
        redirectAttributes.addFlashAttribute("success", "Ročník byl vytvořen.");
        return "redirect:/admin/rocniky";
    }

    @PostMapping("/{id}/aktivovat")
    public String aktivovat(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var rocnikOpt = rocnikRepository.findById(id);
        if (rocnikOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Ročník nebyl nalezen.");
            return "redirect:/admin/rocniky";
        }

        // Deaktivuj všechny ročníky
        rocnikRepository.findAll().forEach(r -> r.setAktivni(false));
        rocnikRepository.saveAll(rocnikRepository.findAll());

        // Aktivuj vybraný
        var rocnik = rocnikOpt.get();
        rocnik.setAktivni(true);
        rocnikRepository.save(rocnik);

        redirectAttributes.addFlashAttribute("success",
                "Ročník " + rocnik.getNazev() + " byl nastaven jako aktivní.");
        return "redirect:/admin/rocniky";
    }

    @PostMapping("/{id}/archivovat")
    public String archivovat(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var rocnikOpt = rocnikRepository.findById(id);
        if (rocnikOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Ročník nebyl nalezen.");
            return "redirect:/admin/rocniky";
        }

        var rocnik = rocnikOpt.get();
        rocnik.setAktivni(false);
        rocnikRepository.save(rocnik);

        redirectAttributes.addFlashAttribute("success", "Ročník byl archivován.");
        return "redirect:/admin/rocniky";
    }

    @PostMapping("/{id}/smazat")
    public String smazat(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var rocnikOpt = rocnikRepository.findById(id);
        if (rocnikOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Ročník nebyl nalezen.");
            return "redirect:/admin/rocniky";
        }

        var rocnik = rocnikOpt.get();
        if (rocnik.isAktivni()) {
            redirectAttributes.addFlashAttribute("error", "Aktivní ročník nelze smazat. Nejprve ho archivujte.");
            return "redirect:/admin/rocniky";
        }

        rocnikRepository.delete(rocnik);
        redirectAttributes.addFlashAttribute("success", "Ročník byl smazán.");
        return "redirect:/admin/rocniky";
    }
}
