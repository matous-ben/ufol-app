package cz.ufol.app.api.v1;

import cz.ufol.app.api.ApiExceptionHandler;
import cz.ufol.app.api.v1.dto.ApiTeamDto;
import cz.ufol.app.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ApiV1Controller.class)
@Import({SecurityConfig.class, ApiExceptionHandler.class})
class ApiV1ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApiQueryService apiQueryService;

    @Test
    void teamsEndpointReturnsDtos() throws Exception {
        when(apiQueryService.getTeams()).thenReturn(List.of(
                new ApiTeamDto(1L, "UFOL A", true, "UP", "UP", "up.png")
        ));

        mockMvc.perform(get("/api/v1/teams")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "Admin1234"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nazev").value("UFOL A"));
    }

    @Test
    void playersEndpointReturnsNotFoundAsStandardJson() throws Exception {
        when(apiQueryService.getTeamPlayersStats(999L))
                .thenThrow(new ResponseStatusException(NOT_FOUND, "Tým nebyl nalezen."));

        mockMvc.perform(get("/api/v1/teams/999/players")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "Admin1234"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
}
