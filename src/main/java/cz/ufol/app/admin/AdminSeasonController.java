package cz.ufol.app.admin;

import cz.ufol.app.season.SeasonService;
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
public class AdminSeasonController {

    private final SeasonService seasonService;

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
        model.addAttribute("seasons", seasonService.findAllByYearFromDesc());
        model.addAttribute("activePage", "seasons");
        return "admin/seasons/list";
    }

    @GetMapping("/novy")
    public String createForm(Model model) {
        model.addAttribute("rocniky", seasonService.findAllByYearFromDesc());
        model.addAttribute("activePage", "seasons");
        return "admin/seasons/form";
    }

    @PostMapping("/novy")
    public String create(@RequestParam String nazev,
                         @RequestParam Integer rokOd,
                         @RequestParam Integer rokDo,
                         RedirectAttributes redirectAttributes) {
        var result = seasonService.createAdminSeason(nazev, rokOd, rokDo);
        redirectAttributes.addFlashAttribute(result.flashType(), result.flashMessage());
        return "redirect:" + result.redirectPath();
    }

    @PostMapping("/{id}/aktivovat")
    public String aktivovat(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var result = seasonService.activateSeason(id);
        redirectAttributes.addFlashAttribute(result.flashType(), result.flashMessage());
        return "redirect:" + result.redirectPath();
    }

    @PostMapping("/{id}/archivovat")
    public String archivovat(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var result = seasonService.archiveSeason(id);
        redirectAttributes.addFlashAttribute(result.flashType(), result.flashMessage());
        return "redirect:" + result.redirectPath();
    }

    @PostMapping("/{id}/smazat")
    public String smazat(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var result = seasonService.deleteSeason(id);
        redirectAttributes.addFlashAttribute(result.flashType(), result.flashMessage());
        return "redirect:" + result.redirectPath();
    }
}
