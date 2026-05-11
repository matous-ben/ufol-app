package cz.ufol.app.match;

import cz.ufol.app.player.Registrace;
import cz.ufol.app.university.Univerzita;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Table(name = "ucasti_v_zapasech",
    indexes = {
        @Index(name = "idx_ucasti_zapas_id", columnList = "zapas_id"),
        @Index(name = "idx_ucasti_registrace_id", columnList = "registrace_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"zapas_id", "registrace_id"})
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UcastVZapase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Match is loaded only for reporting/edit flows; lazy avoids default eager joins.
    @JoinColumn(name = "zapas_id", nullable = false)
    private Zapas zapas;

    @ManyToOne(fetch = FetchType.LAZY) // Registration/player details are needed only in selected screens; lazy is more efficient.
    @JoinColumn(name = "registrace_id", nullable = false)
    private Registrace registrace;

    @Column
    private Integer goly = 0;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UcastVZapase that = (UcastVZapase) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }

    private Long zapasId() {
        return zapas != null ? zapas.getId() : null;
    }

    private Long registraceId() {
        return registrace != null ? registrace.getId() : null;
    }
}
