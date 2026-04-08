package cz.ufol.app.player;

import cz.ufol.app.season.Rocnik;
import cz.ufol.app.team.Tym;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "registrace",
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

    @ManyToOne
    @JoinColumn(name = "hrac_id", nullable = false)
    private Hrac hrac;

    @ManyToOne
    @JoinColumn(name = "tym_id", nullable = false)
    private Tym tym;

    @ManyToOne
    @JoinColumn(name = "rocnik_id",  nullable = false)
    private Rocnik rocnik;
}
