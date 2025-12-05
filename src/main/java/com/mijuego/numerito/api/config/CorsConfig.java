package com.mijuego.numerito.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * Configuración de CORS para permitir peticiones desde el frontend.
 *
 * En desarrollo, permite peticiones desde localhost:5173 (Vite default).
 * En producción, deberías especificar el origen exacto de tu frontend.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Permitir credenciales (cookies, authorization headers, etc.)
        config.setAllowCredentials(true);

        // Permitir estos orígenes (ajustar según necesidad)
        config.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",  // Vite dev server
            "http://localhost:3000",  // Alternativa (Create React App, etc.)
            "http://127.0.0.1:5173"   // Alternativa localhost
        ));

        // Permitir todos los headers
        config.addAllowedHeader("*");

        // Permitir estos métodos HTTP
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);

        return new CorsFilter(source);
    }
}
