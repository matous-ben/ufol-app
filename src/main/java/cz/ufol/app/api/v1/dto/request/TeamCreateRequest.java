package cz.ufol.app.api.v1.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TeamCreateRequest(
        @NotBlank @Size(max = 100) String nazev,
        @NotNull @Positive Long univerzitaId,
        @NotNull Boolean aktivni
) {}
