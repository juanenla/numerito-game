package com.mijuego.numerito.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record SaveScoreRequest(
        @NotBlank(message = "El nombre del jugador es requerido") String playerName,

        @Positive(message = "El número de intentos debe ser positivo") int attempts,

        String gameId) {
}
