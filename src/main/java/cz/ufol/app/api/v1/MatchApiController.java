package cz.ufol.app.api.v1;

import cz.ufol.app.api.v1.dto.ApiMatchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matches")
@RequiredArgsConstructor
public class MatchApiController {

    private final ApiReadService apiReadService;

    @GetMapping("/upcoming")
    public List<ApiMatchDto> getUpcomingMatches() {
        return apiReadService.getUpcomingMatches();
    }

    @GetMapping("/played")
    public List<ApiMatchDto> getPlayedMatches() {
        return apiReadService.getPlayedMatches();
    }
}
