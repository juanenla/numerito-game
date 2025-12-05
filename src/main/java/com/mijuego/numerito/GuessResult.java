package com.mijuego.numerito;

/**
 * Resultado de la evaluación de un intento del jugador.
 *
 * @param bienCount    Cantidad de cifras correctas en posición correcta (B)
 * @param regularCount Cantidad de cifras correctas en posición incorrecta (R)
 * @param malCount     Cantidad de cifras que no están en el número secreto (M)
 * @param isWin        true si el jugador adivinó el número completo
 * @param attemptNumber Número del intento actual
 */
public record GuessResult(
    int bienCount,
    int regularCount,
    int malCount,
    boolean isWin,
    int attemptNumber
) {

    /**
     * Constructor que valida que la suma de B + R + M = 4
     */
    public GuessResult {
        if (bienCount + regularCount + malCount != 4) {
            throw new IllegalArgumentException(
                "La suma de bien + regular + mal debe ser 4"
            );
        }
        if (attemptNumber < 1) {
            throw new IllegalArgumentException(
                "El número de intento debe ser mayor a 0"
            );
        }
    }

    /**
     * Crea un resultado victorioso (4 B)
     */
    public static GuessResult win(int attemptNumber) {
        return new GuessResult(4, 0, 0, true, attemptNumber);
    }
}
