package cz.ufol.app.admin;

import cz.ufol.app.match.UcastVZapase;
import cz.ufol.app.match.UcastVZapaseRepository;
import cz.ufol.app.match.Zapas;
import cz.ufol.app.match.ZapasRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminZapasService {

    private final ZapasRepository zapasRepository;
    private final TymRepository tymRepository;
    private final RocnikRepository rocnikRepository;
    private final MistoKonaniRepository mistoKonaniRepository;
    private final RegistraceRepository registraceRepository;
    private final UcastVZapaseRepository ucastVZapaseRepository;

    @Transactional(readOnly = true)
    public AdminZapasListData getListData() {
        Rocnik aktivniRocnik = rocnikRepository.findByAktivniTrue().orElse(null);
        List<Zapas> naplanovane = Collections.emptyList();
        List<Zapas> odehrane = Collections.emptyList();
        if (aktivniRocnik != null) {
            naplanovane = zapasRepository.findByRocnikAndOdehranFalseOrderByDatumCasAsc(aktivniRocnik);
            odehrane = zapasRepository.findByRocnikAndOdehranTrueOrderByDatumCasDesc(aktivniRocnik);
        }
        return new AdminZapasListData(aktivniRocnik, naplanovane, odehrane);
    }

    @Transactional(readOnly = true)
    public AdminZapasFormData getCreateFormData() {
        return new AdminZapasFormData(
                tymRepository.findByAktivniTrue(),
                rocnikRepository.findAllByOrderByRokOdDesc(),
                mistoKonaniRepository.findAllByOrderByNazevAsc()
        );
    }

    @Transactional
    public void create(Long domaciTymId,
                       Long hosteTymId,
                       Long rocnikId,
                       Long mistoKonaniId,
                       LocalDateTime parsedDatumCas) {
        var domaci = tymRepository.findById(domaciTymId).orElseThrow();
        var hoste = tymRepository.findById(hosteTymId).orElseThrow();
        var rocnik = rocnikRepository.findById(rocnikId).orElseThrow();
        var misto = mistoKonaniId != null ? mistoKonaniRepository.findById(mistoKonaniId).orElse(null) : null;

        var zapas = Zapas.builder()
                .domaciTym(domaci)
                .hosteTym(hoste)
                .rocnik(rocnik)
                .mistoKonani(misto)
                .datumCas(parsedDatumCas)
                .odehran(false)
                .domaciSkore(0)
                .hosteSkore(0)
                .build();
        zapasRepository.save(zapas);
    }

    @Transactional(readOnly = true)
    public boolean existsTym(Long tymId) {
        return tymRepository.existsById(tymId);
    }

    @Transactional(readOnly = true)
    public boolean existsRocnik(Long rocnikId) {
        return rocnikRepository.existsById(rocnikId);
    }

    @Transactional(readOnly = true)
    public boolean existsMistoKonani(Long mistoKonaniId) {
        return mistoKonaniRepository.existsById(mistoKonaniId);
    }

    @Transactional(readOnly = true)
    public Optional<AdminZapasVysledekFormData> getVysledekFormData(Long zapasId) {
        var zapasOpt = zapasRepository.findById(zapasId);
        if (zapasOpt.isEmpty()) {
            return Optional.empty();
        }
        var zapas = zapasOpt.get();

        var domaciRegistrace = registraceRepository
                .findByRocnikAndTymOrderByHracPrijmeniAscHracJmenoAsc(zapas.getRocnik(), zapas.getDomaciTym());
        var hosteRegistrace = registraceRepository
                .findByRocnikAndTymOrderByHracPrijmeniAscHracJmenoAsc(zapas.getRocnik(), zapas.getHosteTym());

        var ucasti = ucastVZapaseRepository.findByZapas(zapas);
        Set<Long> selectedRegistraceIds = ucasti.stream()
                .map(u -> u.getRegistrace().getId())
                .collect(Collectors.toSet());
        Map<Long, Integer> golyMap = ucasti.stream()
                .collect(Collectors.toMap(u -> u.getRegistrace().getId(), UcastVZapase::getGoly));

        return Optional.of(new AdminZapasVysledekFormData(
                zapas, domaciRegistrace, hosteRegistrace, selectedRegistraceIds, golyMap
        ));
    }

    @Transactional
    public boolean ulozVysledek(Long zapasId, Integer domaciSkore, Integer hosteSkore, List<Long> registraceIds, Map<String, String[]> requestParameters) {
        var zapasOpt = zapasRepository.findById(zapasId);
        if (zapasOpt.isEmpty()) {
            return false;
        }
        var zapas = zapasOpt.get();
        zapas.setDomaciSkore(domaciSkore);
        zapas.setHosteSkore(hosteSkore);
        zapas.setOdehran(true);
        zapasRepository.save(zapas);

        var domaciRegistrace = registraceRepository
                .findByRocnikAndTymOrderByHracPrijmeniAscHracJmenoAsc(zapas.getRocnik(), zapas.getDomaciTym());
        var hosteRegistrace = registraceRepository
                .findByRocnikAndTymOrderByHracPrijmeniAscHracJmenoAsc(zapas.getRocnik(), zapas.getHosteTym());

        Set<Long> povoleneRegistraceIds = java.util.stream.Stream.concat(
                        domaciRegistrace.stream().map(Registrace::getId),
                        hosteRegistrace.stream().map(Registrace::getId)
                )
                .collect(Collectors.toSet());

        ucastVZapaseRepository.deleteByZapas(zapas);

        if (registraceIds != null && !registraceIds.isEmpty()) {
            var validniRegistrace = registraceRepository.findAllById(registraceIds).stream()
                    .filter(r -> povoleneRegistraceIds.contains(r.getId()))
                    .toList();

            for (var registrace : validniRegistrace) {
                String paramName = "goly_" + registrace.getId();
                String[] values = requestParameters.get(paramName);
                String golyRaw = (values != null && values.length > 0) ? values[0] : null;
                int goly = parseGoly(golyRaw);

                ucastVZapaseRepository.save(UcastVZapase.builder()
                        .zapas(zapas)
                        .registrace(registrace)
                        .goly(goly)
                        .build());
            }
        }
        return true;
    }

    @Transactional
    public boolean delete(Long id) {
        if (!zapasRepository.existsById(id)) {
            return false;
        }
        zapasRepository.deleteById(id);
        return true;
    }

    private int parseGoly(String golyRaw) {
        if (golyRaw == null || golyRaw.isBlank()) {
            return 0;
        }
        try {
            return Math.max(Integer.parseInt(golyRaw), 0);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    public record AdminZapasListData(Rocnik aktivniRocnik, List<Zapas> naplanovane, List<Zapas> odehrane) {
    }

    public record AdminZapasFormData(List<Tym> tymy, List<Rocnik> rocniky, List<MistoKonani> mistaKonani) {
    }

    public record AdminZapasVysledekFormData(
            Zapas zapas,
            List<Registrace> domaciRegistrace,
            List<Registrace> hosteRegistrace,
            Set<Long> selectedRegistraceIds,
            Map<Long, Integer> golyMap
    ) {
    }
}
