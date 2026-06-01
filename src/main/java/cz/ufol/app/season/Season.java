package cz.ufol.app.season;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.Hibernate;

@Entity
@Table(
        name = "seasons",
        indexes = {
                @Index(name = "idx_seasons_active", columnList = "active"),
                @Index(name = "idx_seasons_year_from", columnList = "year_from")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Season {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank
    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    @NotNull
    @Column(name = "year_from", nullable = false)
    private Integer yearFrom;

    @NotNull
    @Column(name = "year_to", nullable = false)
    private Integer yearTo;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Season season = (Season) o;
        return id != null && id.equals(season.id);
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}
