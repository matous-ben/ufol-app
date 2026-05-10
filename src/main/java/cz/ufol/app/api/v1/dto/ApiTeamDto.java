package cz.ufol.app.api.v1.dto;

public record ApiTeamDto(
        Long id,
        String nazev,
        boolean aktivni,
        String univerzita,
        String univerzitaZkratka,
        String univerzitaLogoFile
) {
}
