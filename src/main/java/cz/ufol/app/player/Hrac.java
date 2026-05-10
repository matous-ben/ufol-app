package cz.ufol.app.player;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(
        name = "hraci",
        indexes = {
                @Index(name = "idx_hraci_prijmeni_jmeno", columnList = "prijmeni, jmeno")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hrac {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank
    @Column(length = 50, nullable = false)
    private String jmeno;

    @NotBlank
    @Column(length = 50, nullable = false)
    private String prijmeni;

    @Column(name = "datum_narozeni")
    private LocalDate datumNarozeni;

    @Column(name = "foto_url", length = 255)
    private String fotoUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hrac hrac = (Hrac) o;
        return Objects.equals(jmeno, hrac.jmeno)
                && Objects.equals(prijmeni, hrac.prijmeni)
                && Objects.equals(datumNarozeni, hrac.datumNarozeni);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jmeno, prijmeni, datumNarozeni);
    }
}
