package cz.ufol.app.event;

import cz.ufol.app.match.UcastVZapase;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "udalosti_zapasu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UdalostZapasu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ucast_v_zapase_id", nullable = false)
    private UcastVZapase ucastVZapase;

    @ManyToOne
    @JoinColumn(name = "typ_udalosti_id", nullable = false)
    private TypUdalosti typUdalosti;

    private Integer minuta;
}
