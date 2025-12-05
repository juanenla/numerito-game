package com.mijuego.numerito.api.service;

import com.mijuego.numerito.api.model.Score;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Servicio para manejar scores con Supabase
 *
 * Este servicio usa WebClient para comunicarse con la REST API de Supabase.
 * Supabase expone automáticamente una API REST sobre la base de datos Postgres.
 *
 * Operaciones:
 * - Guardar un nuevo score (INSERT)
 * - Obtener los mejores N scores ordenados por intentos (SELECT con ORDER BY y LIMIT)
 */
@Service
public class ScoreService {

    private static final Logger log = LoggerFactory.getLogger(ScoreService.class);

    private final WebClient supabaseWebClient;

    public ScoreService(WebClient supabaseWebClient) {
        this.supabaseWebClient = supabaseWebClient;
    }

    /**
     * Guarda un nuevo score en Supabase
     *
     * @param score El score a guardar
     * @return El score guardado con id y timestamp generados por Supabase
     * @throws RuntimeException si hay error al guardar
     */
    public Score saveScore(Score score) {
        log.info("Saving score for player '{}' with {} attempts", score.playerName(), score.attempts());

        try {
            // Preparar el payload para Supabase
            Map<String, Object> payload = Map.of(
                "player_name", score.playerName(),
                "attempts", score.attempts(),
                "game_id", score.gameId() != null ? score.gameId() : ""
            );

            // POST a /scores con el header Prefer=return=representation
            // para que Supabase devuelva el registro insertado
            Map<String, Object> response = supabaseWebClient.post()
                    .uri("/scores")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) {
                throw new RuntimeException("No response from Supabase");
            }

            // Supabase devuelve un array con un solo elemento
            List<Map<String, Object>> results;
            if (response instanceof List) {
                results = (List<Map<String, Object>>) response;
            } else {
                results = List.of(response);
            }

            if (results.isEmpty()) {
                throw new RuntimeException("Empty response from Supabase");
            }

            Map<String, Object> savedScore = results.get(0);

            log.info("Score saved successfully with id: {}", savedScore.get("id"));

            return new Score(
                (String) savedScore.get("id"),
                (String) savedScore.get("player_name"),
                (Integer) savedScore.get("attempts"),
                Instant.parse((String) savedScore.get("created_at")),
                (String) savedScore.get("game_id")
            );

        } catch (WebClientResponseException e) {
            log.error("Error saving score to Supabase: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to save score: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error saving score", e);
            throw new RuntimeException("Failed to save score: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene los mejores N scores ordenados por intentos (ascendente) y fecha (ascendente)
     *
     * @param limit Número máximo de scores a devolver
     * @return Lista de scores
     */
    public List<Score> getTopScores(int limit) {
        log.info("Fetching top {} scores", limit);

        try {
            // GET a /scores con order y limit
            // order=attempts.asc,created_at.asc para ordenar por intentos y luego por fecha
            List<Map<String, Object>> response = supabaseWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scores")
                            .queryParam("order", "attempts.asc,created_at.asc")
                            .queryParam("limit", limit)
                            .build())
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();

            if (response == null) {
                log.warn("No response from Supabase, returning empty list");
                return List.of();
            }

            log.info("Fetched {} scores from Supabase", response.size());

            return response.stream()
                    .map(this::mapToScore)
                    .toList();

        } catch (WebClientResponseException e) {
            log.error("Error fetching scores from Supabase: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to fetch scores: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error fetching scores", e);
            throw new RuntimeException("Failed to fetch scores: " + e.getMessage(), e);
        }
    }

    /**
     * Mapea un Map de Supabase a un objeto Score
     */
    private Score mapToScore(Map<String, Object> map) {
        return new Score(
            (String) map.get("id"),
            (String) map.get("player_name"),
            (Integer) map.get("attempts"),
            Instant.parse((String) map.get("created_at")),
            (String) map.get("game_id")
        );
    }
}
