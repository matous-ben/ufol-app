package cz.ufol.app.api.v1.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SeasonCreateRequest(
        @NotBlank @Size(max = 50) String nazev,
        @NotNull @Min(2000) @Max(2100) Integer rokOd,
        @NotNull @Min(2000) @Max(2100) Integer rokDo,
        @NotNull Boolean aktivni
) {}
