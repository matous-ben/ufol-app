package cz.ufol.app.season;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Objects;

@Entity
@Table(
        name = "rocniky",
        indexes = {
                @Index(name = "idx_rocniky_aktivni", columnList = "aktivni"),
                @Index(name = "idx_rocniky_rok_od", columnList = "rok_od")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rocnik {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank
    @Column(length = 50, nullable = false, unique = true)
    private String nazev;

    @NotNull
    @Column(name = "rok_od", nullable = false)
    private Integer rokOd;

    @NotNull
    @Column(name = "rok_do", nullable = false)
    private Integer rokDo;

    @Column(nullable = false)
    private boolean aktivni = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rocnik rocnik = (Rocnik) o;
        return Objects.equals(nazev, rocnik.nazev);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nazev);
    }
}
