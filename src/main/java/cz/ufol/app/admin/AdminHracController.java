package cz.ufol.app.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/hraci")
@RequiredArgsConstructor
public class AdminHracController {

	private final AdminHracService adminHracService;

	@GetMapping
	public String list(@RequestParam(required = false) Long tymId, Model model) {
		var aktivniRocnik = adminHracService.findAktivniRocnikOrNull();
		var tymy = adminHracService.findAktivniTymy();

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

		if (!adminHracService.tymExists(tymId)) {
			model.addAttribute("error", "Vybraný tým nebyl nalezen.");
			model.addAttribute("hracStats", List.of());
			return "admin/hraci/list";
		}

		model.addAttribute("hracStats", adminHracService.findHracStats(tymId, aktivniRocnik));
		return "admin/hraci/list";
	}

	@PostMapping("/novy")
	public String create(@RequestParam String jmeno,
						 @RequestParam String prijmeni,
						 @RequestParam(required = false) String datumNarozeni,
						 @RequestParam Long tymId,
						 RedirectAttributes redirectAttributes) {
		String error = adminHracService.create(jmeno, prijmeni, datumNarozeni, tymId);
		if (error != null) {
			redirectAttributes.addFlashAttribute("error", error);
			return error.equals("Vybraný tým nebyl nalezen.")
					? "redirect:/admin/hraci"
					: "redirect:/admin/hraci?tymId=" + tymId;
		}

		redirectAttributes.addFlashAttribute("success", "Hráč byl přidán do aktivního ročníku.");
		return "redirect:/admin/hraci?tymId=" + tymId;
	}

	@PostMapping("/{id}/smazat")
	public String delete(@PathVariable Long id,
						 @RequestParam(required = false) Long tymId,
						 RedirectAttributes redirectAttributes) {
		adminHracService.delete(id);
		redirectAttributes.addFlashAttribute("success", "Hráč byl odebrán.");
		return tymId == null ? "redirect:/admin/hraci" : "redirect:/admin/hraci?tymId=" + tymId;
	}
}
