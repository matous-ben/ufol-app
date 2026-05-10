package cz.ufol.app.admin;

import cz.ufol.app.team.Tym;
import cz.ufol.app.team.TymRepository;
import cz.ufol.app.university.Univerzita;
import cz.ufol.app.university.UniverzitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminTymService {

    private final TymRepository tymRepository;
    private final UniverzitaRepository univerzitaRepository;

    @Transactional(readOnly = true)
    public List<Tym> findAllTymy() {
        return tymRepository.findAllByOrderByNazevAsc();
    }

    @Transactional(readOnly = true)
    public List<Univerzita> findAllUniverzity() {
        return univerzitaRepository.findAllByOrderByNazevAsc();
    }

    @Transactional(readOnly = true)
    public Tym findTymById(Long id) {
        return tymRepository.findById(id).orElseThrow();
    }

    @Transactional
    public String create(String nazev, Long univerzitaId, boolean aktivni) {
        String trimmed = nazev == null ? "" : nazev.trim();
        if (trimmed.isBlank()) {
            return "Název týmu je povinný.";
        }
        if (tymRepository.existsByNazevIgnoreCase(trimmed)) {
            return "Tým s názvem '" + trimmed + "' již existuje.";
        }

        var univerzita = univerzitaRepository.findById(univerzitaId).orElseThrow();
        tymRepository.save(Tym.builder()
                .nazev(trimmed)
                .univerzita(univerzita)
                .aktivni(aktivni)
                .build());
        return null;
    }

    @Transactional
    public String edit(Long id, String nazev, Long univerzitaId, boolean aktivni) {
        String trimmed = nazev == null ? "" : nazev.trim();
        if (trimmed.isBlank()) {
            return "Název týmu je povinný.";
        }
        if (tymRepository.existsByNazevIgnoreCaseAndIdNot(trimmed, id)) {
            return "Tým s názvem '" + trimmed + "' již existuje.";
        }

        var tym = tymRepository.findById(id).orElseThrow();
        var univerzita = univerzitaRepository.findById(univerzitaId).orElseThrow();
        tym.setNazev(trimmed);
        tym.setUniverzita(univerzita);
        tym.setAktivni(aktivni);
        tymRepository.save(tym);
        return null;
    }

    @Transactional
    public void delete(Long id) {
        tymRepository.deleteById(id);
    }
}
