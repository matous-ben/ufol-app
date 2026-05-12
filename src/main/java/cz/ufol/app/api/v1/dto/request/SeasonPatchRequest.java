package cz.ufol.app.api.v1.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SeasonPatchRequest(
        @Size(max = 50) @Pattern(regexp = ".*\\S.*") String nazev,
        @Min(2000) @Max(2100) Integer rokOd,
        @Min(2000) @Max(2100) Integer rokDo,
        Boolean aktivni
) {}
