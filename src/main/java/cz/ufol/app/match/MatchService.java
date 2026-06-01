package cz.ufol.app.match;

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
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final SeasonRepository seasonRepository;
    private final VenueRepository venueRepository;
    private final RegistrationRepository registrationRepository;
    private final MatchParticipationRepository matchParticipationRepository;

    public record ServiceResult(String redirectPath, String flashType, String flashMessage) {}
    public record AdminMatchesListData(Season activeSeason, List<Match> upcoming, List<Match> played) {}
    public record AdminMatchFormData(List<Team> teams, List<Season> seasons, List<Venue> venues) {}
    public record AdminResultFormData(
            boolean found,
            String errorMessage,
            Match match,
            List<Registration> homeTeamRegistration,
            List<Registration> awayTeamRegistration,
            Set<Long> selectedRegistrationIds,
            Map<Long, Integer> goalsMap
    ) {}

    @Transactional(readOnly = true)
    public List<Match> findUpcoming() {
        Optional<Season> activeSeason = seasonRepository.findByActiveTrue();
        if (activeSeason.isEmpty()) return Collections.emptyList();
        return matchRepository
                .findBySeasonAndPlayedFalseOrderByMatchDatetimeAsc(activeSeason.get());
    }

    @Transactional(readOnly = true)
    public List<Match> findPlayed() {
        Optional<Season> activeSeason = seasonRepository.findByActiveTrue();
        if (activeSeason.isEmpty()) return Collections.emptyList();
        return matchRepository
                .findBySeasonAndPlayedTrueOrderByMatchDatetimeDesc(activeSeason.get());
    }

    @Transactional(readOnly = true)
    public List<Match> findTop3Upcoming() {
        Optional<Season> activeSeason = seasonRepository.findByActiveTrue();
        if (activeSeason.isEmpty()) return Collections.emptyList();
        return matchRepository
                .findBySeasonAndPlayedFalseOrderByMatchDatetimeAsc(activeSeason.get())
                .stream()
                .limit(3)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Match> findTop3UpcomingForHome() {
        return matchRepository.findTop3ByPlayedFalseOrderByMatchDatetimeAsc();
    }

    @Transactional(readOnly = true)
    public AdminMatchesListData getAdminMatchesListData() {
        var activeSeason = seasonRepository.findByActiveTrue().orElse(null);
        if (activeSeason == null) {
            return new AdminMatchesListData(null, List.of(), List.of());
        }

        return new AdminMatchesListData(
                activeSeason,
                matchRepository.findBySeasonAndPlayedFalseOrderByMatchDatetimeAsc(activeSeason),
                matchRepository.findBySeasonAndPlayedTrueOrderByMatchDatetimeDesc(activeSeason)
        );
    }

    @Transactional(readOnly = true)
    public AdminMatchFormData getAdminMatchFormData() {
        return new AdminMatchFormData(
                teamRepository.findByActiveTrue(),
                seasonRepository.findAllByOrderByYearFromDesc(),
                venueRepository.findAllByOrderByNameAsc()
        );
    }

    @Transactional
    public ServiceResult createAdminMatch(Long homeTeamId, Long awayTeamId, Long seasonId, Long venueId, String dateTime) {
        if (homeTeamId.equals(awayTeamId)) {
            return new ServiceResult("/admin/zapasy/novy", "error", "Tým nemůže hrát sám proti sobě.");
        }

        var homeTeamOpt = teamRepository.findById(homeTeamId);
        if (homeTeamOpt.isEmpty()) {
            return new ServiceResult("/admin/zapasy/novy", "error", "Domácí tým nebyl nalezen.");
        }

        var awayTeamOpt = teamRepository.findById(awayTeamId);
        if (awayTeamOpt.isEmpty()) {
            return new ServiceResult("/admin/zapasy/novy", "error", "Hostující tým nebyl nalezen.");
        }

        var seasonOpt = seasonRepository.findById(seasonId);
        if (seasonOpt.isEmpty()) {
            return new ServiceResult("/admin/zapasy/novy", "error", "Vybraný ročník nebyl nalezen.");
        }

        var venue = venueId != null ? venueRepository.findById(venueId).orElse(null) : null;
        if (venueId != null && venue == null) {
            return new ServiceResult("/admin/zapasy/novy", "error", "Vybrané místo konání nebylo nalezeno.");
        }

        LocalDateTime parsedDateTime = null;
        if (dateTime != null && !dateTime.isBlank()) {
            try {
                parsedDateTime = LocalDateTime.parse(dateTime);
            } catch (DateTimeParseException e) {
                return new ServiceResult("/admin/zapasy/novy", "error", "Neplatný formát data a času. Použijte prosím validní datum.");
            }
        }

        matchRepository.save(Match.builder()
                .homeTeam(homeTeamOpt.get())
                .awayTeam(awayTeamOpt.get())
                .season(seasonOpt.get())
                .venue(venue)
                .matchDatetime(parsedDateTime)
                .played(false)
                .homeScore(0)
                .awayScore(0)
                .build());

        return new ServiceResult("/admin/zapasy", "success", "Zápas byl přidán.");
    }

    @Transactional(readOnly = true)
    public AdminResultFormData getAdminResultFormData(Long matchId) {
        var MatchOpt = matchRepository.findById(matchId);
        if (MatchOpt.isEmpty()) {
            return new AdminResultFormData(false, "Zápas nebyl nalezen.", null, List.of(), List.of(), Set.of(), Map.of());
        }

        var match = MatchOpt.get();
        var homeTeamRegistration = registrationRepository.findBySeasonAndTeamOrderByPlayerLastNameAscPlayerFirstNameAsc(match.getSeason(), match.getHomeTeam());
        var awayTeamRegistration = registrationRepository.findBySeasonAndTeamOrderByPlayerLastNameAscPlayerFirstNameAsc(match.getSeason(), match.getAwayTeam());
        var participations = matchParticipationRepository.findByMatch(match);

        Set<Long> selectedRegistrationIds = participations.stream()
                .map(u -> u.getRegistration().getId())
                .collect(Collectors.toSet());
        Map<Long, Integer> golyMap = participations.stream()
                .collect(Collectors.toMap(u -> u.getRegistration().getId(), MatchParticipation::getGoals));

        return new AdminResultFormData(true, null, match, homeTeamRegistration, awayTeamRegistration, selectedRegistrationIds, golyMap);
    }

    @Transactional
    public ServiceResult saveAdminResult(Long id, Integer homeTeamScore, Integer awayTeamScore, List<Long> registrationIds, Map<String, String[]> parameters) {
        if (homeTeamScore == null || awayTeamScore == null) {
            return new ServiceResult("/admin/zapasy/" + id + "/vysledek", "error", "Skóre musí být vyplněno.");
        }
        if (homeTeamScore < 0 || awayTeamScore < 0) {
            return new ServiceResult("/admin/zapasy/" + id + "/vysledek", "error", "Skóre nemůže být záporné.");
        }

        var MatchOpt = matchRepository.findById(id);
        if (MatchOpt.isEmpty()) {
            return new ServiceResult("/admin/zapasy", "error", "Zápas nebyl nalezen.");
        }

        var match = MatchOpt.get();
        match.setHomeScore(homeTeamScore);
        match.setAwayScore(awayTeamScore);
        match.setPlayed(true);
        matchRepository.save(match);

        var homeTeamRegistration = registrationRepository.findBySeasonAndTeamOrderByPlayerLastNameAscPlayerFirstNameAsc(match.getSeason(), match.getHomeTeam());
        var awayTeamRegistration = registrationRepository.findBySeasonAndTeamOrderByPlayerLastNameAscPlayerFirstNameAsc(match.getSeason(), match.getAwayTeam());

        Set<Long> allowedRegistrationIds = Stream.concat(homeTeamRegistration.stream().map(Registration::getId), awayTeamRegistration.stream().map(Registration::getId))
                .collect(Collectors.toSet());

        matchParticipationRepository.deleteByMatch(match);

        if (registrationIds != null && !registrationIds.isEmpty()) {
            var validniRegistrace = registrationRepository.findAllById(registrationIds).stream()
                    .filter(r -> allowedRegistrationIds.contains(r.getId()))
                    .toList();

            for (var registration : validniRegistrace) {
                String goalsRaw = firstValue(parameters.get("goly_" + registration.getId()));
                int goals = parseGoals(goalsRaw);
                matchParticipationRepository.save(MatchParticipation.builder()
                        .match(match)
                        .registration(registration)
                        .goals(goals)
                        .build());
            }
        }

        return new ServiceResult("/admin/zapasy", "success", "Výsledek zápasu byl uložen. Tabulka se automaticky aktualizovala.");
    }

    @Transactional
    public ServiceResult deleteAdminMatch(Long id) {
        if (!matchRepository.existsById(id)) {
            return new ServiceResult("/admin/zapasy", "error", "Zápas nebyl nalezen.");
        }
        matchRepository.deleteById(id);
        return new ServiceResult("/admin/zapasy", "success", "Zápas byl smazán.");
    }

    private String firstValue(String[] values) {
        return values == null || values.length == 0 ? null : values[0];
    }

    private int parseGoals(String goalsRaw) {
        if (goalsRaw == null || goalsRaw.isBlank()) {
            return 0;
        }
        try {
            int parsed = Integer.parseInt(goalsRaw);
            return Math.max(parsed, 0);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }
}
