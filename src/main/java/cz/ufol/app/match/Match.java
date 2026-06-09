package cz.ufol.app.match;


import cz.ufol.app.exception.BadRequestException;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "matches_seq",
            sequenceName = "matches_sequence"
    )
    @Column(updatable = false, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @Column(name = "match_datetime")
    private LocalDateTime matchDatetime;

    @Column(name = "played", nullable = false)
    private boolean played;

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    public void recordResult(Integer homeScore, Integer awayScore) {
        if (homeScore < 0 || awayScore < 0) {
            throw new BadRequestException("Score cannot be negative");
        }
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.played = true;
    }

    public void updateLogistics(Venue venue, LocalDateTime matchDatetime) {
        if (this.played) {
            throw new BadRequestException("Cannot update logistics for a match that has already been played.");
        }
        this.venue = venue;
        this.matchDatetime = matchDatetime;
    }

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
}
