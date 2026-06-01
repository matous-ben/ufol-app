package cz.ufol.app.season;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeasonService {

    private final SeasonRepository seasonRepository;

    public record ServiceResult(String redirectPath, String flashType, String flashMessage) {}

    @Transactional(readOnly = true)
    public List<Season> findAllByYearFromDesc() {
        return seasonRepository.findAllByOrderByYearFromDesc();
    }

    @Transactional
    public ServiceResult createAdminSeason(String name, Integer yearFrom, Integer yearTo) {
        String trimmedName = name != null ? name.trim() : "";

        if (trimmedName.isBlank()) {
            return new ServiceResult("/admin/seasons/novy", "error", "Název ročníku je povinný.");
        }
        if (yearFrom == null || yearTo == null) {
            return new ServiceResult("/admin/seasons/novy", "error", "Rok od i rok do jsou povinné.");
        }
        if (yearFrom < 2000 || yearFrom > 2100 || yearTo < 2000 || yearTo > 2100) {
            return new ServiceResult("/admin/seasons/novy", "error", "Rok musí být v intervalu 2000-2100.");
        }
        if (yearTo <= yearFrom) {
            return new ServiceResult("/admin/seasons/novy", "error", "Rok do musí být větší než rok od.");
        }
        if (seasonRepository.existsByNameIgnoreCase(trimmedName)) {
            return new ServiceResult("/admin/seasons/novy", "error", "Ročník s tímto názvem již existuje.");
        }

        seasonRepository.save(Season.builder()
                .name(trimmedName)
                .yearFrom(yearFrom)
                .yearTo(yearTo)
                .active(false)
                .build());

        return new ServiceResult("/admin/seasons", "success", "Ročník byl vytvořen.");
    }

    @Transactional
    public ServiceResult activateSeason(Long id) {
        var season = seasonRepository.findById(id).orElse(null);
        if (season == null) {
            return new ServiceResult("/admin/seasons", "error", "Ročník nebyl nalezen.");
        }
        seasonRepository.deactivateAll();
        season.setActive(true);
        seasonRepository.save(season);
        return new ServiceResult("/admin/seasons", "success", "Ročník " + season.getName() + " aktivován.");
    }

    @Transactional
    public ServiceResult archiveSeason(Long id) {
        var seasonOpt = seasonRepository.findById(id);
        if (seasonOpt.isEmpty()) {
            return new ServiceResult("/admin/seasons", "error", "Ročník nebyl nalezen.");
        }
        var season = seasonOpt.get();
        season.setActive(false);
        seasonRepository.save(season);
        return new ServiceResult("/admin/seasons", "success", "Ročník byl archivován.");
    }

    @Transactional
    public ServiceResult deleteSeason(Long id) {
        var seasonOpt = seasonRepository.findById(id);
        if (seasonOpt.isEmpty()) {
            return new ServiceResult("/admin/seasons", "error", "Ročník nebyl nalezen.");
        }

        var season = seasonOpt.get();
        if (season.isActive()) {
            return new ServiceResult("/admin/seasons", "error", "Aktivní ročník nelze smazat. Nejprve ho archivujte.");
        }

        try {
            seasonRepository.delete(season);
            return new ServiceResult("/admin/seasons", "success", "Ročník byl smazán.");
        } catch (DataIntegrityViolationException e) {
            return new ServiceResult("/admin/seasons", "error", "Ročník nelze smazat, protože jsou na něj navázány týmy nebo zápasy.");
        }
    }
}
