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
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
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
    public ListData getListData() {
        var aktivniRocnik = rocnikRepository.findByAktivniTrue().orElse(null);
        List<Zapas> naplanovane = List.of();
        List<Zapas> odehrane = List.of();
        if (aktivniRocnik != null) {
            naplanovane = zapasRepository.findByRocnikAndOdehranFalseOrderByDatumCasAsc(aktivniRocnik);
            odehrane = zapasRepository.findByRocnikAndOdehranTrueOrderByDatumCasDesc(aktivniRocnik);
        }
        return new ListData(aktivniRocnik, naplanovane, odehrane);
    }

    @Transactional(readOnly = true)
    public CreateFormData getCreateFormData() {
        return new CreateFormData(
                tymRepository.findByAktivniTrue(),
                rocnikRepository.findAllByOrderByRokOdDesc(),
                mistoKonaniRepository.findAllByOrderByNazevAsc()
        );
    }

    @Transactional
    public String create(Long domaciTymId, Long hosteTymId, Long rocnikId, Long mistoKonaniId, String datumCas) {
        if (domaciTymId.equals(hosteTymId)) {
            return "Tým nemůže hrát sám proti sobě.";
        }

        var domaciOpt = tymRepository.findById(domaciTymId);
        if (domaciOpt.isEmpty()) {
            return "Domácí tým nebyl nalezen.";
        }

        var hosteOpt = tymRepository.findById(hosteTymId);
        if (hosteOpt.isEmpty()) {
            return "Hostující tým nebyl nalezen.";
        }

        var rocnikOpt = rocnikRepository.findById(rocnikId);
        if (rocnikOpt.isEmpty()) {
            return "Vybraný ročník nebyl nalezen.";
        }

        var misto = mistoKonaniId != null ? mistoKonaniRepository.findById(mistoKonaniId).orElse(null) : null;
        if (mistoKonaniId != null && misto == null) {
            return "Vybrané místo konání nebylo nalezeno.";
        }

        LocalDateTime parsedDatumCas = null;
        if (datumCas != null && !datumCas.isBlank()) {
            try {
                parsedDatumCas = LocalDateTime.parse(datumCas);
            } catch (DateTimeParseException e) {
                return "Neplatný formát data a času. Použijte prosím validní datum.";
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
        return null;
    }

    @Transactional(readOnly = true)
    public ResultFormData getResultFormData(Long id) {
        var zapas = zapasRepository.findById(id).orElse(null);
        if (zapas == null) {
            return null;
        }

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

        return new ResultFormData(zapas, domaciRegistrace, hosteRegistrace, selectedRegistraceIds, golyMap);
    }

    @Transactional
    public String saveResult(Long id, Integer domaciSkore, Integer hosteSkore, List<Long> registraceIds, HttpServletRequest request) {
        if (domaciSkore == null || hosteSkore == null) {
            return "Skóre musí být vyplněno.";
        }
        if (domaciSkore < 0 || hosteSkore < 0) {
            return "Skóre nemůže být záporné.";
        }

        var zapas = zapasRepository.findById(id).orElse(null);
        if (zapas == null) {
            return "Zápas nebyl nalezen.";
        }

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
                String golyRaw = request.getParameter("goly_" + registrace.getId());
                int goly = 0;
                if (golyRaw != null && !golyRaw.isBlank()) {
                    try {
                        goly = Integer.parseInt(golyRaw);
                    } catch (NumberFormatException ignored) {
                        goly = 0;
                    }
                }
                if (goly < 0) {
                    goly = 0;
                }

                ucastVZapaseRepository.save(UcastVZapase.builder()
                        .zapas(zapas)
                        .registrace(registrace)
                        .goly(goly)
                        .build());
            }
        }

        return null;
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return zapasRepository.existsById(id);
    }

    @Transactional
    public void delete(Long id) {
        zapasRepository.deleteById(id);
    }

    public record ListData(Rocnik aktivniRocnik, List<Zapas> naplanovane, List<Zapas> odehrane) {}

    public record CreateFormData(List<Tym> tymy, List<Rocnik> rocniky, List<MistoKonani> mistaKonani) {}

    public record ResultFormData(
            Zapas zapas,
            List<Registrace> domaciRegistrace,
            List<Registrace> hosteRegistrace,
            Set<Long> selectedRegistraceIds,
            Map<Long, Integer> golyMap
    ) {}
}
