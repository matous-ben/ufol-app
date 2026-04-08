package cz.ufol.app.match;


import cz.ufol.app.season.Rocnik;
import cz.ufol.app.team.Tym;
import cz.ufol.app.venue.MistoKonani;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "zapasy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Zapas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rocnik_id", nullable = false)
    private Rocnik rocnik;

    @ManyToOne
    @JoinColumn(name = "domaci_tym_id", nullable = false)
    private Tym domaciTym;

    @ManyToOne
    @JoinColumn(name = "hoste_tym_id", nullable = false)
    private Tym hosteTym;

    @ManyToOne
    @JoinColumn(name = "misto_konani_id")
    private MistoKonani mistoKonani;

    @Column(name = "datum_cas")
    private LocalDateTime datumCas;

    @Column(nullable = false)
    private boolean odehran;

    @Column(name = "domaci_skore")
    private Integer domaciSkore = 0;

    @Column(name = "hoste_skore")
    private Integer hosteSkore = 0;
}
