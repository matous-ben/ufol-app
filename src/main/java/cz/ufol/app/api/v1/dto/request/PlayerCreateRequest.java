package cz.ufol.app.api.v1.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PlayerCreateRequest(
        @NotBlank @Size(max = 50) String jmeno,
        @NotBlank @Size(max = 50) String prijmeni,
        LocalDate datumNarozeni,
        @Size(max = 255) String fotoUrl
) {}
