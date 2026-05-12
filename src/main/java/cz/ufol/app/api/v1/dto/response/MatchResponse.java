package cz.ufol.app.api.v1.dto.response;

import cz.ufol.app.match.Zapas;

import java.time.LocalDateTime;

public record MatchResponse(
        Long id,
        Long rocnikId,
        String rocnikNazev,
        Long domaciTymId,
        String domaciTymNazev,
        Long hosteTymId,
        String hosteTymNazev,
        Long mistoKonaniId,
        String mistoKonaniNazev,
        LocalDateTime datumCas,
        boolean odehran,
        Integer domaciSkore,
        Integer hosteSkore
) {
    public static MatchResponse from(Zapas entity) {
        return new MatchResponse(
                entity.getId(),
                entity.getRocnik() != null ? entity.getRocnik().getId() : null,
                entity.getRocnik() != null ? entity.getRocnik().getNazev() : null,
                entity.getDomaciTym() != null ? entity.getDomaciTym().getId() : null,
                entity.getDomaciTym() != null ? entity.getDomaciTym().getNazev() : null,
                entity.getHosteTym() != null ? entity.getHosteTym().getId() : null,
                entity.getHosteTym() != null ? entity.getHosteTym().getNazev() : null,
                entity.getMistoKonani() != null ? entity.getMistoKonani().getId() : null,
                entity.getMistoKonani() != null ? entity.getMistoKonani().getNazev() : null,
                entity.getDatumCas(),
                entity.isOdehran(),
                entity.getDomaciSkore(),
                entity.getHosteSkore()
        );
    }
}
