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
        var data = adminZapasService.getListData();
        model.addAttribute("naplanovane", data.naplanovane());
        model.addAttribute("odehrane", data.odehrane());
        model.addAttribute("aktivniRocnik", data.aktivniRocnik());
        model.addAttribute("activePage", "zapasy");
        return "admin/zapasy/list";
    }

    @GetMapping("/novy")
    public String createForm(Model model) {
        var data = adminZapasService.getCreateFormData();
        model.addAttribute("tymy", data.tymy());
        model.addAttribute("rocniky", data.rocniky());
        model.addAttribute("mistaKonani", data.mistaKonani());
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
        String error = adminZapasService.create(domaciTymId, hosteTymId, rocnikId, mistoKonaniId, datumCas);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            return "redirect:/admin/zapasy/novy";
        }
        redirectAttributes.addFlashAttribute("success", "Zápas byl přidán.");
        return "redirect:/admin/zapasy";
    }

    // UC-02: Enter match result
    @GetMapping("/{id}/vysledek")
    public String vysledekForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        var data = adminZapasService.getResultFormData(id);
        if (data == null) {
            redirectAttributes.addFlashAttribute("error", "Zápas nebyl nalezen.");
            return "redirect:/admin/zapasy";
        }
        model.addAttribute("zapas", data.zapas());
        model.addAttribute("domaciRegistrace", data.domaciRegistrace());
        model.addAttribute("hosteRegistrace", data.hosteRegistrace());
        model.addAttribute("selectedRegistraceIds", data.selectedRegistraceIds());
        model.addAttribute("golyMap", data.golyMap());
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
        String error = adminZapasService.saveResult(id, domaciSkore, hosteSkore, registraceIds, request);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            return error.equals("Zápas nebyl nalezen.")
                    ? "redirect:/admin/zapasy"
                    : "redirect:/admin/zapasy/" + id + "/vysledek";
        }
        redirectAttributes.addFlashAttribute("success",
                "Výsledek zápasu byl uložen. Tabulka se automaticky aktualizovala.");
        return "redirect:/admin/zapasy";
    }

    @PostMapping("/{id}/smazat")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!adminZapasService.existsById(id)) {
            redirectAttributes.addFlashAttribute("error", "Zápas nebyl nalezen.");
            return "redirect:/admin/zapasy";
        }

        adminZapasService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Zápas byl smazán.");
        return "redirect:/admin/zapasy";
    }
}
