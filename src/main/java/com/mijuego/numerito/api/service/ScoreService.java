package com.mijuego.numerito.api.service;

import com.mijuego.numerito.api.dto.SaveScoreRequest;
import com.mijuego.numerito.api.dto.ScoreResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class ScoreService {

        private final WebClient supabaseClient;

        public ScoreService(@Qualifier("supabaseWebClient") WebClient supabaseClient) {
                this.supabaseClient = supabaseClient;
        }

        public Mono<ScoreResponse> saveScore(SaveScoreRequest request) {
                // Map DTO fields to Supabase column names
                Map<String, Object> body = Map.of(
                                "player_name", request.playerName(),
                                "attempts", request.attempts(),
                                "time_seconds", request.timeSeconds(),
                                "game_id", request.gameId() != null ? request.gameId() : "");

                return supabaseClient.post()
                                .uri("/scores")
                                .header("Prefer", "return=representation")
                                .bodyValue(body)
                                .retrieve()
                                .onStatus(status -> status.isError(), response -> response.bodyToMono(String.class)
                                                .flatMap(errorBody -> {
                                                        System.err.println("❌ Supabase Error: " + response.statusCode()
                                                                        + " - " + errorBody);
                                                        return Mono.error(
                                                                        new RuntimeException("Supabase Error: "
                                                                                        + response.statusCode() + " "
                                                                                        + errorBody));
                                                }))
                                .bodyToFlux(ScoreResponse.class)
                                .next()
                                .doOnError(e -> System.err.println("❌ Error saving score: " + e.getMessage()));
        }

        public Flux<ScoreResponse> getTopScores(int limit) {
                return supabaseClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/scores")
                                                .queryParam("select", "*")
                                                .queryParam("order", "attempts.asc,created_at.desc")
                                                .queryParam("limit", limit)
                                                .build())
                                .retrieve()
                                .bodyToFlux(ScoreResponse.class);
        }
}
