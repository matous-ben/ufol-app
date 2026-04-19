package cz.ufol.app.standings;

import cz.ufol.app.match.Zapas;
import cz.ufol.app.match.ZapasRepository;
import cz.ufol.app.season.Rocnik;
import cz.ufol.app.season.RocnikRepository;
import cz.ufol.app.team.Tym;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StandingsService {

    private final ZapasRepository zapasRepository;
    private final RocnikRepository rocnikRepository;

    // Vypocita celkovy aktualni stav tabulky pro aktivni sezonu
    // Vrati prazdny list pokud neni aktivni sezona nebo nebyl odehran zadny zapas
    // Algoritmus:
    // 1. Najde aktivni sezonu
    // 2. Nacte vsechny odehrane zapasy sezony
    // 3. Pro kazdy zapas aktualizuje statistiky tymu
    // 4. Prevede statistiky do DTOs a seradi je

    public List<StandingsRowDTO> calculateStandings() {
        Optional<Rocnik> aktivniRocnik = rocnikRepository.findByAktivniTrue();
        if (aktivniRocnik.isEmpty()) {
            return Collections.emptyList();
        }

        List<Zapas> odehraneZapasy = zapasRepository
                .findByRocnikAndOdehranTrue(aktivniRocnik.get());

        Map<Tym, int[]> stats = new LinkedHashMap<>();

        for (Zapas zapas : odehraneZapasy) {
            Tym domaci = zapas.getDomaciTym();
            Tym hoste = zapas.getHosteTym();

            // Zajistime ze oba tymy maji zapis v mape statistik
            // int[] layout: [odehrane, vyhry, remizy, prohry, vstreleneGoly, obdrzeneGoly]
            stats.putIfAbsent(domaci, new int[6]);
            stats.putIfAbsent(hoste, new int[6]);

            int[] domaciStats = stats.get(domaci);
            int[] hosteStats = stats.get(hoste);

            int domaciSkore = zapas.getDomaciSkore() != null ? zapas.getDomaciSkore() : 0;
            int hosteSkore = zapas.getHosteSkore()  != null ? zapas.getHosteSkore() : 0;

            // Zvysime pocet odehranych zapasu
            domaciStats[0]++;
            hosteStats[0]++;

            // Na zaklade vysledku zapasu aktualizujeme V/R/P statistiky obou tymu
            if (domaciSkore > hosteSkore) {
                domaciStats[1]++;
                hosteStats[3]++;
            } else if (domaciSkore < hosteSkore) {
                hosteStats[1]++;
                domaciStats[3]++;
            } else {
                domaciStats[2]++;
                hosteStats[2]++;
            }

            // Aktualizujeme vstrelene a obdrzene goly
            domaciStats[4] += domaciSkore;
            domaciStats[5] += hosteSkore;
            hosteStats[4] += hosteSkore;
            hosteStats[5] += domaciSkore;
        }

        // Prevedeme mapu do DTOs a vypocitame body
        List<StandingsRowDTO> standings = new ArrayList<>();
        for (Map.Entry<Tym, int[]> entry : stats.entrySet()) {
            Tym tym = entry.getKey();
            int[] s = entry.getValue();
            int body = (s[1] * 3) + (s[2]); // Vyhra = 3, Remiza = 1

            standings.add(new StandingsRowDTO(
                    tym.getNazev(),
                    tym.getId(),
                    s[0], // odehrane
                    s[1], // vyhry
                    s[2], // remizy
                    s[3], // prohry
                    s[4], // vstrelene goly
                    s[5], // obdrzene goly
                    body,
                    tym.getUniverzita().getLogoFile()
            ));
        }

        // Seradime sestupne podle bodu, pri shode bodu pak podle rozdilu ve skore sestupne
        standings.sort(Comparator
                .comparingInt(StandingsRowDTO::getBody).reversed()
                .thenComparingInt(StandingsRowDTO::getGoloveSkore).reversed()
                .thenComparingInt(StandingsRowDTO::getObdrzeneGoly).reversed()
        );

        return standings;
    }
}
