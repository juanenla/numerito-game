package com.mijuego.numerito;

import com.mijuego.numerito.exception.InvalidGuessException;

/**
 * Clase de ejemplo que demuestra el uso del juego Numerito.
 * NO es parte de la lógica core, solo para demostración.
 */
public class Example {

    public static void main(String[] args) {
        // Crear una nueva sesión de juego
        GameSession game = new GameSession();

        System.out.println("=== JUEGO NUMERITO ===");
        System.out.println("Adivina el número de 4 cifras distintas!");
        System.out.println("Nota: Esta es solo una demostración de la API\n");

        // Simular algunos intentos
        String[] intentos = {"1234", "5678", "9012", "3456"};

        for (String intento : intentos) {
            if (game.isFinished()) {
                System.out.println("¡Juego terminado!");
                break;
            }

            try {
                GuessResult result = game.guess(intento);

                System.out.println("Intento #" + result.attemptNumber() + ": " + intento);
                System.out.println("  → Bien (B): " + result.bienCount());
                System.out.println("  → Regular (R): " + result.regularCount());
                System.out.println("  → Mal (M): " + result.malCount());

                if (result.isWin()) {
                    System.out.println("  → ¡GANASTE! 🎉");
                }

                System.out.println();

            } catch (InvalidGuessException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        System.out.println("Total de intentos: " + game.getAttempts());

        // Demostración de validaciones
        System.out.println("\n=== DEMOSTRACIÓN DE VALIDACIONES ===");

        GameSession testGame = new GameSession();
        String[] intentosInvalidos = {
            "123",      // Muy corto
            "12345",    // Muy largo
            "0123",     // Empieza con 0
            "1223",     // Dígitos repetidos
            "12a4"      // Caracteres no numéricos
        };

        for (String intento : intentosInvalidos) {
            try {
                testGame.guess(intento);
                System.out.println(intento + " → Válido (¡no debería pasar!)");
            } catch (InvalidGuessException e) {
                System.out.println(intento + " → Inválido: " + e.getMessage());
            }
        }
    }
}
