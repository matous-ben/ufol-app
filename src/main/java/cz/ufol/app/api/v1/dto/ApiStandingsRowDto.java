package cz.ufol.app.api.v1.dto;

public record ApiStandingsRowDto(
        int poradi,
        Long tymId,
        String nazevTymu,
        int odehrane,
        int vyhry,
        int remizy,
        int prohry,
        int vstreleneGoly,
        int obdrzeneGoly,
        int goloveSkore,
        int body,
        String univerzitaLogoFile
) {
}
