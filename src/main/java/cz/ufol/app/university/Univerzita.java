package cz.ufol.app.university;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "univerzity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Univerzita {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank
    @Column(length = 150, nullable = false, unique = true)
    private String nazev;

    @NotBlank
    @Column(length = 15, nullable = false, unique = true)
    private String zkratka;

    @Column(name = "logo_file", length = 50)
    private String logoFile;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Univerzita that = (Univerzita) o;
        return Objects.equals(zkratka, that.zkratka);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zkratka);
    }
}
