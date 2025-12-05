package com.mijuego.numerito.api.dto;

import com.mijuego.numerito.GameSession;

/**
 * Respuesta con el estado actual de una partida.
 */
public record GameStateResponse(
    String gameId,
    int attempts,
    boolean finished
) {
    /**
     * Crea una respuesta a partir de una GameSession.
     */
    public static GameStateResponse from(String gameId, GameSession session) {
        return new GameStateResponse(
            gameId,
            session.getAttempts(),
            session.isFinished()
        );
    }
}
