package cz.ufol.app.match;

import cz.ufol.app.player.Registrace;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ucasti_v_zapasech",
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

    @ManyToOne
    @JoinColumn(name = "zapas_id", nullable = false)
    private Zapas zapas;

    @ManyToOne
    @JoinColumn(name = "registrace_id", nullable = false)
    private Registrace registrace;
}
