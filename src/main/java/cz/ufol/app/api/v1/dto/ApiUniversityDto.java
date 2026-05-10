package cz.ufol.app.api.v1.dto;

public record ApiUniversityDto(
        Long id,
        String name,
        String shortcut,
        String logoFile
) {
}
