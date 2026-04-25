package cz.ufol.app.standings;

import cz.ufol.app.match.Zapas;
import cz.ufol.app.match.ZapasRepository;
import cz.ufol.app.season.Rocnik;
import cz.ufol.app.season.RocnikRepository;
import cz.ufol.app.team.Tym;
import cz.ufol.app.team.TymRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StandingsService {

    private final ZapasRepository zapasRepository;
    private final RocnikRepository rocnikRepository;
    private final TymRepository tymRepository;

    private static class TeamStats {
        private int odehrane = 0;
        private int vyhry = 0;
        private int remizy = 0;
        private int prohry = 0;
        private int vstreleneGoly = 0;
        private int obdrzeneGoly = 0;

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

        // 1. Map by ID to guarantee uniqueness, regardless of Java memory addresses
        Map<Long, TeamStats> statsMap = new LinkedHashMap<>();
        Map<Long, Tym> teamsDetailsMap = new HashMap<>();

        // 2. Defensive Initialization
        List<Tym> vsechnyAktivniTymy = tymRepository.findByAktivniTrue();
        for (Tym tym : vsechnyAktivniTymy) {
            statsMap.put(tym.getId(), new TeamStats());
            teamsDetailsMap.put(tym.getId(), tym);
        }

        // 3. Process matches ONLY for active teams
        List<Zapas> odehraneZapasy = zapasRepository
                .findByRocnikAndOdehranTrueWithDetails(aktivniRocnik.get());

        for (Zapas zapas : odehraneZapasy) {
            Tym domaci = zapas.getDomaciTym();
            Tym hoste = zapas.getHosteTym();

            int domaciSkore = zapas.getDomaciSkore() != null ? zapas.getDomaciSkore() : 0;
            int hosteSkore = zapas.getHosteSkore()  != null ? zapas.getHosteSkore() : 0;

            // Only add stats if the team is in our active map
            if (statsMap.containsKey(domaci.getId())) {
                statsMap.get(domaci.getId()).pridejVysledekZapasu(domaciSkore, hosteSkore);
            }

            if (statsMap.containsKey(hoste.getId())) {
                statsMap.get(hoste.getId()).pridejVysledekZapasu(hosteSkore, domaciSkore);
            }
        }

        // 4. Map to DTOs
        List<StandingsRowDTO> standings = new ArrayList<>();
        for (Map.Entry<Long, TeamStats> entry : statsMap.entrySet()) {
            Long tymId = entry.getKey();
            TeamStats s = entry.getValue();
            Tym tym = teamsDetailsMap.get(tymId);

            standings.add(new StandingsRowDTO(
                    tym.getNazev(),
                    tym.getId(),
                    s.odehrane,
                    s.vyhry,
                    s.remizy,
                    s.prohry,
                    s.vstreleneGoly,
                    s.obdrzeneGoly,
                    s.getBody(),
                    tym.getUniverzita().getLogoFile()
            ));
        }

        // 5// 5. Explicit FIFA-Standard sorting
        standings.sort((teamA, teamB) -> {
            // Rule 1: Points (Descending)
            int bodyCompare = Integer.compare(teamB.getBody(), teamA.getBody());
            if (bodyCompare != 0) return bodyCompare;

            // Rule 2: Goal Difference (Descending)
            int diffCompare = Integer.compare(teamB.getGoloveSkore(), teamA.getGoloveSkore());
            if (diffCompare != 0) return diffCompare;

            // Rule 3: Goals Scored (Descending)
            int scoredCompare = Integer.compare(teamB.getVstreleneGoly(), teamA.getVstreleneGoly());
            if (scoredCompare != 0) return scoredCompare;

            // Rule 4: Alphabetical order (Ascending)
            return teamA.getNazevTymu().compareToIgnoreCase(teamB.getNazevTymu());
        });

        return standings;
    }
}
