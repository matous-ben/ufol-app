package cz.ufol.app.season;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "rocniky")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rocnik {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank
    @Column(length = 50, nullable = false, unique = true)
    private String nazev;

    @NotNull
    @Column(name = "rok_od", nullable = false)
    private Integer rokOd;

    @NotNull
    @Column(name = "rok_do", nullable = false)
    private Integer rokDo;

    @Column(nullable = false)
    private boolean aktivni = true;
}
