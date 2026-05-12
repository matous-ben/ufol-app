package cz.ufol.app.api.v1.dto.response;

import cz.ufol.app.player.Hrac;

import java.time.LocalDate;

public record PlayerResponse(
        Long id,
        String jmeno,
        String prijmeni,
        LocalDate datumNarozeni,
        String fotoUrl
) {
    public static PlayerResponse from(Hrac entity) {
        return new PlayerResponse(
                entity.getId(),
                entity.getJmeno(),
                entity.getPrijmeni(),
                entity.getDatumNarozeni(),
                entity.getFotoUrl()
        );
    }
}
