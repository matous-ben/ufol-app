package cz.ufol.app.match.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RecordMatchResultRequest(
        @NotNull @Min(0) @Max(99)
        Integer homeTeamScore,

        @NotNull @Min(0) @Max(99)
        Integer awayTeamScore,

        @NotNull
        List<@Valid PlayerStatsRequest> playerParticipations
) {
    public record PlayerStatsRequest(
            @NotNull
            Long registrationId,

            @NotNull
            @Min(0)
            Integer goals) {
    }
}
