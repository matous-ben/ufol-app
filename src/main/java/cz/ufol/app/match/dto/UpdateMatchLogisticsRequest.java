package cz.ufol.app.match.dto;

import java.time.LocalDateTime;

public record UpdateMatchLogisticsRequest(
        Long venueId,

        LocalDateTime dateTime
) {
}
