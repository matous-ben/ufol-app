package cz.ufol.app.api.v1.dto;

public record ApiTeamDto(
        Long id,
        String name,
        boolean active,
        ApiUniversityDto university
) {
}
