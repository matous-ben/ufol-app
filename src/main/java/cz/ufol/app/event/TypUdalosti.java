package cz.ufol.app.event;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "typy_udalosti")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypUdalosti {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank
    @Column(length = 50, nullable = false, unique = true)
    private String nazev;

    @NotBlank
    @Column(length = 10, nullable = false, unique = true)
    private String kod;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypUdalosti that = (TypUdalosti) o;
        return Objects.equals(kod, that.kod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kod);
    }
}
