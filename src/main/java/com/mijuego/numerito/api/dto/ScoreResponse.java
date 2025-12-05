package com.mijuego.numerito.api.dto;

import com.mijuego.numerito.api.model.Score;
import java.time.Instant;

/**
 * DTO para devolver información de un score
 */
public record ScoreResponse(
    String id,
    String playerName,
    int attempts,
    Instant createdAt,
    String gameId
) {
    /**
     * Crea un ScoreResponse desde un Score
     */
    public static ScoreResponse from(Score score) {
        return new ScoreResponse(
            score.id(),
            score.playerName(),
            score.attempts(),
            score.createdAt(),
            score.gameId()
        );
    }
}
