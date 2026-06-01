package cz.ufol.app.standings;

import cz.ufol.app.match.Match;
import cz.ufol.app.match.MatchRepository;
import cz.ufol.app.season.Season;
import cz.ufol.app.season.SeasonRepository;
import cz.ufol.app.team.Team;
import cz.ufol.app.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StandingsService {

    private final MatchRepository matchRepository;
    private final SeasonRepository seasonRepository;
    private final TeamRepository teamRepository;

    private static class TeamStats {
        private int played = 0;
        private int wins = 0;
        private int draws = 0;
        private int losses = 0;
        private int goalsFor = 0;
        private int goalsAgainst = 0;

        public void addMatchResult(int goalsFor, int goalsAgainst) {
            this.played++;
            this.goalsFor += goalsFor;
            this.goalsAgainst += goalsAgainst;

            if (goalsFor > goalsAgainst) {
                this.wins++;
            } else if (goalsFor < goalsAgainst) {
                this.losses++;
            } else {
                this.draws++;
            }
        }

        public int getPoints() {
            return (this.wins * 3) + this.draws;
        }
    }

    public List<StandingsRowDTO> calculateStandings() {
        Optional<Season> activeSeason = seasonRepository.findByActiveTrue();
        if (activeSeason.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. Map by ID to guarantee uniqueness, regardless of Java memory addresses
        Map<Long, TeamStats> statsMap = new LinkedHashMap<>();
        Map<Long, Team> teamsDetailsMap = new HashMap<>();

        // 2. Defensive Initialization
        List<Team> allActiveTeams = teamRepository.findByActiveTrue();
        for (Team team : allActiveTeams) {
            statsMap.put(team.getId(), new TeamStats());
            teamsDetailsMap.put(team.getId(), team);
        }

        // 3. Process matches ONLY for active teams
        List<Match> playedMatches = matchRepository
                .findBySeasonAndPlayedTrue(activeSeason.get());

        for (Match match : playedMatches) {
            Team home = match.getHomeTeam();
            Team away = match.getAwayTeam();

            int homeScore = match.getHomeScore() != null ? match.getHomeScore() : 0;
            int awayScore = match.getAwayScore()  != null ? match.getAwayScore() : 0;

            // Only add stats if the team is in our active map
            if (statsMap.containsKey(home.getId())) {
                statsMap.get(home.getId()).addMatchResult(homeScore, awayScore);
            }

            if (statsMap.containsKey(away.getId())) {
                statsMap.get(away.getId()).addMatchResult(awayScore, homeScore);
            }
        }

        // 4. Map to DTOs
        List<StandingsRowDTO> standings = new ArrayList<>();
        for (Map.Entry<Long, TeamStats> entry : statsMap.entrySet()) {
            Long teamId = entry.getKey();
            TeamStats s = entry.getValue();
            Team team = teamsDetailsMap.get(teamId);

            standings.add(new StandingsRowDTO(
                    team.getName(),
                    team.getId(),
                    s.played,
                    s.wins,
                    s.draws,
                    s.losses,
                    s.goalsFor,
                    s.goalsAgainst,
                    s.getPoints(),
                    team.getUniversity().getLogoFile()
            ));
        }

        // 5. Explicit FIFA-Standard sorting
        standings.sort((teamA, teamB) -> {
            // Rule 1: Points (Descending)
            int pointsCompare = Integer.compare(teamB.getPoints(), teamA.getPoints());
            if (pointsCompare != 0) return pointsCompare;

            // Rule 2: Goal Difference (Descending)
            int diffCompare = Integer.compare(teamB.getGoalDifference(), teamA.getGoalDifference());
            if (diffCompare != 0) return diffCompare;

            // Rule 3: Goals Scored (Descending)
            int scoredCompare = Integer.compare(teamB.getGoalsFor(), teamA.getGoalsFor());
            if (scoredCompare != 0) return scoredCompare;

            // Rule 4: Alphabetical order (Ascending)
            return teamA.getTeamName().compareToIgnoreCase(teamB.getTeamName());
        });

        return standings;
    }
}
