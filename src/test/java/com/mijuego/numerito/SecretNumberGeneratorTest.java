package com.mijuego.numerito;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SecretNumberGeneratorTest {

    @Test
    void testGenerateReturns4Digits() {
        SecretNumberGenerator generator = new SecretNumberGenerator();
        String secret = generator.generate();

        assertEquals(4, secret.length(), "El número secreto debe tener 4 dígitos");
    }

    @Test
    void testFirstDigitNotZero() {
        SecretNumberGenerator generator = new SecretNumberGenerator();

        // Generar múltiples números para asegurar que nunca empiece con 0
        for (int i = 0; i < 100; i++) {
            String secret = generator.generate();
            assertNotEquals('0', secret.charAt(0),
                "El primer dígito no puede ser 0");
        }
    }

    @Test
    void testAllDigitsAreUnique() {
        SecretNumberGenerator generator = new SecretNumberGenerator();

        // Generar múltiples números y verificar que todos tengan dígitos únicos
        for (int i = 0; i < 100; i++) {
            String secret = generator.generate();
            Set<Character> digits = new HashSet<>();

            for (char c : secret.toCharArray()) {
                assertTrue(digits.add(c),
                    "Todas las cifras deben ser distintas, número: " + secret);
            }

            assertEquals(4, digits.size(),
                "Debe haber exactamente 4 dígitos distintos");
        }
    }

    @Test
    void testAllCharactersAreDigits() {
        SecretNumberGenerator generator = new SecretNumberGenerator();

        for (int i = 0; i < 100; i++) {
            String secret = generator.generate();

            for (char c : secret.toCharArray()) {
                assertTrue(Character.isDigit(c),
                    "Todos los caracteres deben ser dígitos, encontrado: " + c);
            }
        }
    }

    @Test
    void testGenerateDifferentNumbers() {
        SecretNumberGenerator generator = new SecretNumberGenerator();
        Set<String> generated = new HashSet<>();

        // Generar 50 números y verificar que haya variedad
        for (int i = 0; i < 50; i++) {
            generated.add(generator.generate());
        }

        // Debería haber al menos 40 números diferentes de 50 intentos
        assertTrue(generated.size() >= 40,
            "El generador debe producir números variados");
    }
}
