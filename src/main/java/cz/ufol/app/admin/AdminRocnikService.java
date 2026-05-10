package cz.ufol.app.admin;

import cz.ufol.app.season.Rocnik;
import cz.ufol.app.season.RocnikService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminRocnikService {

    private final RocnikService rocnikService;

    @Transactional(readOnly = true)
    public List<Rocnik> findAll() {
        return rocnikService.findAllByOrderByRokOdDesc();
    }

    @Transactional(readOnly = true)
    public Optional<Rocnik> findById(Long id) {
        return rocnikService.findById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByNazevIgnoreCase(String nazev) {
        return rocnikService.existsByNazevIgnoreCase(nazev);
    }

    @Transactional
    public Rocnik save(Rocnik rocnik) {
        return rocnikService.save(rocnik);
    }

    @Transactional
    public void activate(Rocnik rocnik) {
        rocnikService.deactivateAll();
        rocnik.setAktivni(true);
        rocnikService.save(rocnik);
    }

    @Transactional
    public void archive(Rocnik rocnik) {
        rocnik.setAktivni(false);
        rocnikService.save(rocnik);
    }

    @Transactional
    public void delete(Rocnik rocnik) {
        rocnikService.delete(rocnik);
    }
}
