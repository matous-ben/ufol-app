package cz.ufol.app.season;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RocnikService {

    private final RocnikRepository rocnikRepository;

    public record ServiceResult(String redirectPath, String flashType, String flashMessage) {}

    @Transactional(readOnly = true)
    public List<Rocnik> findAllByRokOdDesc() {
        return rocnikRepository.findAllByOrderByRokOdDesc();
    }

    @Transactional
    public ServiceResult createAdminRocnik(String nazev, Integer rokOd, Integer rokDo) {
        String trimmedNazev = nazev != null ? nazev.trim() : "";

        if (trimmedNazev.isBlank()) {
            return new ServiceResult("/admin/rocniky/novy", "error", "Název ročníku je povinný.");
        }
        if (rokOd == null || rokDo == null) {
            return new ServiceResult("/admin/rocniky/novy", "error", "Rok od i rok do jsou povinné.");
        }
        if (rokOd < 2000 || rokOd > 2100 || rokDo < 2000 || rokDo > 2100) {
            return new ServiceResult("/admin/rocniky/novy", "error", "Rok musí být v intervalu 2000-2100.");
        }
        if (rokDo <= rokOd) {
            return new ServiceResult("/admin/rocniky/novy", "error", "Rok do musí být větší než rok od.");
        }
        if (rocnikRepository.existsByNazevIgnoreCase(trimmedNazev)) {
            return new ServiceResult("/admin/rocniky/novy", "error", "Ročník s tímto názvem již existuje.");
        }

        rocnikRepository.save(Rocnik.builder()
                .nazev(trimmedNazev)
                .rokOd(rokOd)
                .rokDo(rokDo)
                .aktivni(false)
                .build());

        return new ServiceResult("/admin/rocniky", "success", "Ročník byl vytvořen.");
    }

    @Transactional
    public ServiceResult aktivovatRocnik(Long id) {
        var rocnik = rocnikRepository.findById(id).orElse(null);
        if (rocnik == null) {
            return new ServiceResult("/admin/rocniky", "error", "Ročník nebyl nalezen.");
        }
        rocnikRepository.deactivateAll();
        rocnik.setAktivni(true);
        rocnikRepository.save(rocnik);
        return new ServiceResult("/admin/rocniky", "success", "Ročník " + rocnik.getNazev() + " aktivován.");
    }

    @Transactional
    public ServiceResult archivovatRocnik(Long id) {
        var rocnikOpt = rocnikRepository.findById(id);
        if (rocnikOpt.isEmpty()) {
            return new ServiceResult("/admin/rocniky", "error", "Ročník nebyl nalezen.");
        }
        var rocnik = rocnikOpt.get();
        rocnik.setAktivni(false);
        rocnikRepository.save(rocnik);
        return new ServiceResult("/admin/rocniky", "success", "Ročník byl archivován.");
    }

    @Transactional
    public ServiceResult smazRocnik(Long id) {
        var rocnikOpt = rocnikRepository.findById(id);
        if (rocnikOpt.isEmpty()) {
            return new ServiceResult("/admin/rocniky", "error", "Ročník nebyl nalezen.");
        }

        var rocnik = rocnikOpt.get();
        if (rocnik.isAktivni()) {
            return new ServiceResult("/admin/rocniky", "error", "Aktivní ročník nelze smazat. Nejprve ho archivujte.");
        }

        try {
            rocnikRepository.delete(rocnik);
            return new ServiceResult("/admin/rocniky", "success", "Ročník byl smazán.");
        } catch (DataIntegrityViolationException e) {
            return new ServiceResult("/admin/rocniky", "error", "Ročník nelze smazat, protože jsou na něj navázány týmy nebo zápasy.");
        }
    }

    @Transactional(readOnly = true)
    public List<Rocnik> findAllApi() {
        return rocnikRepository.findAllByOrderByRokOdDesc();
    }

    @Transactional(readOnly = true)
    public Rocnik findByIdApi(Long id) {
        return rocnikRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Season with id " + id + " not found"));
    }

    @Transactional
    public Rocnik createApi(String nazev, Integer rokOd, Integer rokDo, boolean aktivni) {
        if (nazev == null) {
            throw new IllegalArgumentException("Season name is required");
        }
        validateYears(rokOd, rokDo);
        if (aktivni) {
            rocnikRepository.deactivateAll();
        }
        return rocnikRepository.save(Rocnik.builder()
                .nazev(nazev.trim())
                .rokOd(rokOd)
                .rokDo(rokDo)
                .aktivni(aktivni)
                .build());
    }

    @Transactional
    public Rocnik updateApi(Long id, String nazev, Integer rokOd, Integer rokDo, boolean aktivni) {
        if (nazev == null) {
            throw new IllegalArgumentException("Season name is required");
        }
        validateYears(rokOd, rokDo);
        var rocnik = rocnikRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Season with id " + id + " not found"));

        if (aktivni) {
            rocnikRepository.deactivateAll();
        }

        rocnik.setNazev(nazev.trim());
        rocnik.setRokOd(rokOd);
        rocnik.setRokDo(rokDo);
        rocnik.setAktivni(aktivni);
        return rocnikRepository.save(rocnik);
    }

    @Transactional
    public Rocnik patchApi(Long id, String nazev, Integer rokOd, Integer rokDo, Boolean aktivni) {
        var rocnik = rocnikRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Season with id " + id + " not found"));

        if (nazev != null) {
            rocnik.setNazev(nazev.trim());
        }
        if (rokOd != null) {
            rocnik.setRokOd(rokOd);
        }
        if (rokDo != null) {
            rocnik.setRokDo(rokDo);
        }
        if (aktivni != null) {
            if (aktivni) {
                rocnikRepository.deactivateAll();
            }
            rocnik.setAktivni(aktivni);
        }
        validateYears(rocnik.getRokOd(), rocnik.getRokDo());
        return rocnikRepository.save(rocnik);
    }

    @Transactional
    public void deleteApi(Long id) {
        if (!rocnikRepository.existsById(id)) {
            throw new NoSuchElementException("Season with id " + id + " not found");
        }
        rocnikRepository.deleteById(id);
    }

    private void validateYears(Integer rokOd, Integer rokDo) {
        if (rokOd == null || rokDo == null) {
            throw new IllegalArgumentException("Season years are required");
        }
        if (rokDo <= rokOd) {
            throw new IllegalArgumentException("rokDo must be greater than rokOd");
        }
    }
}
