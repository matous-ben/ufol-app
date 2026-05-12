package cz.ufol.app.api.v1.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

public record MatchCreateRequest(
        @NotNull @Positive Long rocnikId,
        @NotNull @Positive Long domaciTymId,
        @NotNull @Positive Long hosteTymId,
        @Positive Long mistoKonaniId,
        LocalDateTime datumCas,
        @NotNull Boolean odehran,
        @NotNull @PositiveOrZero Integer domaciSkore,
        @NotNull @PositiveOrZero Integer hosteSkore
) {}
