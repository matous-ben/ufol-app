package cz.ufol.app.api.v1.dto.response;

import cz.ufol.app.season.Rocnik;

public record SeasonResponse(
        Long id,
        String nazev,
        Integer rokOd,
        Integer rokDo,
        boolean aktivni
) {
    public static SeasonResponse from(Rocnik entity) {
        return new SeasonResponse(
                entity.getId(),
                entity.getNazev(),
                entity.getRokOd(),
                entity.getRokDo(),
                entity.isAktivni()
        );
    }
}
