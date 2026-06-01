package cz.ufol.app.player;

import cz.ufol.app.university.Univerzita;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.Hibernate;

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
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Hrac that = (Hrac) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}
