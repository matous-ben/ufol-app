package cz.ufol.app.team;

import cz.ufol.app.player.PlayerService;
import cz.ufol.app.season.SeasonRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Tag(name = "Týmy", description = "Přehled týmů")
public class TeamController {

    private final TeamService teamService;
    private final TeamRepository teamRepository;
    private final SeasonRepository seasonRepository;
    private final PlayerService playerService;

    @GetMapping("/tymy")
    @Operation(summary = "Zobrazit všechny týmy")
    @ApiResponse(
            responseCode = "200",
            description = "Úspěšně vyrenderovaná HTML stránka",
            content = @Content(
                    mediaType = "text/html",
                    schema = @Schema(type = "string")
            )
    )
    public String teams(Model model) {
        model.addAttribute("tymy", teamService.findAllActive());
        model.addAttribute("activePage", "teams");
        return "teams";
    }

    @GetMapping("/tymy/{id}")
    public String teamDetail(@PathVariable Long id, Model model) {
        var team = teamRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var activeSeason = seasonRepository.findByActiveTrue().orElse(null);
        var playerStats = activeSeason != null
                ? playerService.findTeamStatisticsForSeason(team, activeSeason)
                : List.of();

        model.addAttribute("tym", team);
        model.addAttribute("hracStats", playerStats);
        model.addAttribute("activePage", "teams");
        return "team-detail";
    }
}
