package cz.ufol.app.player;

import cz.ufol.app.season.Rocnik;
import cz.ufol.app.team.Tym;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "registrace",
    indexes = {
        @Index(name = "idx_registrace_hrac_id", columnList = "hrac_id"),
        @Index(name = "idx_registrace_tym_id", columnList = "tym_id"),
        @Index(name = "idx_registrace_rocnik_id", columnList = "rocnik_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"hrac_id", "rocnik_id"})
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Registrace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Player details are only needed when rendering player-related views.
    @JoinColumn(name = "hrac_id", nullable = false)
    private Hrac hrac;

    @ManyToOne(fetch = FetchType.LAZY) // Team is loaded selectively for listings/forms; lazy prevents blanket joins.
    @JoinColumn(name = "tym_id", nullable = false)
    private Tym tym;

    @ManyToOne(fetch = FetchType.LAZY) // Season data is accessed in specific contexts only; lazy keeps default fetches lean.
    @JoinColumn(name = "rocnik_id",  nullable = false)
    private Rocnik rocnik;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registrace that = (Registrace) o;
        return Objects.equals(hracId(), that.hracId())
                && Objects.equals(rocnikId(), that.rocnikId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(hracId(), rocnikId());
    }

    private Long hracId() {
        return hrac != null ? hrac.getId() : null;
    }

    private Long rocnikId() {
        return rocnik != null ? rocnik.getId() : null;
    }
}
