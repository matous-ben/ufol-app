package cz.ufol.app.api.v1.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PlayerPatchRequest(
        @Pattern(regexp = ".*\\S.*") @Size(min = 1, max = 50) String jmeno,
        @Pattern(regexp = ".*\\S.*") @Size(min = 1, max = 50) String prijmeni,
        LocalDate datumNarozeni,
        @Size(max = 255) String fotoUrl
) {}
