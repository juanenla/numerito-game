package com.mijuego.numerito.api.model;

import java.time.Instant;

/**
 * Representa un score/puntuación en el ranking de Numerito
 *
 * Esta clase es un record inmutable que contiene la información
 * de una partida ganada que se guarda en Supabase.
 */
public record Score(
        String id,
        String playerName,
        int attempts,
        Instant createdAt,
        String gameId,
        long timeSeconds) {
    /**
     * Constructor para crear un nuevo score (sin id ni timestamp)
     */
    public Score(String playerName, int attempts, String gameId, long timeSeconds) {
        this(null, playerName, attempts, null, gameId, timeSeconds);
    }

    /**
     * Validación básica
     */
    public Score {
        if (attempts <= 0) {
            throw new IllegalArgumentException("Attempts must be greater than 0");
        }
        if (playerName == null || playerName.isBlank()) {
            playerName = "Anónimo";
        }
        // Truncar nombre si es muy largo
        if (playerName.length() > 50) {
            playerName = playerName.substring(0, 50);
        }
    }
}
