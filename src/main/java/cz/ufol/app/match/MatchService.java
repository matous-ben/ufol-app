package cz.ufol.app.match;

import cz.ufol.app.exception.BadRequestException;
import cz.ufol.app.exception.InvalidMatchResultException;
import cz.ufol.app.exception.ResourceNotFoundException;
import cz.ufol.app.match.dto.CreateMatchRequest;
import cz.ufol.app.match.dto.MatchResponse;
import cz.ufol.app.match.dto.RecordMatchResultRequest;
import cz.ufol.app.match.dto.UpdateMatchLogisticsRequest;
import cz.ufol.app.player.Registration;
import cz.ufol.app.player.RegistrationRepository;
import cz.ufol.app.season.Season;
import cz.ufol.app.season.SeasonRepository;
import cz.ufol.app.team.Team;
import cz.ufol.app.team.TeamRepository;
import cz.ufol.app.venue.Venue;
import cz.ufol.app.venue.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final SeasonRepository seasonRepository;
    private final VenueRepository venueRepository;
    private final RegistrationRepository registrationRepository;
    private final MatchParticipationRepository matchParticipationRepository;


    @Transactional(readOnly = true)
    public List<MatchResponse> getMatches() {
        return seasonRepository.findByActiveTrue()
                .map(season -> matchRepository.findBySeasonOrderByMatchDatetimeAsc(season)
                        .stream()
                        .map(this::mapToMatchResponse)
                        .toList())
                .orElse(List.of());
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> findUpcoming() {
        return seasonRepository.findByActiveTrue()
                .map(season -> matchRepository
                        .findBySeasonAndPlayedFalseOrderByMatchDatetimeAsc(season)
                        .stream()
                        .map(this::mapToMatchResponse)
                        .toList())
                .orElse(List.of());
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> findPlayed() {
        return seasonRepository.findByActiveTrue()
                .map(season -> matchRepository
                        .findBySeasonAndPlayedTrueOrderByMatchDatetimeDesc(season)
                        .stream()
                        .map(this::mapToMatchResponse)
                        .toList())
                .orElse(List.of());
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> findTop3Upcoming() {
        return seasonRepository.findByActiveTrue()
                .map(season -> matchRepository
                        .findBySeasonAndPlayedFalseOrderByMatchDatetimeAsc(season)
                        .stream()
                        .limit(3)
                        .map(this::mapToMatchResponse)
                        .toList())
                .orElse(List.of());
    }

    @Transactional(readOnly = true)
    public MatchResponse getMatchById(Long matchId) {
        return matchRepository.findById(matchId)
                .map(this::mapToMatchResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Match with ID " + matchId + " not found"));
    }

    public MatchResponse createMatch(CreateMatchRequest createMatchRequest) {
        if (createMatchRequest.homeTeamId().equals(createMatchRequest.awayTeamId())) {
            throw new BadRequestException("The home team can't be the same as the away team.");
        }

        Team homeTeam = teamRepository.findById(createMatchRequest.homeTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Home team with ID " + createMatchRequest.homeTeamId() + " not found"));
        Team awayTeam = teamRepository.findById(createMatchRequest.awayTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Away team with ID " + createMatchRequest.awayTeamId() + " not found"));
        Season season = seasonRepository.findById(createMatchRequest.seasonId())
                .orElseThrow(() -> new ResourceNotFoundException("Season with ID " + createMatchRequest.seasonId() + " not found"));
        LocalDateTime matchDateTime = createMatchRequest.dateTime();
        Venue venue = null;

        // load optional venue if present
        if (createMatchRequest.venueId() != null) {
            venue = venueRepository.findById(createMatchRequest.venueId())
                    .orElseThrow(() -> new ResourceNotFoundException("Venue with ID " + createMatchRequest.venueId() + " not found"));
        }

        // build the new match entity
        Match newMatch = Match.builder()
                .played(false)
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .season(season)
                .matchDatetime(matchDateTime)
                .venue(venue)
                .build();

        // save the new match and return a MatchResponse
        Match savedMatch = matchRepository.save(newMatch);
        return mapToMatchResponse(savedMatch);
    }

    @Transactional
    public MatchResponse updateMatchLogistics(Long matchId, UpdateMatchLogisticsRequest request) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match with ID " + matchId + " not found"));

        // check for venue presence and then if it exists
        Venue venue = match.getVenue();
        if (request.venueId() != null) {
            venue = venueRepository.findById(request.venueId())
                    .orElseThrow(() -> new ResourceNotFoundException("Venue with ID " + request.venueId() + " not found"));
        }

        // store the updated dateTime if present
        LocalDateTime matchDateTime = request.dateTime() != null ? request.dateTime() : match.getMatchDatetime();

        match.updateLogistics(venue, matchDateTime);
        return mapToMatchResponse(match);
    }

    @Transactional
    public MatchResponse recordMatchResult(Long matchId, RecordMatchResultRequest request) {
        // load the match if it exists
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match with ID " + matchId + " not found"));

        // load registrations for both teams
        List<Registration> homeRegistrations = registrationRepository
                .findBySeasonAndTeamOrderByPlayerLastNameAscPlayerFirstNameAsc(match.getSeason(), match.getHomeTeam());
        List<Registration> awayRegistrations = registrationRepository
                .findBySeasonAndTeamOrderByPlayerLastNameAscPlayerFirstNameAsc(match.getSeason(), match.getAwayTeam());

        // convert ID to HashSets for O(1) optimization
        Set<Long> homePlayerIds = homeRegistrations.stream()
                .map(Registration::getId)
                .collect(Collectors.toSet());

        Set<Long> awayPlayerIds = awayRegistrations.stream()
                .map(Registration::getId)
                .collect(Collectors.toSet());

        // business validation of goals
        int calculatedHomeScore = request.playerParticipations().stream()
                .filter(p -> homePlayerIds.contains(p.registrationId()))
                .mapToInt(RecordMatchResultRequest.PlayerStatsRequest::goals)
                .sum();

        int calculatedAwayScore = request.playerParticipations().stream()
                .filter(p -> awayPlayerIds.contains(p.registrationId()))
                .mapToInt(RecordMatchResultRequest.PlayerStatsRequest::goals)
                .sum();

        if (calculatedHomeScore != request.homeTeamScore() || calculatedAwayScore != request.awayTeamScore()) {
            throw new InvalidMatchResultException("The sum of player goals does not match the final team scores.");
        }

        // update the match result in the database
        match.recordResult(request.homeTeamScore(), request.awayTeamScore());

        // participation and statistics management (removing old, saving new)
        matchParticipationRepository.deleteByMatch(match);

        for (var playerDto : request.playerParticipations()) {
            boolean isHome = homePlayerIds.contains(playerDto.registrationId());
            boolean isAway = awayPlayerIds.contains(playerDto.registrationId());

            if (!isHome && !isAway) {
                throw new BadRequestException("Player with registration ID " + playerDto.registrationId() + " does not belong to either team.");
            }

            Registration registration = registrationRepository.getReferenceById(playerDto.registrationId());

            MatchParticipation participation = MatchParticipation.builder()
                    .match(match)
                    .registration(registration)
                    .goals(playerDto.goals())
                    .build();

            matchParticipationRepository.save(participation);
        }

        // transformation back into DTO
        return mapToMatchResponse(match);
    }

    @Transactional
    public void deleteMatch(Long matchId) {
        if (!matchRepository.existsById(matchId)) {
            throw new ResourceNotFoundException("Match with ID " + matchId + " not found");
        }
        matchRepository.deleteById(matchId);
    }

    // private helper method for mapping
    private MatchResponse mapToMatchResponse(Match match) {
        return new MatchResponse(
                match.getId(),
                new MatchResponse.TeamSummary(
                        match.getHomeTeam().getId(),
                        match.getHomeTeam().getName(),
                        match.getHomeTeam().getUniversity().getLogoUrl()
                ),
                new MatchResponse.TeamSummary(
                        match.getAwayTeam().getId(),
                        match.getAwayTeam().getName(),
                        match.getAwayTeam().getUniversity().getLogoUrl()
                ),
                match.getSeason().getName(),
                match.getVenue() != null ? match.getVenue().getName() : null,
                match.getMatchDatetime(),
                match.isPlayed(),
                match.getHomeScore(),
                match.getAwayScore()
        );
    }
}
