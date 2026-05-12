package cz.ufol.app.api.v1.dto.response;

import cz.ufol.app.team.Tym;

public record TeamResponse(
        Long id,
        String nazev,
        boolean aktivni,
        Long univerzitaId,
        String univerzitaNazev
) {
    public static TeamResponse from(Tym entity) {
        return new TeamResponse(
                entity.getId(),
                entity.getNazev(),
                entity.isAktivni(),
                entity.getUniverzita() != null ? entity.getUniverzita().getId() : null,
                entity.getUniverzita() != null ? entity.getUniverzita().getNazev() : null
        );
    }
}
