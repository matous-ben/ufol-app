package cz.ufol.app.api.v1.dto;

public record ApiStandingsRowDto(
        Long teamId,
        String teamName,
        int played,
        int wins,
        int draws,
        int losses,
        int goalsFor,
        int goalsAgainst,
        int points,
        String teamLogoFile
) {
}
