package cz.ufol.app.season;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RocnikService {

    private final RocnikRepository rocnikRepository;

    @Transactional
    public List<Rocnik> findAllByOrderByRokOdDesc() {
        return rocnikRepository.findAllByOrderByRokOdDesc();
    }

    @Transactional
    public Optional<Rocnik> findById(Long id) {
        return rocnikRepository.findById(id);
    }

    @Transactional
    public Optional<Rocnik> findByAktivniTrue() {
        return rocnikRepository.findByAktivniTrue();
    }

    @Transactional
    public boolean existsByNazevIgnoreCase(String nazev) {
        return rocnikRepository.existsByNazevIgnoreCase(nazev);
    }

    @Transactional
    public Rocnik save(Rocnik rocnik) {
        return rocnikRepository.save(rocnik);
    }

    @Transactional
    public void delete(Rocnik rocnik) {
        rocnikRepository.delete(rocnik);
    }

    @Transactional
    public void deactivateAll() {
        rocnikRepository.deactivateAll();
    }
}
