package com.mijuego.numerito;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Generador de números secretos para el juego Numerito.
 * Genera números de 4 cifras con todas las cifras distintas.
 */
public class SecretNumberGenerator {

    private final Random random;

    /**
     * Constructor con generador de números aleatorios por defecto (SecureRandom)
     */
    public SecretNumberGenerator() {
        this.random = new SecureRandom();
    }

    /**
     * Constructor que permite inyectar un Random específico (útil para testing)
     */
    public SecretNumberGenerator(Random random) {
        this.random = random;
    }

    /**
     * Genera un número secreto de 4 cifras distintas.
     * La primera cifra está entre 1-9, las demás entre 0-9.
     *
     * @return String de 4 dígitos, ej: "1234"
     */
    public String generate() {
        List<Integer> digits = new ArrayList<>();

        // Primera cifra: 1-9 (no puede ser 0)
        int firstDigit = random.nextInt(9) + 1;
        digits.add(firstDigit);

        // Crear lista con dígitos disponibles (0-9 excepto el ya usado)
        List<Integer> available = new ArrayList<>();
        for (int i = 0; i <= 9; i++) {
            if (i != firstDigit) {
                available.add(i);
            }
        }

        // Mezclar y tomar 3 dígitos más
        Collections.shuffle(available, random);
        for (int i = 0; i < 3; i++) {
            digits.add(available.get(i));
        }

        // Convertir a String
        StringBuilder result = new StringBuilder();
        for (int digit : digits) {
            result.append(digit);
        }

        return result.toString();
    }
}
