package com.mijuego.numerito.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * DTO para guardar un nuevo score
 */
public record SaveScoreRequest(
    @Size(max = 50, message = "Player name must not exceed 50 characters")
    String playerName,

    @Min(value = 1, message = "Attempts must be at least 1")
    int attempts,

    String gameId
) {
    /**
     * Normaliza el nombre del jugador
     */
    public String getPlayerNameOrDefault() {
        if (playerName == null || playerName.isBlank()) {
            return "Anónimo";
        }
        return playerName.trim();
    }
}
