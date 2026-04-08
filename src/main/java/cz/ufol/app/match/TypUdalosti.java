package cz.ufol.app.match;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

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
}
