package cz.ufol.app.match;


import cz.ufol.app.season.Season;
import cz.ufol.app.team.Team;
import cz.ufol.app.venue.Venue;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "matches",
        indexes = {
                @Index(name = "idx_matches_season_id", columnList = "season_id"),
                @Index(name = "idx_matches_home_team_id", columnList = "home_team_id"),
                @Index(name = "idx_matches_away_team_id", columnList = "away_team_id"),
                @Index(name = "idx_matches_venue_id", columnList = "venue_id"),
                @Index(name = "idx_matches_played_datetime", columnList = "played, match_datetime")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Season relation is needed only in selected use-cases; lazy avoids unnecessary joins.
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    @ManyToOne(fetch = FetchType.LAZY) // Team details are loaded for match pages only; lazy keeps generic queries lightweight.
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.LAZY) // Away team is read conditionally; lazy prevents over-fetching in write paths.
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeam;

    @ManyToOne(fetch = FetchType.LAZY) // Venue is optional and not always rendered, so lazy loading is preferable.
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @Column(name = "match_datetime")
    private LocalDateTime matchDatetime;

    @Column(name = "played", nullable = false)
    private boolean played;

    @Column(name = "home_score")
    private Integer homeScore = 0;

    @Column(name = "away_score")
    private Integer awayScore = 0;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Match that = (Match) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }

    private Long seasonId() {
        return season != null ? season.getId() : null;
    }

    private Long homeTeamId() {
        return homeTeam != null ? homeTeam.getId() : null;
    }

    private Long awayTeamId() {
        return awayTeam != null ? awayTeam.getId() : null;
    }
}
