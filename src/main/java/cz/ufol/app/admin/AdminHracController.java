package cz.ufol.app.admin;

import cz.ufol.app.player.HracService;
import cz.ufol.app.season.RocnikRepository;
import cz.ufol.app.team.TymRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
@RequestMapping("/admin/hraci")
@RequiredArgsConstructor
public class AdminHracController {

	private final HracService hracService;
	private final TymRepository tymRepository;
	private final RocnikRepository rocnikRepository;

	@GetMapping
	public String list(@RequestParam(required = false) Long tymId, Model model) {
		var aktivniRocnik = rocnikRepository.findByAktivniTrue().orElse(null);
		var tymy = tymRepository.findByAktivniTrue();

		model.addAttribute("tymy", tymy);
		model.addAttribute("selectedTymId", tymId);
		model.addAttribute("aktivniRocnik", aktivniRocnik);
		model.addAttribute("activePage", "hraci");

		if (aktivniRocnik == null) {
			model.addAttribute("error", "Nejprve nastavte aktivní ročník.");
			model.addAttribute("hracStats", List.of());
			return "admin/hraci/list";
		}

		if (tymId == null) {
			model.addAttribute("hracStats", List.of());
			return "admin/hraci/list";
		}

		var tymOpt = tymRepository.findById(tymId);
		if (tymOpt.isEmpty()) {
			model.addAttribute("error", "Vybraný tým nebyl nalezen.");
			model.addAttribute("hracStats", List.of());
			return "admin/hraci/list";
		}

		model.addAttribute("hracStats", hracService.najdiStatistikyTymuProRocnik(tymOpt.get(), aktivniRocnik));
		return "admin/hraci/list";
	}

	@PostMapping("/novy")
	public String create(@RequestParam String jmeno,
						 @RequestParam String prijmeni,
						 @RequestParam(required = false) String datumNarozeni,
						 @RequestParam Long tymId,
						 RedirectAttributes redirectAttributes) {

		String jmenoTrim = jmeno == null ? "" : jmeno.trim();
		String prijmeniTrim = prijmeni == null ? "" : prijmeni.trim();

		if (jmenoTrim.isBlank() || prijmeniTrim.isBlank()) {
			redirectAttributes.addFlashAttribute("error", "Jméno i příjmení jsou povinné.");
			return "redirect:/admin/hraci?tymId=" + tymId;
		}

		var aktivniRocnikOpt = rocnikRepository.findByAktivniTrue();
		if (aktivniRocnikOpt.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Nejprve nastavte aktivní ročník.");
			return "redirect:/admin/hraci?tymId=" + tymId;
		}

		var tymOpt = tymRepository.findById(tymId);
		if (tymOpt.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Vybraný tým nebyl nalezen.");
			return "redirect:/admin/hraci";
		}

		LocalDate parsedDatumNarozeni = null;
		if (datumNarozeni != null && !datumNarozeni.isBlank()) {
			try {
				parsedDatumNarozeni = LocalDate.parse(datumNarozeni);
			} catch (DateTimeParseException e) {
				redirectAttributes.addFlashAttribute("error", "Neplatný formát data narození.");
				return "redirect:/admin/hraci?tymId=" + tymId;
			}
		}

		hracService.createHracSRegistraci(
				jmenoTrim,
				prijmeniTrim,
				parsedDatumNarozeni,
				tymOpt.get(),
				aktivniRocnikOpt.get()
		);

		redirectAttributes.addFlashAttribute("success", "Hráč byl přidán do aktivního ročníku.");
		return "redirect:/admin/hraci?tymId=" + tymId;
	}

	@PostMapping("/{id}/smazat")
	public String delete(@PathVariable Long id,
						 @RequestParam(required = false) Long tymId,
						 RedirectAttributes redirectAttributes) {
		hracService.smazatHraceVcetneHistorie(id);
		redirectAttributes.addFlashAttribute("success", "Hráč byl odebrán.");
		return tymId == null ? "redirect:/admin/hraci" : "redirect:/admin/hraci?tymId=" + tymId;
	}
}
