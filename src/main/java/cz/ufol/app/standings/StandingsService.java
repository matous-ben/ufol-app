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

    private static class TeamStats {
        private int odehrane = 0;
        private int vyhry = 0;
        private int remizy = 0;
        private int prohry = 0;
        private int vstreleneGoly = 0;
        private int obdrzeneGoly = 0;

        // Metoda, která zapouzdřuje logiku (behavior) přidání výsledku
        public void pridejVysledekZapasu(int vstrelene, int obdrzene) {
            this.odehrane++;
            this.vstreleneGoly += vstrelene;
            this.obdrzeneGoly += obdrzene;

            if (vstrelene > obdrzene) {
                this.vyhry++;
            } else if (vstrelene < obdrzene) {
                this.prohry++;
            } else {
                this.remizy++;
            }
        }

        public int getBody() {
            return (this.vyhry * 3) + this.remizy;
        }
    }

    public List<StandingsRowDTO> calculateStandings() {
        Optional<Rocnik> aktivniRocnik = rocnikRepository.findByAktivniTrue();
        if (aktivniRocnik.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Tym, TeamStats> stats = new LinkedHashMap<>();

        List<Zapas> odehraneZapasy = zapasRepository
                .findByRocnikAndOdehranTrueWithDetails(aktivniRocnik.get());

        for (Zapas zapas : odehraneZapasy) {
            Tym domaci = zapas.getDomaciTym();
            Tym hoste = zapas.getHosteTym();

            // Zajistime ze oba tymy maji zapis v mape statistik
            // int[] layout: [odehrane, vyhry, remizy, prohry, vstreleneGoly, obdrzeneGoly]
            stats.putIfAbsent(domaci, new TeamStats());
            stats.putIfAbsent(hoste, new TeamStats());

            int domaciSkore = zapas.getDomaciSkore() != null ? zapas.getDomaciSkore() : 0;
            int hosteSkore = zapas.getHosteSkore()  != null ? zapas.getHosteSkore() : 0;

            stats.get(domaci).pridejVysledekZapasu(domaciSkore, hosteSkore);
            stats.get(hoste).pridejVysledekZapasu(hosteSkore, domaciSkore);
        }

        // Prevedeme mapu do DTOs a vypocitame body
        List<StandingsRowDTO> standings = new ArrayList<>();
        for (Map.Entry<Tym, TeamStats> entry : stats.entrySet()) {
            Tym tym = entry.getKey();
            TeamStats s = entry.getValue();

            standings.add(new StandingsRowDTO(
                    tym.getNazev(),
                    tym.getId(),
                    s.odehrane,
                    s.vyhry,
                    s.remizy,
                    s.prohry,
                    s.vstreleneGoly,
                    s.obdrzeneGoly,
                    s.getBody(), // logika vypoctu uvnitr objektu
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
