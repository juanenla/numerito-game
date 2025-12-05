package com.mijuego.numerito.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Configuración de CORS para permitir peticiones desde el frontend.
 *
 * En desarrollo, permite peticiones desde localhost:5173 (Vite default).
 * En producción, permite el origen configurado en FRONTEND_URL.
 */
@Configuration
public class CorsConfig {

    @Value("${frontend.url:}")
    private String frontendUrl;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Permitir credenciales (cookies, authorization headers, etc.)
        config.setAllowCredentials(true);

        // Lista de orígenes permitidos
        List<String> allowedOrigins = new ArrayList<>(Arrays.asList(
            "http://localhost:5173",  // Vite dev server
            "http://localhost:3000",  // Alternativa (Create React App, etc.)
            "http://127.0.0.1:5173"   // Alternativa localhost
        ));

        // Agregar el dominio de producción si está configurado
        if (frontendUrl != null && !frontendUrl.isEmpty()) {
            allowedOrigins.add(frontendUrl);
            System.out.println("CORS: Permitiendo origen de producción: " + frontendUrl);
        }

        config.setAllowedOrigins(allowedOrigins);

        // Permitir todos los headers
        config.addAllowedHeader("*");

        // Permitir estos métodos HTTP
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);

        return new CorsFilter(source);
    }
}
