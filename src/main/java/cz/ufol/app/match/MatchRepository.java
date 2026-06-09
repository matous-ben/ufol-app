package cz.ufol.app.match;

import cz.ufol.app.season.Season;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    @EntityGraph(attributePaths = {"season", "homeTeam", "homeTeam.university", "awayTeam", "awayTeam.university", "venue"})
    List<Match> findBySeasonAndPlayedFalseOrderByMatchDatetimeAsc(Season season);

    @EntityGraph(attributePaths = {"season", "homeTeam", "homeTeam.university", "awayTeam", "awayTeam.university", "venue"})
    List<Match> findBySeasonAndPlayedTrueOrderByMatchDatetimeDesc(Season season);

    @EntityGraph(attributePaths = {"season", "homeTeam", "homeTeam.university", "awayTeam", "awayTeam.university", "venue"})
    List<Match> findBySeasonAndPlayedTrue(Season season);

    @EntityGraph(attributePaths = {"season", "homeTeam", "homeTeam.university", "awayTeam", "awayTeam.university", "venue"})
    List<Match> findBySeasonOrderByMatchDatetimeAsc(Season season);
}
