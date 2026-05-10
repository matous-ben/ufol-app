package cz.ufol.app.match;


import cz.ufol.app.season.Rocnik;
import cz.ufol.app.team.Tym;
import cz.ufol.app.venue.MistoKonani;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
        name = "zapasy",
        indexes = {
                @Index(name = "idx_zapasy_rocnik_id", columnList = "rocnik_id"),
                @Index(name = "idx_zapasy_domaci_tym_id", columnList = "domaci_tym_id"),
                @Index(name = "idx_zapasy_hoste_tym_id", columnList = "hoste_tym_id"),
                @Index(name = "idx_zapasy_misto_konani_id", columnList = "misto_konani_id"),
                @Index(name = "idx_zapasy_odehran_datum", columnList = "odehran, datum_cas")
        }
)
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

    @ManyToOne(fetch = FetchType.LAZY) // Season relation is needed only in selected use-cases; lazy avoids unnecessary joins.
    @JoinColumn(name = "rocnik_id", nullable = false)
    private Rocnik rocnik;

    @ManyToOne(fetch = FetchType.LAZY) // Team details are loaded for match pages only; lazy keeps generic queries lightweight.
    @JoinColumn(name = "domaci_tym_id", nullable = false)
    private Tym domaciTym;

    @ManyToOne(fetch = FetchType.LAZY) // Away team is read conditionally; lazy prevents over-fetching in write paths.
    @JoinColumn(name = "hoste_tym_id", nullable = false)
    private Tym hosteTym;

    @ManyToOne(fetch = FetchType.LAZY) // Venue is optional and not always rendered, so lazy loading is preferable.
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zapas zapas = (Zapas) o;
        return Objects.equals(rocnikId(), zapas.rocnikId())
                && Objects.equals(domaciTymId(), zapas.domaciTymId())
                && Objects.equals(hosteTymId(), zapas.hosteTymId())
                && Objects.equals(datumCas, zapas.datumCas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rocnikId(), domaciTymId(), hosteTymId(), datumCas);
    }

    private Long rocnikId() {
        return rocnik != null ? rocnik.getId() : null;
    }

    private Long domaciTymId() {
        return domaciTym != null ? domaciTym.getId() : null;
    }

    private Long hosteTymId() {
        return hosteTym != null ? hosteTym.getId() : null;
    }
}
