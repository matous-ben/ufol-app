package cz.ufol.app.api.v1.dto;

public record ApiPlayerStatsDto(
        Long hracId,
        String jmeno,
        String prijmeni,
        long odehraneZapasy,
        long goly
) {
}
