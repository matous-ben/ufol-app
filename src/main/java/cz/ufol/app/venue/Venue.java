package cz.ufol.app.venue;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.Hibernate;

@Entity
@Table(
        name = "venues",
        indexes = {
                @Index(name = "idx_venues_name", columnList = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "street", length = 100)
    private String street;

    @NotBlank
    @Column(name = "city", length = 100, nullable = false)
    private String city;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Venue that = (Venue) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}
