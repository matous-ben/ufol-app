package cz.ufol.app.config;


import cz.ufol.app.event.TypUdalosti;
import cz.ufol.app.event.TypUdalostiRepository;
import cz.ufol.app.match.Zapas;
import cz.ufol.app.match.ZapasRepository;
import cz.ufol.app.season.Rocnik;
import cz.ufol.app.season.RocnikRepository;
import cz.ufol.app.team.Tym;
import cz.ufol.app.team.TymRepository;
import cz.ufol.app.university.Univerzita;
import cz.ufol.app.university.UniverzitaRepository;
import cz.ufol.app.user.User;
import cz.ufol.app.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(
            UserRepository userRepository,
            UniverzitaRepository univerzitaRepository,
            TymRepository tymRepository,
            RocnikRepository rocnikRepository,
            TypUdalostiRepository typUdalostiRepository,
            PasswordEncoder passwordEncoder,
            ZapasRepository zapasRepository) {

        return args -> {

            // Admin account
            if (userRepository.findByEmail("admin@ufol.cz").isEmpty()) {
                userRepository.save(User.builder()
                        .email("admin@ufol.cz")
                        .password(passwordEncoder.encode("Admin1234"))
                        .build());
            }

            // Event types
            seedTypUdalosti(typUdalostiRepository, "Gól", "GOL");
            seedTypUdalosti(typUdalostiRepository, "Žlutá karta", "ZK");
            seedTypUdalosti(typUdalostiRepository, "Červená karta", "CK");
            seedTypUdalosti(typUdalostiRepository, "Asistence", "ASIST");

            // Universities and teams
            if (univerzitaRepository.count() == 0) {
                Object[][] data = {
                        {"Univerzita Hradec Králové", "UHK", "uhk.svg"},
                        {"Univerzita Pardubice", "UPCE", "upce.svg"},
                        {"Masarykova univerzita", "MUNI", "muni.svg"},
                        {"České vysoké učení technické v Praze", "ČVUT", "cvut.svg"},
                        {"Česká zemědělská univerzita v Praze", "ČZU", "czu.svg"},
                        {"Jihočeská univerzita v Českých Budějovicích", "JU", "ju.svg"},
                        {"Ostravská univerzita", "OSU", "osu.svg"},
                        {"Technická univerzita v Liberci", "TUL", "tul.svg"},
                        {"Univerzita J. E. Purkyně v Ústí nad Labem", "UJEP", "ujep.svg"},
                        {"Univerzita Karlova", "UK", "uk.svg"},
                        {"Univerzita Palackého v Olomouci", "UPOL", "upol.svg"},
                        {"Univerzita Tomáše Bati ve Zlíně", "UTB", "utb.svg"},
                        {"VŠB – Technická univerzita Ostrava", "VŠB", "vsb.webp"},
                        {"Vysoká škola ekonomická v Praze", "VŠE", "vse.svg"},
                        {"Vysoká škola technická a ekonomická v ČB", "VŠTE", "vste.webp"},
                        {"Západočeská univerzita v Plzni", "ZČU", "zcu.svg"}
                };

                for (Object[] row : data) {
                    univerzitaRepository.save(Univerzita.builder()
                            .nazev((String) row[0])
                            .zkratka((String) row[1])
                            .logoFile((String) row[2])
                            .build());
                }
            }

            // Active season
            if (rocnikRepository.count() == 0) {
                rocnikRepository.save(Rocnik.builder()
                        .nazev("2025/2026")
                        .rokOd(2025)
                        .rokDo(2026)
                        .aktivni(true)
                        .build());
            }


        };
    }

    private void seedTypUdalosti(TypUdalostiRepository repo,
                                 String nazev, String kod) {
        if (repo.findByKod(kod).isEmpty()) {
            repo.save(TypUdalosti.builder()
                    .nazev(nazev).kod(kod).build());
        }
    }
}
