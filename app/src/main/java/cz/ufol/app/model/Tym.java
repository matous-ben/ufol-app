package cz.ufol.app.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tymy")

public class Tym {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nazev;

    private String logo;

    @Column(name = "je_aktivni")
    private boolean jeAktivni = true;
}
