package cz.ufol.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ufolOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("UFoL API")
                        .description("REST API dokumentace pro Univerzitní fotbalovou ligu")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Matouš Benedikt")
                                .email("admin@ufol.cz")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Lokální vývojové prostředí")
                ));
    }
}
