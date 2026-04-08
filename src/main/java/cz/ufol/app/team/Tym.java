package cz.ufol.app.team;


import cz.ufol.app.university.Univerzita;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "tymy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tym {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank
    @Column(length = 100, nullable = false, unique = true)
    private String nazev;

    @Column(nullable = false)
    private boolean aktivni = true;

    @ManyToOne
    @JoinColumn(name = "univerzita_id", nullable = false)
    private Univerzita univerzita;
}
