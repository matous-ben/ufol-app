package cz.ufol.app.api.v1;

import cz.ufol.app.api.v1.dto.ApiMatchDto;
import cz.ufol.app.api.v1.dto.ApiPlayerStatsDto;
import cz.ufol.app.api.v1.dto.ApiStandingsRowDto;
import cz.ufol.app.api.v1.dto.ApiTeamDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ApiV1Controller {

    private final ApiQueryService apiQueryService;

    @GetMapping("/teams")
    public List<ApiTeamDto> teams() {
        return apiQueryService.getTeams();
    }

    @GetMapping("/teams/{id}/players")
    public List<ApiPlayerStatsDto> teamPlayers(@PathVariable("id") Long teamId) {
        return apiQueryService.getTeamPlayersStats(teamId);
    }

    @GetMapping("/matches/upcoming")
    public List<ApiMatchDto> upcomingMatches() {
        return apiQueryService.getUpcomingMatches();
    }

    @GetMapping("/matches/played")
    public List<ApiMatchDto> playedMatches() {
        return apiQueryService.getPlayedMatches();
    }

    @GetMapping("/standings")
    public List<ApiStandingsRowDto> standings() {
        return apiQueryService.getStandings();
    }
}
