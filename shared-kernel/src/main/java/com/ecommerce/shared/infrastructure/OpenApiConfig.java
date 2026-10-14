package com.ecommerce.shared.infrastructure;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ecommerceOpenApi() {
        return new OpenAPI().info(new Info()
                .title("E-Commerce Monolith API")
                .description("Clean Architecture + DDD + CQRS (state-stored) — order fulfillment saga")
                .version("1.0.0"));
    }
}
