package cz.ufol.app.admin;

import cz.ufol.app.match.Zapas;
import cz.ufol.app.match.ZapasRepository;
import cz.ufol.app.match.UcastVZapase;
import cz.ufol.app.match.UcastVZapaseRepository;
import cz.ufol.app.player.RegistraceRepository;
import cz.ufol.app.season.RocnikRepository;
import cz.ufol.app.team.TymRepository;
import cz.ufol.app.venue.MistoKonaniRepository;
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/zapasy")
@RequiredArgsConstructor
@Tag(name = "Admin - zápasy", description = "Správa zápasů — vyžaduje přihlášení")
public class AdminZapasController {

    private final ZapasRepository zapasRepository;
    private final TymRepository tymRepository;
    private final RocnikRepository rocnikRepository;
    private final MistoKonaniRepository mistoKonaniRepository;
    private final RegistraceRepository registraceRepository;
    private final UcastVZapaseRepository ucastVZapaseRepository;

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
        var aktivniRocnik = rocnikRepository.findByAktivniTrue().orElse(null);

        if (aktivniRocnik != null) {
            model.addAttribute("naplanovane",
                    zapasRepository.findByRocnikAndOdehranFalseOrderByDatumCasAsc(aktivniRocnik));
            model.addAttribute("odehrane",
                    zapasRepository.findByRocnikAndOdehranTrueOrderByDatumCasDesc(aktivniRocnik));
        }

        model.addAttribute("aktivniRocnik", aktivniRocnik);
        model.addAttribute("activePage", "zapasy");
        return "admin/zapasy/list";
    }

    @GetMapping("/novy")
    public String createForm(Model model) {
        model.addAttribute("tymy", tymRepository.findByAktivniTrue());
        model.addAttribute("rocniky", rocnikRepository.findAllByOrderByRokOdDesc());
        model.addAttribute("mistaKonani", mistoKonaniRepository.findAllByOrderByNazevAsc());
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
        var domaciOpt = tymRepository.findById(domaciTymId);
        if (domaciOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Domácí tým nebyl nalezen.");
            return "redirect:/admin/zapasy/novy";
        }

        var hosteOpt = tymRepository.findById(hosteTymId);
        if (hosteOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Hostující tým nebyl nalezen.");
            return "redirect:/admin/zapasy/novy";
        }

        var rocnikOpt = rocnikRepository.findById(rocnikId);
        if (rocnikOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vybraný ročník nebyl nalezen.");
            return "redirect:/admin/zapasy/novy";
        }

        // 3) Místo je volitelné, ale pokud je ID zaslané, musí existovat
        var misto = mistoKonaniId != null
                ? mistoKonaniRepository.findById(mistoKonaniId).orElse(null)
                : null;

        if (mistoKonaniId != null && misto == null) {
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

        var zapas = Zapas.builder()
                .domaciTym(domaciOpt.get())
                .hosteTym(hosteOpt.get())
                .rocnik(rocnikOpt.get())
                .mistoKonani(misto)
                .datumCas(parsedDatumCas)
                .odehran(false)
                .domaciSkore(0)
                .hosteSkore(0)
                .build();

        zapasRepository.save(zapas);
        redirectAttributes.addFlashAttribute("success", "Zápas byl přidán.");
        return "redirect:/admin/zapasy";
    }

    // UC-02: Enter match result
    @GetMapping("/{id}/vysledek")
    public String vysledekForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        var zapasOpt = zapasRepository.findById(id);
        if (zapasOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Zápas nebyl nalezen.");
            return "redirect:/admin/zapasy";
        }

        var zapas = zapasOpt.get();

        var domaciRegistrace = registraceRepository
                .findByRocnikAndTymOrderByHracPrijmeniAscHracJmenoAsc(zapas.getRocnik(), zapas.getDomaciTym());
        var hosteRegistrace = registraceRepository
                .findByRocnikAndTymOrderByHracPrijmeniAscHracJmenoAsc(zapas.getRocnik(), zapas.getHosteTym());

        var ucasti = ucastVZapaseRepository.findByZapas(zapas);
        Set<Long> selectedRegistraceIds = ucasti.stream()
                .map(u -> u.getRegistrace().getId())
                .collect(Collectors.toSet());
        Map<Long, Integer> golyMap = ucasti.stream()
                .collect(Collectors.toMap(u -> u.getRegistrace().getId(), UcastVZapase::getGoly));

        model.addAttribute("zapas", zapas);
        model.addAttribute("domaciRegistrace", domaciRegistrace);
        model.addAttribute("hosteRegistrace", hosteRegistrace);
        model.addAttribute("selectedRegistraceIds", selectedRegistraceIds);
        model.addAttribute("golyMap", golyMap);
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

        var zapasOpt = zapasRepository.findById(id);
        if (zapasOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Zápas nebyl nalezen.");
            return "redirect:/admin/zapasy";
        }

        var zapas = zapasOpt.get();
        zapas.setDomaciSkore(domaciSkore);
        zapas.setHosteSkore(hosteSkore);
        zapas.setOdehran(true);
        zapasRepository.save(zapas);

        var domaciRegistrace = registraceRepository
                .findByRocnikAndTymOrderByHracPrijmeniAscHracJmenoAsc(zapas.getRocnik(), zapas.getDomaciTym());
        var hosteRegistrace = registraceRepository
                .findByRocnikAndTymOrderByHracPrijmeniAscHracJmenoAsc(zapas.getRocnik(), zapas.getHosteTym());

        Set<Long> povoleneRegistraceIds = java.util.stream.Stream.concat(
                        domaciRegistrace.stream().map(r -> r.getId()),
                        hosteRegistrace.stream().map(r -> r.getId())
                )
                .collect(Collectors.toSet());

        ucastVZapaseRepository.deleteByZapas(zapas);

        if (registraceIds != null && !registraceIds.isEmpty()) {
            var validniRegistrace = registraceRepository.findAllById(registraceIds).stream()
                    .filter(r -> povoleneRegistraceIds.contains(r.getId()))
                    .toList();

            for (var registrace : validniRegistrace) {
                String golyRaw = request.getParameter("goly_" + registrace.getId());
                int goly = 0;
                if (golyRaw != null && !golyRaw.isBlank()) {
                    try {
                        goly = Integer.parseInt(golyRaw);
                    } catch (NumberFormatException ignored) {
                        goly = 0;
                    }
                }
                if (goly < 0) {
                    goly = 0;
                }

                ucastVZapaseRepository.save(UcastVZapase.builder()
                        .zapas(zapas)
                        .registrace(registrace)
                        .goly(goly)
                        .build());
            }
        }

        redirectAttributes.addFlashAttribute("success",
                "Výsledek zápasu byl uložen. Tabulka se automaticky aktualizovala.");
        return "redirect:/admin/zapasy";
    }

    @PostMapping("/{id}/smazat")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!zapasRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("error", "Zápas nebyl nalezen.");
            return "redirect:/admin/zapasy";
        }

        zapasRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Zápas byl smazán.");
        return "redirect:/admin/zapasy";
    }
}
