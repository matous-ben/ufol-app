package cz.ufol.app.match.dto;

import java.time.LocalDateTime;

public record MatchResponse (
        Long id,
        TeamSummary homeTeam,
        TeamSummary awayTeam,
        String season,
        String venue,
        LocalDateTime dateTime,
        boolean played,
        Integer homeScore,
        Integer awayScore
) {
    public record TeamSummary(Long id, String name, String universityLogo) {}
}
