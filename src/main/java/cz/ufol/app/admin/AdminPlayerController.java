package cz.ufol.app.admin;

import cz.ufol.app.player.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/hraci")
@RequiredArgsConstructor
public class AdminPlayerController {

	private final PlayerService playerService;

	@GetMapping
	public String list(@RequestParam(required = false) Long tymId, Model model) {
		var data = playerService.getAdminPlayerListData(tymId);
		model.addAttribute("tymy", data.teams());
		model.addAttribute("selectedTymId", data.selectedTeamId());
		model.addAttribute("aktivniRocnik", data.activeSeason());
		model.addAttribute("activePage", "hraci");
		model.addAttribute("error", data.error());
		model.addAttribute("hracStats", data.playerStats());
		return "admin/hraci/list";
	}

	@PostMapping("/novy")
	public String create(@RequestParam String jmeno,
						 @RequestParam String prijmeni,
						 @RequestParam(required = false) String datumNarozeni,
						 @RequestParam Long tymId,
						 RedirectAttributes redirectAttributes) {
		var result = playerService.createAdminPlayer(jmeno, prijmeni, datumNarozeni, tymId);
		redirectAttributes.addFlashAttribute(result.flashType(), result.flashMessage());
		return "redirect:" + result.redirectPath();
	}

	@PostMapping("/{id}/smazat")
	public String delete(@PathVariable Long id,
						 @RequestParam(required = false) Long tymId,
						 RedirectAttributes redirectAttributes) {
		var result = playerService.deleteAdminPlayer(id, tymId);
		redirectAttributes.addFlashAttribute(result.flashType(), result.flashMessage());
		return "redirect:" + result.redirectPath();
	}
}
