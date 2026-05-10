package cz.ufol.app.team;

import cz.ufol.app.season.Rocnik;
import cz.ufol.app.season.RocnikRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TymService {
    private final TymRepository tymRepository;
    private final RocnikRepository rocnikRepository;

    @Transactional(readOnly = true)
    public List<Tym> findAllAktivni() {
        return tymRepository.findByAktivniTrue();
    }

    @Transactional(readOnly = true)
    public Tym findByIdOrThrow(Long id) {
        return tymRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Optional<Rocnik> findAktivniRocnik() {
        return rocnikRepository.findByAktivniTrue();
    }
}
