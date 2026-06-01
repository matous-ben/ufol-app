package cz.ufol.app.player;

import cz.ufol.app.season.Season;
import cz.ufol.app.team.Team;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

@Entity
@Table(name = "registrations",
    indexes = {
        @Index(name = "idx_registrations_player_id", columnList = "player_id"),
        @Index(name = "idx_registrations_team_id", columnList = "team_id"),
        @Index(name = "idx_registrations_season_id", columnList = "season_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"player_id", "season_id"})
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Player details are only needed when rendering player-related views.
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY) // Team is loaded selectively for listings/forms; lazy prevents blanket joins.
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY) // Season data is accessed in specific contexts only; lazy keeps default fetches lean.
    @JoinColumn(name = "season_id",  nullable = false)
    private Season season;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Registration that = (Registration) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }

    private Long playerId() {
        return player != null ? player.getId() : null;
    }

    private Long seasonId() {
        return season != null ? season.getId() : null;
    }
}
