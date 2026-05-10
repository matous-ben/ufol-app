package cz.ufol.app.team;


import cz.ufol.app.university.Univerzita;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Objects;

@Entity
@Table(
        name = "tymy",
        indexes = {
                @Index(name = "idx_tymy_univerzita_id", columnList = "univerzita_id"),
                @Index(name = "idx_tymy_aktivni", columnList = "aktivni")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tym {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank
    @Column(length = 100, nullable = false, unique = true)
    private String nazev;

    @Column(nullable = false)
    private boolean aktivni = true;

    @ManyToOne(fetch = FetchType.LAZY) // University details are rendered in selected views only; lazy avoids eager graph loading.
    @JoinColumn(name = "univerzita_id", nullable = false)
    private Univerzita univerzita;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tym tym = (Tym) o;
        return Objects.equals(nazev, tym.nazev);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nazev);
    }
}
