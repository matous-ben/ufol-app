package cz.ufol.app.admin;

import cz.ufol.app.team.Tym;
import cz.ufol.app.team.TymService;
import cz.ufol.app.university.UniverzitaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminTymService {

    private final TymService tymService;
    private final UniverzitaService univerzitaService;

    @Transactional(readOnly = true)
    public List<Tym> findAll() {
        return tymService.findAllByOrderByNazevAsc();
    }

    @Transactional(readOnly = true)
    public boolean existsByNazevIgnoreCase(String nazev) {
        return tymService.existsByNazevIgnoreCase(nazev);
    }

    @Transactional(readOnly = true)
    public boolean existsByNazevIgnoreCaseAndIdNot(String nazev, Long id) {
        return tymService.existsByNazevIgnoreCaseAndIdNot(nazev, id);
    }

    @Transactional(readOnly = true)
    public Tym findByIdOrThrow(Long id) {
        return tymService.findByIdOrThrow(id);
    }

    @Transactional
    public Tym create(String nazev, Long univerzitaId, boolean aktivni) {
        Tym tym = Tym.builder()
                .nazev(nazev.trim())
                .univerzita(univerzitaService.findByIdOrThrow(univerzitaId))
                .aktivni(aktivni)
                .build();
        return tymService.save(tym);
    }

    @Transactional
    public Tym edit(Long id, String nazev, Long univerzitaId, boolean aktivni) {
        Tym tym = tymService.findByIdOrThrow(id);
        tym.setNazev(nazev.trim());
        tym.setUniverzita(univerzitaService.findByIdOrThrow(univerzitaId));
        tym.setAktivni(aktivni);
        return tymService.save(tym);
    }

    @Transactional
    public void deleteById(Long id) {
        tymService.deleteById(id);
    }
}
