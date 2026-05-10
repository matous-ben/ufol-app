package cz.ufol.app.api.v1.dto;

import java.time.LocalDateTime;

public record ApiMatchDto(
        Long id,
        ApiTeamDto homeTeam,
        ApiTeamDto awayTeam,
        LocalDateTime dateTime,
        boolean played,
        Integer homeScore,
        Integer awayScore,
        String venueName
) {
}
