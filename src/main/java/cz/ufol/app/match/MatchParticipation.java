package cz.ufol.app.match;

import cz.ufol.app.player.Registration;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

@Entity
@Table(name = "match_participations",
    indexes = {
        @Index(name = "idx_match_participations_match_id", columnList = "match_id"),
        @Index(name = "idx_match_participations_registration_id", columnList = "registration_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"match_id", "registration_id"})
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Match is loaded only for reporting/edit flows; lazy avoids default eager joins.
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY) // Registration/player details are needed only in selected screens; lazy is more efficient.
    @JoinColumn(name = "registration_id", nullable = false)
    private Registration registration;

    @Column
    private Integer goals = 0;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MatchParticipation that = (MatchParticipation) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }

    private Long zapasId() {
        return match != null ? match.getId() : null;
    }

    private Long registraceId() {
        return registration != null ? registration.getId() : null;
    }
}
