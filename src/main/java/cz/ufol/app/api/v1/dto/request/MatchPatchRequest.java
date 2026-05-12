package cz.ufol.app.api.v1.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

public record MatchPatchRequest(
        @Positive Long rocnikId,
        @Positive Long domaciTymId,
        @Positive Long hosteTymId,
        @Positive Long mistoKonaniId,
        LocalDateTime datumCas,
        Boolean odehran,
        @PositiveOrZero Integer domaciSkore,
        @PositiveOrZero Integer hosteSkore
) {}
