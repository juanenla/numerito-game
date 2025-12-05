package com.mijuego.numerito;

import com.mijuego.numerito.exception.InvalidGuessException;

import java.util.HashSet;
import java.util.Set;

/**
 * Representa una sesión de juego de Numerito.
 * Mantiene el estado de una partida: número secreto, intentos realizados, etc.
 */
public class GameSession {

    private final String secretNumber;
    private int attempts;
    private boolean finished;

    /**
     * Crea una nueva sesión con un generador de números por defecto
     */
    public GameSession() {
        this(new SecretNumberGenerator());
    }

    /**
     * Crea una nueva sesión con un generador específico (útil para testing)
     */
    public GameSession(SecretNumberGenerator generator) {
        this.secretNumber = generator.generate();
        this.attempts = 0;
        this.finished = false;
    }

    /**
     * Constructor para testing que permite especificar el número secreto
     */
    GameSession(String secretNumber) {
        this.secretNumber = secretNumber;
        this.attempts = 0;
        this.finished = false;
    }

    /**
     * Evalúa un intento del jugador.
     *
     * @param guess El intento del jugador como String de 4 dígitos
     * @return GuessResult con el resultado de la evaluación
     * @throws InvalidGuessException si el intento no cumple las reglas
     */
    public GuessResult guess(String guess) throws InvalidGuessException {
        if (finished) {
            throw new InvalidGuessException("La partida ya ha terminado");
        }

        validateGuess(guess);
        attempts++;

        int bien = 0;
        int regular = 0;

        // Array para marcar qué posiciones del secreto ya fueron usadas
        boolean[] secretUsed = new boolean[4];
        boolean[] guessUsed = new boolean[4];

        // Primera pasada: contar BIEN (posición y valor correctos)
        for (int i = 0; i < 4; i++) {
            if (guess.charAt(i) == secretNumber.charAt(i)) {
                bien++;
                secretUsed[i] = true;
                guessUsed[i] = true;
            }
        }

        // Segunda pasada: contar REGULAR (valor correcto, posición incorrecta)
        for (int i = 0; i < 4; i++) {
            if (!guessUsed[i]) {
                for (int j = 0; j < 4; j++) {
                    if (!secretUsed[j] && guess.charAt(i) == secretNumber.charAt(j)) {
                        regular++;
                        secretUsed[j] = true;
                        break;
                    }
                }
            }
        }

        int mal = 4 - bien - regular;
        boolean isWin = bien == 4;

        if (isWin) {
            finished = true;
        }

        return new GuessResult(bien, regular, mal, isWin, attempts);
    }

    /**
     * Valida que un intento cumpla todas las reglas del juego.
     */
    private void validateGuess(String guess) throws InvalidGuessException {
        if (guess == null) {
            throw new InvalidGuessException("El intento no puede ser null");
        }

        if (guess.length() != 4) {
            throw new InvalidGuessException(
                "El intento debe tener exactamente 4 dígitos, recibido: " + guess.length()
            );
        }

        // Validar que todos son dígitos
        for (int i = 0; i < 4; i++) {
            char c = guess.charAt(i);
            if (!Character.isDigit(c)) {
                throw new InvalidGuessException(
                    "El intento debe contener solo dígitos, carácter inválido: " + c
                );
            }
        }

        // Validar que el primer dígito no sea 0
        if (guess.charAt(0) == '0') {
            throw new InvalidGuessException(
                "El primer dígito no puede ser 0"
            );
        }

        // Validar que todas las cifras sean distintas
        Set<Character> uniqueDigits = new HashSet<>();
        for (int i = 0; i < 4; i++) {
            if (!uniqueDigits.add(guess.charAt(i))) {
                throw new InvalidGuessException(
                    "Todas las cifras deben ser distintas, dígito repetido: " + guess.charAt(i)
                );
            }
        }
    }

    /**
     * Retorna true si la partida ha terminado (el jugador ganó)
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Retorna el número de intentos realizados
     */
    public int getAttempts() {
        return attempts;
    }

    /**
     * Método para debugging/testing - expone el número secreto
     * NO debería usarse en producción
     */
    String getSecretNumber() {
        return secretNumber;
    }
}
