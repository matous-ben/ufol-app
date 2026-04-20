package cz.ufol.app.team;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TymService {
    private final TymRepository tymRepository;

    public List<Tym> findAllAktivni() {
        return tymRepository.findAllByOrderByNazevAsc()
                .stream()
                .filter(Tym::isAktivni)
                .toList();
    }
}
