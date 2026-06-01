package cz.ufol.app.event;

import cz.ufol.app.match.MatchParticipation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchEventRepository extends JpaRepository<MatchEvent, Long> {
    List<MatchEvent> findByMatchParticipation(MatchParticipation matchParticipation);
    List<MatchEvent> findByMatchParticipation_MatchIdAndEventType_Code(Long matchId, String code);
}
