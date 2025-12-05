package com.mijuego.numerito.api.dto;

/**
 * Respuesta al crear una nueva partida.
 */
public record GameCreatedResponse(
    String gameId,
    String message
) {
    public static GameCreatedResponse create(String gameId) {
        return new GameCreatedResponse(gameId, "Partida creada exitosamente");
    }
}
