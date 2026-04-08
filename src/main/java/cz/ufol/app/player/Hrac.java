package cz.ufol.app.player;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "hraci")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hrac {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank
    @Column(length = 50, nullable = false)
    private String jmeno;

    @NotBlank
    @Column(length = 50, nullable = false)
    private String prijmeni;

    @Column(name = "datum_narozeni")
    private LocalDate datumNarozeni;

    @Column(name = "foto_url", length = 255)
    private String fotoUrl;
}
