package cz.ufol.app.player;

import cz.ufol.app.season.Season;
import cz.ufol.app.team.Team;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    @EntityGraph(attributePaths = {"player", "team", "team.university", "season"})
    List<Registration> findByTeamAndSeason(Team team, Season season);

    @EntityGraph(attributePaths = {"player", "team", "team.university", "season"})
    List<Registration> findBySeasonAndTeamOrderByPlayerLastNameAscPlayerFirstNameAsc(Season season, Team team);

    @EntityGraph(attributePaths = {"player", "team", "team.university", "season"})
    List<Registration> findByPlayerId(Long playerId);
    boolean existsByPlayerIdAndSeasonId(Long playerId, Long seasonId);
}
