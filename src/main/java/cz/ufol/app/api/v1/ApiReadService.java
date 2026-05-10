package cz.ufol.app.api.v1;

import cz.ufol.app.api.v1.dto.ApiMatchDto;
import cz.ufol.app.api.v1.dto.ApiStandingsRowDto;
import cz.ufol.app.api.v1.dto.ApiTeamDto;
import cz.ufol.app.api.v1.dto.ApiUniversityDto;
import cz.ufol.app.match.Zapas;
import cz.ufol.app.match.ZapasService;
import cz.ufol.app.standings.StandingsService;
import cz.ufol.app.team.Tym;
import cz.ufol.app.team.TymService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ApiReadService {

    private final TymService tymService;
    private final ZapasService zapasService;
    private final StandingsService standingsService;

    @Transactional(readOnly = true)
    public List<ApiTeamDto> getActiveTeams() {
        return tymService.findAllAktivni().stream()
                .map(this::mapTeam)
                .toList();
    }

    @Transactional(readOnly = true)
    public ApiTeamDto getTeam(Long id) {
        return mapTeam(tymService.findByIdOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<ApiMatchDto> getUpcomingMatches() {
        return zapasService.findNaplanovane().stream()
                .map(this::mapMatch)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ApiMatchDto> getPlayedMatches() {
        return zapasService.findOdehrane().stream()
                .map(this::mapMatch)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ApiStandingsRowDto> getStandings() {
        return standingsService.calculateStandings().stream()
                .map(row -> new ApiStandingsRowDto(
                        row.getTymId(),
                        row.getNazevTymu(),
                        row.getOdehrane(),
                        row.getVyhry(),
                        row.getRemizy(),
                        row.getProhry(),
                        row.getVstreleneGoly(),
                        row.getObdrzeneGoly(),
                        row.getBody(),
                        row.getLogoFile()
                ))
                .toList();
    }

    private ApiMatchDto mapMatch(Zapas zapas) {
        return new ApiMatchDto(
                zapas.getId(),
                mapTeam(zapas.getDomaciTym()),
                mapTeam(zapas.getHosteTym()),
                zapas.getDatumCas(),
                zapas.isOdehran(),
                zapas.getDomaciSkore(),
                zapas.getHosteSkore(),
                zapas.getMistoKonani() != null ? zapas.getMistoKonani().getNazev() : null
        );
    }

    private ApiTeamDto mapTeam(Tym tym) {
        if (tym == null) {
            throw new ResponseStatusException(NOT_FOUND, "Team not found");
        }
        return new ApiTeamDto(
                tym.getId(),
                tym.getNazev(),
                tym.isAktivni(),
                new ApiUniversityDto(
                        tym.getUniverzita().getId(),
                        tym.getUniverzita().getNazev(),
                        tym.getUniverzita().getZkratka(),
                        tym.getUniverzita().getLogoFile()
                )
        );
    }
}
