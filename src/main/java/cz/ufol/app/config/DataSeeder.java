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
                Univerzita uhk = univerzitaRepository.save(
                        Univerzita.builder()
                                .nazev("Univerzita Hradec Králové")
                                .zkratka("UHK")
                                .build());

                Univerzita upce = univerzitaRepository.save(
                        Univerzita.builder()
                                .nazev("Univerzita Pardubice")
                                .zkratka("UPCE")
                                .build());

                Univerzita muni = univerzitaRepository.save(
                        Univerzita.builder()
                                .nazev("Masarykova univerzita")
                                .zkratka("MUNI")
                                .build());

                tymRepository.save(Tym.builder()
                        .nazev("Hradečáci").univerzita(uhk).build());
                tymRepository.save(Tym.builder()
                        .nazev("Pardubáci").univerzita(upce).build());
                tymRepository.save(Tym.builder()
                        .nazev("Brňáci").univerzita(muni).build());
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
