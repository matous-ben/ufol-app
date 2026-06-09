package cz.ufol.app;

import cz.ufol.app.exception.BadRequestException;
import cz.ufol.app.exception.ResourceNotFoundException;
import cz.ufol.app.match.*;
import cz.ufol.app.match.dto.CreateMatchRequest;
import cz.ufol.app.match.dto.MatchResponse;
import cz.ufol.app.match.dto.RecordMatchResultRequest;
import cz.ufol.app.player.Registration;
import cz.ufol.app.player.RegistrationRepository;
import cz.ufol.app.season.Season;
import cz.ufol.app.season.SeasonRepository;
import cz.ufol.app.team.Team;
import cz.ufol.app.team.TeamRepository;
import cz.ufol.app.university.University;
import cz.ufol.app.venue.Venue;
import cz.ufol.app.venue.VenueRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private SeasonRepository seasonRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private MatchParticipationRepository matchParticipationRepository;

    @InjectMocks
    private MatchService matchService;

    @Test
    @DisplayName("Should successfully record match result and return response when data is valid")
    void givenValidResultRequest_whenRecordMatchResult_thenReturnUpdatedMatchResponse() {
        // GIVEN
        Long matchId = 1L;

        // tested objects
        University university = University.builder().logoUrl("logo.png").build();
        Season season = Season.builder().id(10L).name("2025/2026").build();
        Team homeTeam = Team.builder().id(2L).name("Home Team FC").university(university).build();
        Team awayTeam = Team.builder().id(3L).name("Away Team FC").university(university).build();

        Match existingMatch = Match.builder()
                .id(matchId)
                .season(season)
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .played(false)
                .homeScore(0)
                .awayScore(0)
                .build();

        // registrations for tested players
        Registration homePlayer = Registration.builder().id(100L).build();
        Registration awayPlayer = Registration.builder().id(200L).build();

        // creating Request DTO as an input from admin (frontend)
        // home team won 2:1
        // player with a registration id 100 scored 2 goals
        // player with registration id 200 scored 1 goal.
        RecordMatchResultRequest request = new RecordMatchResultRequest(
                2,
                1,
                List.of(
                        new RecordMatchResultRequest.PlayerStatsRequest(100L, 2),
                        new RecordMatchResultRequest.PlayerStatsRequest(200L, 1)
                )
        );

        // stubbing: how should mocks behave when service is calling them
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(existingMatch));

        when(registrationRepository.findBySeasonAndTeamOrderByPlayerLastNameAscPlayerFirstNameAsc(season, homeTeam))
                .thenReturn(List.of(homePlayer));

        when(registrationRepository.findBySeasonAndTeamOrderByPlayerLastNameAscPlayerFirstNameAsc(season, awayTeam))
                .thenReturn(List.of(awayPlayer));

        when(registrationRepository.getReferenceById(100L)).thenReturn(homePlayer);
        when(registrationRepository.getReferenceById(200L)).thenReturn(awayPlayer);

        // WHEN:
        MatchResponse expected = matchService.recordMatchResult(matchId, request);

        // THEN:
        // 1. checking if the data changed correctly
        assertThat(expected).isNotNull();
        assertThat(expected.id()).isEqualTo(matchId);
        assertThat(expected.played()).isTrue();
        assertThat(expected.homeScore()).isEqualTo(2);
        assertThat(expected.awayScore()).isEqualTo(1);
        assertThat(expected.homeTeam().name()).isEqualTo("Home Team FC");

        // 2. checks if the entity itself changed correctly
        assertThat(existingMatch.isPlayed()).isTrue();
        assertThat(existingMatch.getHomeScore()).isEqualTo(2);

        // 3. checks if the service really updates and saves the statistics
        verify(matchParticipationRepository, times(1)).deleteByMatch(existingMatch);
        verify(matchParticipationRepository, times(2)).save(any(MatchParticipation.class));
    }

    @Test
    @DisplayName("Should successfully return a single match DTO if it exists")
    void givenExistingMatchId_whenGetMatchById_thenReturnMatchResponse() {
        // GIVEN
        Long matchId = 1L;

        University university = University.builder().logoUrl("logo.png").build();
        Season season = Season.builder().id(10L).name("2025/2026").build();
        Team homeTeam = Team.builder().id(2L).name("Home Team FC").university(university).build();
        Team awayTeam = Team.builder().id(3L).name("Away Team FC").university(university).build();
        Venue venue = Venue.builder().id(20L).name("Venue Name").city("City").street("Street").postalCode("12345").build();
        LocalDateTime dateTime = LocalDateTime.now();

        Match existingMatch = Match.builder()
                .id(matchId)
                .season(season)
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .played(true)
                .homeScore(3)
                .awayScore(2)
                .venue(venue)
                .matchDatetime(dateTime)
                .build();

        // stubbing
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(existingMatch));

        // WHEN
        MatchResponse response = matchService.getMatchById(matchId);

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.season()).isEqualTo("2025/2026");
        assertThat(response.homeTeam().id()).isEqualTo(2L);
        assertThat(response.awayTeam().id()).isEqualTo(3L);
        assertThat(response.played()).isTrue();
        assertThat(response.homeScore()).isEqualTo(3);
        assertThat(response.awayScore()).isEqualTo(2);
        assertThat(response.venue()).isEqualTo("Venue Name");

    }

    @Test
    @DisplayName("Should return an error message saying that the match does not exist")
    void givenNonExistentMatchId_whenGetMatchById_thenReturnErrorMessage() {

        // GIVEN
        Long matchId = 1L;

        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThatThrownBy(() -> matchService.getMatchById(matchId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Match with ID " + matchId + " not found");

        verifyNoInteractions(matchParticipationRepository);
    }

    @Test
    @DisplayName("Should accept a valid CreateMatchRequest, save it in DB and return the saved entity mapped into MatchResponse")
    void givenValidCreateMatchRequest_whenCreateMatch_thenReturnCreatedMatchResponse() {

        // GIVEN
        University homeUniversity = University.builder()
                .id(1L).name("Univerzita Pardubice").abbreviation("UPCE").logoUrl("abc.com").build();
        University awayUniversity = University.builder()
                .id(2L).name("Univerzita Hradec").abbreviation("UHK").logoUrl("xyz.com").build();
        Team homeTeam = Team.builder().id(1L).name("Home Team FC").university(homeUniversity).build();
        Team awayTeam = Team.builder().id(2L).name("Away Team FC").university(awayUniversity).build();
        Season season = Season.builder().id(10L).name("2025/2026").build();
        Venue venue = Venue.builder().id(5L).name("Stadium name").city("City").street("Street").build();
        LocalDateTime dateTime = LocalDateTime.now();

        CreateMatchRequest request = new CreateMatchRequest(
                homeTeam.getId(),
                awayTeam.getId(),
                season.getId(),
                venue.getId(),
                dateTime
        );

        Long matchId = 99L;
        Match savedMatch = Match.builder()
                .id(matchId)
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .season(season)
                .venue(venue)
                .matchDatetime(dateTime)
                .played(false)
                .build();

        // stubbing: how should mock repositories behave
        when(teamRepository.findById(1L)).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(awayTeam));
        when(seasonRepository.findById(10L)).thenReturn(Optional.of(season));
        when(venueRepository.findById(5L)).thenReturn(Optional.of(venue));

        when(matchRepository.save(any(Match.class))).thenReturn(savedMatch);

        // WHEN
        MatchResponse response = matchService.createMatch(request);

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(matchId);
        assertThat(response.season()).isEqualTo("2025/2026");

        assertThat(response.homeTeam().id()).isEqualTo(1L);
        assertThat(response.homeTeam().name()).isEqualTo("Home Team FC");

        assertThat(response.awayTeam().id()).isEqualTo(2L);
        assertThat(response.awayTeam().name()).isEqualTo("Away Team FC");

        assertThat(response.played()).isFalse();
    }

    @Test
    @DisplayName("Should throw BadRequestException when home team is the same as away team")
    void givenSameHomeAndAwayTeamId_whenCreateMatch_thenThrowBadRequestException() {

        // GIVEN - same ID for both teams
        Long identicalTeamId = 1L;
        LocalDateTime dateTime = LocalDateTime.now();

        CreateMatchRequest request = new CreateMatchRequest(
                identicalTeamId,
                identicalTeamId,
                10L,
                5L,
                dateTime
        );

        // WHEN + THEN
        assertThatThrownBy(() -> matchService.createMatch(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("The home team can't be the same as the away team.");
        
        verifyNoInteractions(teamRepository, seasonRepository, venueRepository, matchRepository);
    }
}
