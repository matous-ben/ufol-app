package cz.ufol.app.university;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

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

    @Column(name = "logo_url", length = 255)
    private String logoUrl;
}
