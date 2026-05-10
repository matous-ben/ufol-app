package cz.ufol.app.admin;

import cz.ufol.app.player.HracService;
import cz.ufol.app.player.HracStatView;
import cz.ufol.app.season.Rocnik;
import cz.ufol.app.season.RocnikService;
import cz.ufol.app.team.Tym;
import cz.ufol.app.team.TymService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminHracService {

    private final HracService hracService;
    private final TymService tymService;
    private final RocnikService rocnikService;

    @Transactional(readOnly = true)
    public Optional<Rocnik> findAktivniRocnik() {
        return rocnikService.findByAktivniTrue();
    }

    @Transactional(readOnly = true)
    public List<Tym> findAktivniTymy() {
        return tymService.findAllAktivni();
    }

    @Transactional(readOnly = true)
    public Optional<Tym> findTymById(Long tymId) {
        return tymService.findById(tymId);
    }

    @Transactional(readOnly = true)
    public List<HracStatView> najdiStatistikyTymuProRocnik(Tym tym, Rocnik rocnik) {
        return hracService.najdiStatistikyTymuProRocnik(tym, rocnik);
    }

    @Transactional
    public void createHracSRegistraci(String jmeno, String prijmeni, LocalDate datumNarozeni, Long tymId, Rocnik rocnik) {
        Tym tym = tymService.findByIdOrThrow(tymId);
        hracService.createHracSRegistraci(jmeno, prijmeni, datumNarozeni, tym, rocnik);
    }

    @Transactional
    public void smazatHraceVcetneHistorie(Long id) {
        hracService.smazatHraceVcetneHistorie(id);
    }
}
