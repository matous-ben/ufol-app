package cz.ufol.app.venue;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Objects;

@Entity
@Table(
        name = "mista_konani",
        indexes = {
                @Index(name = "idx_mista_konani_nazev", columnList = "nazev")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MistoKonani {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank
    @Column(length = 100, nullable = false)
    private String nazev;

    @Column(length = 100)
    private String ulice;

    @NotBlank
    @Column(length = 100, nullable = false)
    private String mesto;

    @Column(length = 5)
    private String psc;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MistoKonani that = (MistoKonani) o;
        return Objects.equals(nazev, that.nazev)
                && Objects.equals(ulice, that.ulice)
                && Objects.equals(mesto, that.mesto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nazev, ulice, mesto);
    }
}
