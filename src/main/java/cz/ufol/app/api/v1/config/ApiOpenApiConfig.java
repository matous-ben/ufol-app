package cz.ufol.app.api.v1.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiOpenApiConfig {

    @Bean
    public OpenAPI apiV1OpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("UFoL API v1")
                        .version("v1")
                        .description("REST API v1 endpoints"));
    }
}
