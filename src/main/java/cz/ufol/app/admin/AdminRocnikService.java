package cz.ufol.app.admin;

import cz.ufol.app.season.Rocnik;
import cz.ufol.app.season.RocnikRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminRocnikService {

    private final RocnikRepository rocnikRepository;

    @Transactional(readOnly = true)
    public List<Rocnik> findAllByOrderByRokOdDesc() {
        return rocnikRepository.findAllByOrderByRokOdDesc();
    }

    @Transactional
    public OperationResult create(String nazev, Integer rokOd, Integer rokDo) {
        String trimmedNazev = nazev != null ? nazev.trim() : "";

        if (trimmedNazev.isBlank()) {
            return OperationResult.error("Název ročníku je povinný.");
        }
        if (rokOd == null || rokDo == null) {
            return OperationResult.error("Rok od i rok do jsou povinné.");
        }
        if (rokOd < 2000 || rokOd > 2100 || rokDo < 2000 || rokDo > 2100) {
            return OperationResult.error("Rok musí být v intervalu 2000-2100.");
        }
        if (rokDo <= rokOd) {
            return OperationResult.error("Rok do musí být větší než rok od.");
        }
        if (rocnikRepository.existsByNazevIgnoreCase(trimmedNazev)) {
            return OperationResult.error("Ročník s tímto názvem již existuje.");
        }

        rocnikRepository.save(Rocnik.builder()
                .nazev(trimmedNazev)
                .rokOd(rokOd)
                .rokDo(rokDo)
                .aktivni(false)
                .build());

        return OperationResult.success("Ročník byl vytvořen.");
    }

    @Transactional
    public OperationResult aktivovat(Long id) {
        var rocnik = rocnikRepository.findById(id).orElse(null);
        if (rocnik == null) {
            return OperationResult.error("Ročník nebyl nalezen.");
        }
        rocnikRepository.deactivateAll();
        rocnik.setAktivni(true);
        rocnikRepository.save(rocnik);
        return OperationResult.success("Ročník " + rocnik.getNazev() + " aktivován.");
    }

    @Transactional
    public OperationResult archivovat(Long id) {
        var rocnik = rocnikRepository.findById(id).orElse(null);
        if (rocnik == null) {
            return OperationResult.error("Ročník nebyl nalezen.");
        }
        rocnik.setAktivni(false);
        rocnikRepository.save(rocnik);
        return OperationResult.success("Ročník byl archivován.");
    }

    @Transactional
    public OperationResult smazat(Long id) {
        var rocnik = rocnikRepository.findById(id).orElse(null);
        if (rocnik == null) {
            return OperationResult.error("Ročník nebyl nalezen.");
        }
        if (rocnik.isAktivni()) {
            return OperationResult.error("Aktivní ročník nelze smazat. Nejprve ho archivujte.");
        }
        try {
            rocnikRepository.delete(rocnik);
            return OperationResult.success("Ročník byl smazán.");
        } catch (DataIntegrityViolationException e) {
            return OperationResult.error("Ročník nelze smazat, protože jsou na něj navázány týmy nebo zápasy.");
        }
    }

    public record OperationResult(boolean success, String message) {
        public static OperationResult success(String message) {
            return new OperationResult(true, message);
        }

        public static OperationResult error(String message) {
            return new OperationResult(false, message);
        }
    }
}
