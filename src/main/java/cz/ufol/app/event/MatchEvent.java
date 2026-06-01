package cz.ufol.app.event;

import cz.ufol.app.match.UcastVZapase;
import cz.ufol.app.university.Univerzita;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Table(
        name = "udalosti_zapasu",
        indexes = {
                @Index(name = "idx_udalosti_ucast_id", columnList = "ucast_v_zapase_id"),
                @Index(name = "idx_udalosti_typ_id", columnList = "typ_udalosti_id"),
                @Index(name = "idx_udalosti_minuta", columnList = "minuta")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UdalostZapasu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Participation is only needed when listing match events.
    @JoinColumn(name = "ucast_v_zapase_id", nullable = false)
    private UcastVZapase ucastVZapase;

    @ManyToOne(fetch = FetchType.LAZY) // Event type metadata is referenced on demand in event views.
    @JoinColumn(name = "typ_udalosti_id", nullable = false)
    private TypUdalosti typUdalosti;

    private Integer minuta;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UdalostZapasu that = (UdalostZapasu) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }

    private Long ucastId() {
        return ucastVZapase != null ? ucastVZapase.getId() : null;
    }

    private Long typUdalostiId() {
        return typUdalosti != null ? typUdalosti.getId() : null;
    }
}
