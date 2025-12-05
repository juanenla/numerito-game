package com.mijuego.numerito.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuración para conectar con Supabase REST API
 *
 * Usa WebClient para hacer peticiones HTTP a Supabase.
 * La autenticación se hace mediante el header Authorization con la service_role key.
 */
@Configuration
public class SupabaseConfig {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    /**
     * Bean de WebClient configurado para Supabase
     *
     * Este WebClient ya incluye:
     * - Base URL de Supabase
     * - Headers de autenticación (apikey y Authorization)
     * - Header de preferencia para devolver la representación completa
     */
    @Bean
    public WebClient supabaseWebClient() {
        return WebClient.builder()
                .baseUrl(supabaseUrl + "/rest/v1")
                .defaultHeader("apikey", supabaseKey)
                .defaultHeader("Authorization", "Bearer " + supabaseKey)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Prefer", "return=representation")
                .build();
    }
}
