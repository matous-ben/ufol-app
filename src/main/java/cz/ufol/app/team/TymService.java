package cz.ufol.app.team;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TymService {
    private final TymRepository tymRepository;

    @Transactional(readOnly = true)
    public List<Tym> findAllAktivni() {
        return tymRepository.findByAktivniTrue();
    }

    @Transactional(readOnly = true)
    public List<Tym> findAllByOrderByNazevAsc() {
        return tymRepository.findAllByOrderByNazevAsc();
    }

    @Transactional(readOnly = true)
    public Tym findByIdOrThrow(Long id) {
        return tymRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Optional<Tym> findById(Long id) {
        return tymRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByNazevIgnoreCase(String nazev) {
        return tymRepository.existsByNazevIgnoreCase(nazev);
    }

    @Transactional(readOnly = true)
    public boolean existsByNazevIgnoreCaseAndIdNot(String nazev, Long id) {
        return tymRepository.existsByNazevIgnoreCaseAndIdNot(nazev, id);
    }

    @Transactional
    public Tym save(Tym tym) {
        return tymRepository.save(tym);
    }

    @Transactional
    public void deleteById(Long id) {
        tymRepository.deleteById(id);
    }
}
