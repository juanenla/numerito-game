package com.mijuego.numerito.api.dto;

import com.mijuego.numerito.GuessResult;

/**
 * Respuesta al realizar un intento en el juego.
 */
public record GuessResponse(
    int bien,
    int regular,
    int mal,
    boolean win,
    int attemptNumber,
    boolean finished
) {
    /**
     * Crea una respuesta a partir de un GuessResult y el estado de la partida.
     */
    public static GuessResponse from(GuessResult result, boolean finished) {
        return new GuessResponse(
            result.bienCount(),
            result.regularCount(),
            result.malCount(),
            result.isWin(),
            result.attemptNumber(),
            finished
        );
    }
}
