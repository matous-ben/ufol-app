package cz.ufol.app.venue;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "mista_konani")
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
}
