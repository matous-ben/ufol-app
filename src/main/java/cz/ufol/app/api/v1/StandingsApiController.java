package cz.ufol.app.api.v1;

import cz.ufol.app.api.v1.dto.ApiStandingsRowDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/standings")
@RequiredArgsConstructor
public class StandingsApiController {

    private final ApiReadService apiReadService;

    @GetMapping
    public List<ApiStandingsRowDto> getStandings() {
        return apiReadService.getStandings();
    }
}
