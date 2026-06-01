package cz.ufol.app.match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

import cz.ufol.app.player.Registration;

public interface MatchParticipationRepository extends JpaRepository<MatchParticipation, Long> {
    @EntityGraph(attributePaths = {"match", "registration", "registration.player"})
    List<MatchParticipation> findByMatch(Match match);
    void deleteByMatch(Match match);

    @EntityGraph(attributePaths = {"match", "registration", "registration.player"})
    List<MatchParticipation> findByRegistrationIn(List<Registration> registration);
    void deleteByRegistrationIn(List<Registration> registration);
}
