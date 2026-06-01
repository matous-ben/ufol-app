package cz.ufol.app.event;

import cz.ufol.app.match.MatchParticipation;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

@Entity
@Table(
        name = "match_events",
        indexes = {
                @Index(name = "idx_match_events_participation_id", columnList = "match_participation_id"),
                @Index(name = "idx_match_events_event_type_id", columnList = "event_type_id"),
                @Index(name = "idx_match_events_minute", columnList = "minute")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Participation is only needed when listing match events.
    @JoinColumn(name = "match_participation_id", nullable = false)
    private MatchParticipation matchParticipation;

    @ManyToOne(fetch = FetchType.LAZY) // Event type metadata is referenced on demand in event views.
    @JoinColumn(name = "event_type_id", nullable = false)
    private EventType eventType;

    @Column(name = "minute")
    private Integer minute;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MatchEvent that = (MatchEvent) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }

    private Long matchParticipationId() {
        return matchParticipation != null ? matchParticipation.getId() : null;
    }

    private Long eventTypeId() {
        return eventType != null ? eventType.getId() : null;
    }
}
