package cz.ufol.app.match.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateMatchRequest(
        @NotNull(message = "Home team ID is required")
        Long homeTeamId,

        @NotNull(message = "Away team ID is required")
        Long awayTeamId,

        @NotNull
        Long seasonId,

        Long venueId,

        @NotNull
        LocalDateTime dateTime
) {
}
