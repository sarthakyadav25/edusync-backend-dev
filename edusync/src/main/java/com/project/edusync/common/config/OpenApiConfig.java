package com.project.edusync.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Dine Cognizant APIs",
                version = "1.0",
                description = "Documentation for Dine Cognizant API"
        ),
        // This applies the security requirement globally to all endpoints
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        // A name for the security scheme
        name = "bearerAuth",
        // A brief description
        description = "JWT auth description",
        // The scheme to use
        scheme = "bearer",
        // The type of security scheme
        type = SecuritySchemeType.HTTP,
        // The format of the bearer token
        bearerFormat = "JWT",
        // Where the security scheme is located
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
    // This class is for configuration purposes only.
    // No methods are needed inside.
}
