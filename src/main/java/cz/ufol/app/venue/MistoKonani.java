package cz.ufol.app.venue;

import cz.ufol.app.university.Univerzita;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.Hibernate;

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
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MistoKonani that = (MistoKonani) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}
