package cz.ufol.app.university;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.Hibernate;

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
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Univerzita that = (Univerzita) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}
