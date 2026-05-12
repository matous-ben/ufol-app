package cz.ufol.app.team;

import cz.ufol.app.university.Univerzita;
import cz.ufol.app.university.UniverzitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TymService {
    private final TymRepository tymRepository;
    private final UniverzitaRepository univerzitaRepository;

    public record ServiceResult(String redirectPath, String flashType, String flashMessage) {}
    public record AdminTymyFormData(Tym tym, List<Univerzita> univerzity, String formAction) {}

    @Transactional(readOnly = true)
    public List<Tym> findAllAktivni() {
        return tymRepository.findByAktivniTrue();
    }

    @Transactional(readOnly = true)
    public List<Tym> findAllAdminTymy() {
        return tymRepository.findAllByOrderByNazevAsc();
    }

    @Transactional(readOnly = true)
    public AdminTymyFormData getCreateFormData() {
        return new AdminTymyFormData(new Tym(), univerzitaRepository.findAllByOrderByNazevAsc(), "/admin/tymy/novy");
    }

    @Transactional(readOnly = true)
    public AdminTymyFormData getEditFormData(Long id) {
        return new AdminTymyFormData(
                tymRepository.findById(id).orElseThrow(),
                univerzitaRepository.findAllByOrderByNazevAsc(),
                "/admin/tymy/" + id + "/edit"
        );
    }

    @Transactional
    public ServiceResult createAdminTym(String nazev, Long univerzitaId, boolean aktivni) {
        String trimmedNazev = nazev == null ? "" : nazev.trim();
        if (tymRepository.existsByNazevIgnoreCase(trimmedNazev)) {
            return new ServiceResult("/admin/tymy/novy", "error", "Tým s názvem '" + trimmedNazev + "' již existuje.");
        }

        var univerzita = univerzitaRepository.findById(univerzitaId).orElseThrow();
        tymRepository.save(Tym.builder()
                .nazev(trimmedNazev)
                .univerzita(univerzita)
                .aktivni(aktivni)
                .build());
        return new ServiceResult("/admin/tymy", "success", "Tým byl úspěšně přidán.");
    }

    @Transactional
    public ServiceResult updateAdminTym(Long id, String nazev, Long univerzitaId, boolean aktivni) {
        String trimmedNazev = nazev == null ? "" : nazev.trim();
        if (tymRepository.existsByNazevIgnoreCaseAndIdNot(trimmedNazev, id)) {
            return new ServiceResult("/admin/tymy/" + id + "/edit", "error", "Tým s názvem '" + trimmedNazev + "' již existuje.");
        }

        var tym = tymRepository.findById(id).orElseThrow();
        var univerzita = univerzitaRepository.findById(univerzitaId).orElseThrow();
        tym.setNazev(trimmedNazev);
        tym.setUniverzita(univerzita);
        tym.setAktivni(aktivni);
        tymRepository.save(tym);

        return new ServiceResult("/admin/tymy", "success", "Tým byl upraven.");
    }

    @Transactional
    public ServiceResult deleteAdminTym(Long id) {
        tymRepository.deleteById(id);
        return new ServiceResult("/admin/tymy", "success", "Tým byl smazán.");
    }

    @Transactional(readOnly = true)
    public List<Tym> findAllApi() {
        return tymRepository.findAllByOrderByNazevAsc();
    }

    @Transactional(readOnly = true)
    public Tym findByIdApi(Long id) {
        return tymRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Team with id " + id + " not found"));
    }

    @Transactional
    public Tym createApi(String nazev, Long univerzitaId, boolean aktivni) {
        String trimmedNazev = nazev == null ? "" : nazev.trim();
        var univerzita = univerzitaRepository.findById(univerzitaId)
                .orElseThrow(() -> new NoSuchElementException("University with id " + univerzitaId + " not found"));

        return tymRepository.save(Tym.builder()
                .nazev(trimmedNazev)
                .univerzita(univerzita)
                .aktivni(aktivni)
                .build());
    }

    @Transactional
    public Tym updateApi(Long id, String nazev, Long univerzitaId, boolean aktivni) {
        var tym = tymRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Team with id " + id + " not found"));
        var univerzita = univerzitaRepository.findById(univerzitaId)
                .orElseThrow(() -> new NoSuchElementException("University with id " + univerzitaId + " not found"));

        tym.setNazev(nazev == null ? "" : nazev.trim());
        tym.setUniverzita(univerzita);
        tym.setAktivni(aktivni);
        return tymRepository.save(tym);
    }

    @Transactional
    public Tym patchApi(Long id, String nazev, Long univerzitaId, Boolean aktivni) {
        var tym = tymRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Team with id " + id + " not found"));

        if (nazev != null) {
            tym.setNazev(nazev.trim());
        }
        if (univerzitaId != null) {
            var univerzita = univerzitaRepository.findById(univerzitaId)
                    .orElseThrow(() -> new NoSuchElementException("University with id " + univerzitaId + " not found"));
            tym.setUniverzita(univerzita);
        }
        if (aktivni != null) {
            tym.setAktivni(aktivni);
        }

        return tymRepository.save(tym);
    }

    @Transactional
    public void deleteApi(Long id) {
        if (!tymRepository.existsById(id)) {
            throw new NoSuchElementException("Team with id " + id + " not found");
        }
        tymRepository.deleteById(id);
    }
}
