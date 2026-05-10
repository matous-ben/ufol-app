package cz.ufol.app.api.v1.dto;

import java.time.LocalDateTime;

public record ApiMatchDto(
        Long id,
        LocalDateTime datumCas,
        boolean odehran,
        Integer domaciSkore,
        Integer hosteSkore,
        Long domaciTymId,
        String domaciTymNazev,
        Long hosteTymId,
        String hosteTymNazev,
        String mistoKonani,
        Long rocnikId,
        String rocnikNazev
) {
}
