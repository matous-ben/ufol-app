package cz.ufol.app.admin;

import cz.ufol.app.player.HracService;
import cz.ufol.app.player.HracStatView;
import cz.ufol.app.season.Rocnik;
import cz.ufol.app.season.RocnikRepository;
import cz.ufol.app.team.Tym;
import cz.ufol.app.team.TymRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminHracService {

    private final HracService hracService;
    private final TymRepository tymRepository;
    private final RocnikRepository rocnikRepository;

    @Transactional(readOnly = true)
    public List<Tym> findAktivniTymy() {
        return tymRepository.findByAktivniTrue();
    }

    @Transactional(readOnly = true)
    public Rocnik findAktivniRocnikOrNull() {
        return rocnikRepository.findByAktivniTrue().orElse(null);
    }

    @Transactional(readOnly = true)
    public List<HracStatView> findHracStats(Long tymId, Rocnik aktivniRocnik) {
        if (aktivniRocnik == null || tymId == null) {
            return List.of();
        }
        var tym = tymRepository.findById(tymId).orElse(null);
        if (tym == null) {
            return List.of();
        }
        return hracService.najdiStatistikyTymuProRocnik(tym, aktivniRocnik);
    }

    @Transactional(readOnly = true)
    public boolean tymExists(Long tymId) {
        return tymRepository.findById(tymId).isPresent();
    }

    @Transactional
    public String create(String jmeno, String prijmeni, String datumNarozeni, Long tymId) {
        String jmenoTrim = jmeno == null ? "" : jmeno.trim();
        String prijmeniTrim = prijmeni == null ? "" : prijmeni.trim();

        if (jmenoTrim.isBlank() || prijmeniTrim.isBlank()) {
            return "Jméno i příjmení jsou povinné.";
        }

        var aktivniRocnikOpt = rocnikRepository.findByAktivniTrue();
        if (aktivniRocnikOpt.isEmpty()) {
            return "Nejprve nastavte aktivní ročník.";
        }

        var tymOpt = tymRepository.findById(tymId);
        if (tymOpt.isEmpty()) {
            return "Vybraný tým nebyl nalezen.";
        }

        LocalDate parsedDatumNarozeni = null;
        if (datumNarozeni != null && !datumNarozeni.isBlank()) {
            try {
                parsedDatumNarozeni = LocalDate.parse(datumNarozeni);
            } catch (DateTimeParseException e) {
                return "Neplatný formát data narození.";
            }
        }

        hracService.createHracSRegistraci(
                jmenoTrim,
                prijmeniTrim,
                parsedDatumNarozeni,
                tymOpt.get(),
                aktivniRocnikOpt.get()
        );
        return null;
    }

    @Transactional
    public void delete(Long id) {
        hracService.smazatHraceVcetneHistorie(id);
    }
}
