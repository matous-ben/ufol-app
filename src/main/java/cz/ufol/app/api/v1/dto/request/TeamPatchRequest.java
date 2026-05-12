package cz.ufol.app.api.v1.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TeamPatchRequest(
        @Size(min = 1, max = 100) String nazev,
        @Positive Long univerzitaId,
        Boolean aktivni
) {}
