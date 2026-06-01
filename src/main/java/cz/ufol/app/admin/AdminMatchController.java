package cz.ufol.app.admin;

import cz.ufol.app.match.MatchService;
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

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@Controller
@RequestMapping("/admin/zapasy")
@RequiredArgsConstructor
@Tag(name = "Admin - zápasy", description = "Správa zápasů — vyžaduje přihlášení")
public class AdminMatchController {

    private final MatchService matchService;

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
        var data = matchService.getAdminZapasyListData();
        model.addAttribute("aktivniRocnik", data.aktivniSeason());
        model.addAttribute("naplanovane", data.naplanovane());
        model.addAttribute("odehrane", data.odehrane());
        model.addAttribute("activePage", "zapasy");
        return "admin/zapasy/list";
    }

    @GetMapping("/novy")
    public String createForm(Model model) {
        var data = matchService.getAdminZapasFormData();
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
        var result = matchService.createAdminZapas(domaciTymId, hosteTymId, rocnikId, mistoKonaniId, datumCas);
        redirectAttributes.addFlashAttribute(result.flashType(), result.flashMessage());
        return "redirect:" + result.redirectPath();
    }

    // UC-02: Enter match result
    @GetMapping("/{id}/vysledek")
    public String vysledekForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        var data = matchService.getAdminVysledekFormData(id);
        if (!data.found()) {
            redirectAttributes.addFlashAttribute("error", data.errorMessage());
            return "redirect:/admin/zapasy";
        }

        model.addAttribute("zapas", data.match());
        model.addAttribute("domaciRegistrace", data.domaciRegistration());
        model.addAttribute("hosteRegistrace", data.hosteRegistration());
        model.addAttribute("selectedRegistraceIds", data.selectedRegistraceIds());
        model.addAttribute("golyMap", data.golyMap());
        model.addAttribute("activePage", "zapasy");
        return "admin/zapasy/vysledek";
    }

    @PostMapping("/{id}/vysledek")
    public String vysledek(@PathVariable Long id,
                           @RequestParam Integer domaciSkore,
                           @RequestParam Integer hosteSkore,
                           @RequestParam(required = false) List<Long> registraceIds,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        var result = matchService.ulozAdminVysledek(id, domaciSkore, hosteSkore, registraceIds, request.getParameterMap());
        redirectAttributes.addFlashAttribute(result.flashType(), result.flashMessage());
        return "redirect:" + result.redirectPath();
    }

    @PostMapping("/{id}/smazat")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var result = matchService.smazAdminZapas(id);
        redirectAttributes.addFlashAttribute(result.flashType(), result.flashMessage());
        return "redirect:" + result.redirectPath();
    }
}
