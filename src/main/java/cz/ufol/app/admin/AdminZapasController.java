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
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
@Controller
@RequestMapping("/admin/zapasy")
@RequiredArgsConstructor
@Tag(name = "Admin - zápasy", description = "Správa zápasů — vyžaduje přihlášení")
public class AdminZapasController {

    private final AdminZapasService adminZapasService;

    @GetMapping
    @Operation(summary = "Admin dashboard - zápasy", description = "Správa jednotlivých zápasů")
    @ApiResponse(
            responseCode = "200",
            description = "Úspěšně vyrenderovaná HTML stránka",
            content = @Content(
                    mediaType = "text/html",
                    schema = @Schema(type = "string")
            )
    )
    public String list(Model model) {
        var listData = adminZapasService.getListData();
        model.addAttribute("naplanovane", listData.naplanovane());
        model.addAttribute("odehrane", listData.odehrane());
        model.addAttribute("aktivniRocnik", listData.aktivniRocnik());
        model.addAttribute("activePage", "zapasy");
        return "admin/zapasy/list";
    }

    @GetMapping("/novy")
    public String createForm(Model model) {
        var formData = adminZapasService.getCreateFormData();
        model.addAttribute("tymy", formData.tymy());
        model.addAttribute("rocniky", formData.rocniky());
        model.addAttribute("mistaKonani", formData.mistaKonani());
        model.addAttribute("activePage", "zapasy");
        return "admin/zapasy/form";
    }

    @PostMapping("/novy")
    public String create(@RequestParam Long domaciTymId,
                         @RequestParam Long hosteTymId,
                         @RequestParam Long rocnikId,
                         @RequestParam(required = false) Long mistoKonaniId,
                         @RequestParam(required = false) String datumCas,
                         RedirectAttributes redirectAttributes) {

        // 1) Domácí a hosté nesmí být stejný tým
        if (domaciTymId.equals(hosteTymId)) {
            redirectAttributes.addFlashAttribute("error", "Tým nemůže hrát sám proti sobě.");
            return "redirect:/admin/zapasy/novy";
        }

        // 2) Ověření existence entit
        if (!adminZapasService.existsTym(domaciTymId)) {
            redirectAttributes.addFlashAttribute("error", "Domácí tým nebyl nalezen.");
            return "redirect:/admin/zapasy/novy";
        }

        if (!adminZapasService.existsTym(hosteTymId)) {
            redirectAttributes.addFlashAttribute("error", "Hostující tým nebyl nalezen.");
            return "redirect:/admin/zapasy/novy";
        }

        if (!adminZapasService.existsRocnik(rocnikId)) {
            redirectAttributes.addFlashAttribute("error", "Vybraný ročník nebyl nalezen.");
            return "redirect:/admin/zapasy/novy";
        }

        if (mistoKonaniId != null && !adminZapasService.existsMistoKonani(mistoKonaniId)) {
            redirectAttributes.addFlashAttribute("error", "Vybrané místo konání nebylo nalezeno.");
            return "redirect:/admin/zapasy/novy";
        }

        // 4) Datum a čas (volitelné), bezpečné parsování
        LocalDateTime parsedDatumCas = null;
        if (datumCas != null && !datumCas.isBlank()) {
            try {
                parsedDatumCas = LocalDateTime.parse(datumCas);
            } catch (DateTimeParseException e) {
                redirectAttributes.addFlashAttribute("error",
                        "Neplatný formát data a času. Použijte prosím validní datum.");
                return "redirect:/admin/zapasy/novy";
            }
        }

        adminZapasService.create(domaciTymId, hosteTymId, rocnikId, mistoKonaniId, parsedDatumCas);
        redirectAttributes.addFlashAttribute("success", "Zápas byl přidán.");
        return "redirect:/admin/zapasy";
    }

    // UC-02: Enter match result
    @GetMapping("/{id}/vysledek")
    public String vysledekForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        var formDataOpt = adminZapasService.getVysledekFormData(id);
        if (formDataOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Zápas nebyl nalezen.");
            return "redirect:/admin/zapasy";
        }
        var formData = formDataOpt.get();
        model.addAttribute("zapas", formData.zapas());
        model.addAttribute("domaciRegistrace", formData.domaciRegistrace());
        model.addAttribute("hosteRegistrace", formData.hosteRegistrace());
        model.addAttribute("selectedRegistraceIds", formData.selectedRegistraceIds());
        model.addAttribute("golyMap", formData.golyMap());
        model.addAttribute("activePage", "zapasy");
        return "admin/zapasy/vysledek";
    }

    @PostMapping("/{id}/vysledek")
    @Transactional
    public String vysledek(@PathVariable Long id,
                           @RequestParam Integer domaciSkore,
                           @RequestParam Integer hosteSkore,
                           @RequestParam(required = false) List<Long> registraceIds,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {

        if (domaciSkore == null || hosteSkore == null) {
            redirectAttributes.addFlashAttribute("error", "Skóre musí být vyplněno.");
            return "redirect:/admin/zapasy/" + id + "/vysledek";
        }

        if (domaciSkore < 0 || hosteSkore < 0) {
            redirectAttributes.addFlashAttribute("error", "Skóre nemůže být záporné.");
            return "redirect:/admin/zapasy/" + id + "/vysledek";
        }

        if (!adminZapasService.ulozVysledek(id, domaciSkore, hosteSkore, registraceIds, request.getParameterMap())) {
            redirectAttributes.addFlashAttribute("error", "Zápas nebyl nalezen.");
            return "redirect:/admin/zapasy";
        }

        redirectAttributes.addFlashAttribute("success",
                "Výsledek zápasu byl uložen. Tabulka se automaticky aktualizovala.");
        return "redirect:/admin/zapasy";
    }

    @PostMapping("/{id}/smazat")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!adminZapasService.delete(id)) {
            redirectAttributes.addFlashAttribute("error", "Zápas nebyl nalezen.");
            return "redirect:/admin/zapasy";
        }
        redirectAttributes.addFlashAttribute("success", "Zápas byl smazán.");
        return "redirect:/admin/zapasy";
    }
}
