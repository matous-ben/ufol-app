package cz.ufol.app.api.v1;

import cz.ufol.app.api.v1.dto.ApiTeamDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
public class TeamApiController {

    private final ApiReadService apiReadService;

    @GetMapping
    public List<ApiTeamDto> getTeams() {
        return apiReadService.getActiveTeams();
    }

    @GetMapping("/{id}")
    public ApiTeamDto getTeam(@PathVariable Long id) {
        return apiReadService.getTeam(id);
    }
}
