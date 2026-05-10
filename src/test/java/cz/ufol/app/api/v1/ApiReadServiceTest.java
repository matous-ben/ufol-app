package cz.ufol.app.api.v1;

import cz.ufol.app.match.ZapasService;
import cz.ufol.app.standings.StandingsRowDTO;
import cz.ufol.app.standings.StandingsService;
import cz.ufol.app.team.Tym;
import cz.ufol.app.team.TymService;
import cz.ufol.app.university.Univerzita;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiReadServiceTest {

    @Mock
    private TymService tymService;
    @Mock
    private ZapasService zapasService;
    @Mock
    private StandingsService standingsService;

    @InjectMocks
    private ApiReadService apiReadService;

    @Test
    void getActiveTeams_mapsEntityToDto() {
        var university = Univerzita.builder().id(10L).nazev("CVUT").zkratka("CVUT").logoFile("logo.svg").build();
        var team = Tym.builder().id(1L).nazev("Wolves").aktivni(true).univerzita(university).build();
        when(tymService.findAllAktivni()).thenReturn(List.of(team));

        var result = apiReadService.getActiveTeams();

        assertEquals(1, result.size());
        assertEquals("Wolves", result.get(0).name());
        assertEquals("CVUT", result.get(0).university().name());
    }

    @Test
    void getTeam_propagatesNotFound() {
        when(tymService.findByIdOrThrow(77L))
                .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));

        assertThrows(ResponseStatusException.class, () -> apiReadService.getTeam(77L));
    }

    @Test
    void getStandings_mapsRowsToApiRows() {
        when(standingsService.calculateStandings()).thenReturn(List.of(
                new StandingsRowDTO("Wolves", 1L, 1, 1, 0, 0, 2, 0, 3, "logo.svg")
        ));

        var result = apiReadService.getStandings();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).teamId());
        assertEquals(3, result.get(0).points());
    }
}
