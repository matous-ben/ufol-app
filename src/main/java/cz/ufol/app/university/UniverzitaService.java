package cz.ufol.app.university;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UniverzitaService {

    private final UniverzitaRepository univerzitaRepository;

    @Transactional(readOnly = true)
    public List<Univerzita> findAllByOrderByNazevAsc() {
        return univerzitaRepository.findAllByOrderByNazevAsc();
    }

    @Transactional(readOnly = true)
    public Univerzita findByIdOrThrow(Long id) {
        return univerzitaRepository.findById(id).orElseThrow();
    }
}
