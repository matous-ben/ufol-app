package cz.ufol.app.config;


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
            PasswordEncoder passwordEncoder) {

        return args -> {

            // Keep a guaranteed admin account for local development.
            if (userRepository.findByEmail("admin@ufol.cz").isEmpty()) {
                userRepository.save(User.builder()
                        .email("admin@ufol.cz")
                        .password(passwordEncoder.encode("Admin1234"))
                        .build());
            }
        };
    }
}
