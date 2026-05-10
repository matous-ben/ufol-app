package cz.ufol.app.api.v1;

import cz.ufol.app.api.v1.dto.ApiMatchDto;
import cz.ufol.app.api.v1.dto.ApiPlayerStatsDto;
import cz.ufol.app.api.v1.dto.ApiStandingsRowDto;
import cz.ufol.app.api.v1.dto.ApiTeamDto;
import cz.ufol.app.match.Zapas;
import cz.ufol.app.match.ZapasService;
import cz.ufol.app.player.HracService;
import cz.ufol.app.season.Rocnik;
import cz.ufol.app.season.RocnikService;
import cz.ufol.app.standings.StandingsService;
import cz.ufol.app.team.Tym;
import cz.ufol.app.team.TymService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiQueryService {

    private final TymService tymService;
    private final ZapasService zapasService;
    private final StandingsService standingsService;
    private final RocnikService rocnikService;
    private final HracService hracService;

    @Transactional(readOnly = true)
    public List<ApiTeamDto> getTeams() {
        return tymService.findAllAktivni().stream().map(this::mapTeam).toList();
    }

    @Transactional(readOnly = true)
    public List<ApiMatchDto> getUpcomingMatches() {
        return zapasService.findNaplanovane().stream().map(this::mapMatch).toList();
    }

    @Transactional(readOnly = true)
    public List<ApiMatchDto> getPlayedMatches() {
        return zapasService.findOdehrane().stream().map(this::mapMatch).toList();
    }

    @Transactional(readOnly = true)
    public List<ApiStandingsRowDto> getStandings() {
        var rows = standingsService.calculateStandings();
        final int[] poradi = {1};
        return rows.stream().map(row -> new ApiStandingsRowDto(
                poradi[0]++,
                row.getTymId(),
                row.getNazevTymu(),
                row.getOdehrane(),
                row.getVyhry(),
                row.getRemizy(),
                row.getProhry(),
                row.getVstreleneGoly(),
                row.getObdrzeneGoly(),
                row.getGoloveSkore(),
                row.getBody(),
                row.getLogoFile()
        )).toList();
    }

    @Transactional(readOnly = true)
    public List<ApiPlayerStatsDto> getTeamPlayersStats(Long tymId) {
        Tym tym = tymService.findById(tymId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tým nebyl nalezen."));
        Rocnik rocnik = rocnikService.findByAktivniTrue()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aktivní ročník nebyl nalezen."));

        return hracService.najdiStatistikyTymuProRocnik(tym, rocnik).stream()
                .map(stat -> new ApiPlayerStatsDto(
                        stat.registrace().getHrac().getId(),
                        stat.registrace().getHrac().getJmeno(),
                        stat.registrace().getHrac().getPrijmeni(),
                        stat.odehraneZapasy(),
                        stat.goly()
                ))
                .toList();
    }

    private ApiTeamDto mapTeam(Tym tym) {
        return new ApiTeamDto(
                tym.getId(),
                tym.getNazev(),
                tym.isAktivni(),
                tym.getUniverzita().getNazev(),
                tym.getUniverzita().getZkratka(),
                tym.getUniverzita().getLogoFile()
        );
    }

    private ApiMatchDto mapMatch(Zapas zapas) {
        return new ApiMatchDto(
                zapas.getId(),
                zapas.getDatumCas(),
                zapas.isOdehran(),
                zapas.getDomaciSkore(),
                zapas.getHosteSkore(),
                zapas.getDomaciTym().getId(),
                zapas.getDomaciTym().getNazev(),
                zapas.getHosteTym().getId(),
                zapas.getHosteTym().getNazev(),
                zapas.getMistoKonani() != null ? zapas.getMistoKonani().getNazev() : null,
                zapas.getRocnik().getId(),
                zapas.getRocnik().getNazev()
        );
    }
}
