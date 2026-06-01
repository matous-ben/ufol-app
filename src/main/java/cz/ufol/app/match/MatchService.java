package cz.ufol.app.match;

import cz.ufol.app.player.Registrace;
import cz.ufol.app.player.RegistraceRepository;
import cz.ufol.app.season.Rocnik;
import cz.ufol.app.season.RocnikRepository;
import cz.ufol.app.team.Tym;
import cz.ufol.app.team.TymRepository;
import cz.ufol.app.venue.MistoKonani;
import cz.ufol.app.venue.MistoKonaniRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ZapasService {

    private final ZapasRepository zapasRepository;
    private final TymRepository tymRepository;
    private final RocnikRepository rocnikRepository;
    private final MistoKonaniRepository mistoKonaniRepository;
    private final RegistraceRepository registraceRepository;
    private final UcastVZapaseRepository ucastVZapaseRepository;

    public record ServiceResult(String redirectPath, String flashType, String flashMessage) {}
    public record AdminZapasyListData(Rocnik aktivniRocnik, List<Zapas> naplanovane, List<Zapas> odehrane) {}
    public record AdminZapasFormData(List<Tym> tymy, List<Rocnik> rocniky, List<MistoKonani> mistaKonani) {}
    public record AdminVysledekFormData(
            boolean found,
            String errorMessage,
            Zapas zapas,
            List<Registrace> domaciRegistrace,
            List<Registrace> hosteRegistrace,
            Set<Long> selectedRegistraceIds,
            Map<Long, Integer> golyMap
    ) {}

    @Transactional(readOnly = true)
    public List<Zapas> findNaplanovane() {
        Optional<Rocnik> aktivniRocnik = rocnikRepository.findByAktivniTrue();
        if (aktivniRocnik.isEmpty()) return Collections.emptyList();
        return zapasRepository
                .findByRocnikAndOdehranFalseOrderByDatumCasAsc(aktivniRocnik.get());
    }

    @Transactional(readOnly = true)
    public List<Zapas> findOdehrane() {
        Optional<Rocnik> aktivniRocnik = rocnikRepository.findByAktivniTrue();
        if (aktivniRocnik.isEmpty()) return Collections.emptyList();
        return zapasRepository
                .findByRocnikAndOdehranTrueOrderByDatumCasDesc(aktivniRocnik.get());
    }

    @Transactional(readOnly = true)
    public List<Zapas> findTop3Naplanovane() {
        Optional<Rocnik> aktivniRocnik = rocnikRepository.findByAktivniTrue();
        if (aktivniRocnik.isEmpty()) return Collections.emptyList();
        return zapasRepository
                .findByRocnikAndOdehranFalseOrderByDatumCasAsc(aktivniRocnik.get())
                .stream()
                .limit(3)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Zapas> findTop3UpcomingForHome() {
        return zapasRepository.findTop3ByOdehranFalseOrderByDatumCasAsc();
    }

    @Transactional(readOnly = true)
    public AdminZapasyListData getAdminZapasyListData() {
        var aktivniRocnik = rocnikRepository.findByAktivniTrue().orElse(null);
        if (aktivniRocnik == null) {
            return new AdminZapasyListData(null, List.of(), List.of());
        }

        return new AdminZapasyListData(
                aktivniRocnik,
                zapasRepository.findByRocnikAndOdehranFalseOrderByDatumCasAsc(aktivniRocnik),
                zapasRepository.findByRocnikAndOdehranTrueOrderByDatumCasDesc(aktivniRocnik)
        );
    }

    @Transactional(readOnly = true)
    public AdminZapasFormData getAdminZapasFormData() {
        return new AdminZapasFormData(
                tymRepository.findByAktivniTrue(),
                rocnikRepository.findAllByOrderByRokOdDesc(),
                mistoKonaniRepository.findAllByOrderByNazevAsc()
        );
    }

    @Transactional
    public ServiceResult createAdminZapas(Long domaciTymId, Long hosteTymId, Long rocnikId, Long mistoKonaniId, String datumCas) {
        if (domaciTymId.equals(hosteTymId)) {
            return new ServiceResult("/admin/zapasy/novy", "error", "Tým nemůže hrát sám proti sobě.");
        }

        var domaciOpt = tymRepository.findById(domaciTymId);
        if (domaciOpt.isEmpty()) {
            return new ServiceResult("/admin/zapasy/novy", "error", "Domácí tým nebyl nalezen.");
        }

        var hosteOpt = tymRepository.findById(hosteTymId);
        if (hosteOpt.isEmpty()) {
            return new ServiceResult("/admin/zapasy/novy", "error", "Hostující tým nebyl nalezen.");
        }

        var rocnikOpt = rocnikRepository.findById(rocnikId);
        if (rocnikOpt.isEmpty()) {
            return new ServiceResult("/admin/zapasy/novy", "error", "Vybraný ročník nebyl nalezen.");
        }

        var misto = mistoKonaniId != null ? mistoKonaniRepository.findById(mistoKonaniId).orElse(null) : null;
        if (mistoKonaniId != null && misto == null) {
            return new ServiceResult("/admin/zapasy/novy", "error", "Vybrané místo konání nebylo nalezeno.");
        }

        LocalDateTime parsedDatumCas = null;
        if (datumCas != null && !datumCas.isBlank()) {
            try {
                parsedDatumCas = LocalDateTime.parse(datumCas);
            } catch (DateTimeParseException e) {
                return new ServiceResult("/admin/zapasy/novy", "error", "Neplatný formát data a času. Použijte prosím validní datum.");
            }
        }

        zapasRepository.save(Zapas.builder()
                .domaciTym(domaciOpt.get())
                .hosteTym(hosteOpt.get())
                .rocnik(rocnikOpt.get())
                .mistoKonani(misto)
                .datumCas(parsedDatumCas)
                .odehran(false)
                .domaciSkore(0)
                .hosteSkore(0)
                .build());

        return new ServiceResult("/admin/zapasy", "success", "Zápas byl přidán.");
    }

    @Transactional(readOnly = true)
    public AdminVysledekFormData getAdminVysledekFormData(Long zapasId) {
        var zapasOpt = zapasRepository.findById(zapasId);
        if (zapasOpt.isEmpty()) {
            return new AdminVysledekFormData(false, "Zápas nebyl nalezen.", null, List.of(), List.of(), Set.of(), Map.of());
        }

        var zapas = zapasOpt.get();
        var domaciRegistrace = registraceRepository.findByRocnikAndTymOrderByHracPrijmeniAscHracJmenoAsc(zapas.getRocnik(), zapas.getDomaciTym());
        var hosteRegistrace = registraceRepository.findByRocnikAndTymOrderByHracPrijmeniAscHracJmenoAsc(zapas.getRocnik(), zapas.getHosteTym());
        var ucasti = ucastVZapaseRepository.findByZapas(zapas);

        Set<Long> selectedRegistraceIds = ucasti.stream()
                .map(u -> u.getRegistrace().getId())
                .collect(Collectors.toSet());
        Map<Long, Integer> golyMap = ucasti.stream()
                .collect(Collectors.toMap(u -> u.getRegistrace().getId(), UcastVZapase::getGoly));

        return new AdminVysledekFormData(true, null, zapas, domaciRegistrace, hosteRegistrace, selectedRegistraceIds, golyMap);
    }

    @Transactional
    public ServiceResult ulozAdminVysledek(Long id, Integer domaciSkore, Integer hosteSkore, List<Long> registraceIds, Map<String, String[]> parameters) {
        if (domaciSkore == null || hosteSkore == null) {
            return new ServiceResult("/admin/zapasy/" + id + "/vysledek", "error", "Skóre musí být vyplněno.");
        }
        if (domaciSkore < 0 || hosteSkore < 0) {
            return new ServiceResult("/admin/zapasy/" + id + "/vysledek", "error", "Skóre nemůže být záporné.");
        }

        var zapasOpt = zapasRepository.findById(id);
        if (zapasOpt.isEmpty()) {
            return new ServiceResult("/admin/zapasy", "error", "Zápas nebyl nalezen.");
        }

        var zapas = zapasOpt.get();
        zapas.setDomaciSkore(domaciSkore);
        zapas.setHosteSkore(hosteSkore);
        zapas.setOdehran(true);
        zapasRepository.save(zapas);

        var domaciRegistrace = registraceRepository.findByRocnikAndTymOrderByHracPrijmeniAscHracJmenoAsc(zapas.getRocnik(), zapas.getDomaciTym());
        var hosteRegistrace = registraceRepository.findByRocnikAndTymOrderByHracPrijmeniAscHracJmenoAsc(zapas.getRocnik(), zapas.getHosteTym());

        Set<Long> povoleneRegistraceIds = Stream.concat(domaciRegistrace.stream().map(Registrace::getId), hosteRegistrace.stream().map(Registrace::getId))
                .collect(Collectors.toSet());

        ucastVZapaseRepository.deleteByZapas(zapas);

        if (registraceIds != null && !registraceIds.isEmpty()) {
            var validniRegistrace = registraceRepository.findAllById(registraceIds).stream()
                    .filter(r -> povoleneRegistraceIds.contains(r.getId()))
                    .toList();

            for (var registrace : validniRegistrace) {
                String golyRaw = firstValue(parameters.get("goly_" + registrace.getId()));
                int goly = parseGoly(golyRaw);
                ucastVZapaseRepository.save(UcastVZapase.builder()
                        .zapas(zapas)
                        .registrace(registrace)
                        .goly(goly)
                        .build());
            }
        }

        return new ServiceResult("/admin/zapasy", "success", "Výsledek zápasu byl uložen. Tabulka se automaticky aktualizovala.");
    }

    @Transactional
    public ServiceResult smazAdminZapas(Long id) {
        if (!zapasRepository.existsById(id)) {
            return new ServiceResult("/admin/zapasy", "error", "Zápas nebyl nalezen.");
        }
        zapasRepository.deleteById(id);
        return new ServiceResult("/admin/zapasy", "success", "Zápas byl smazán.");
    }

    private String firstValue(String[] values) {
        return values == null || values.length == 0 ? null : values[0];
    }

    private int parseGoly(String golyRaw) {
        if (golyRaw == null || golyRaw.isBlank()) {
            return 0;
        }
        try {
            int parsed = Integer.parseInt(golyRaw);
            return Math.max(parsed, 0);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }
}
