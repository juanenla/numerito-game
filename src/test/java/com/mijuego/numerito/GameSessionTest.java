package com.mijuego.numerito;

import com.mijuego.numerito.exception.InvalidGuessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameSessionTest {

    // ========== Tests de generación del número secreto ==========

    @Test
    void testGameSessionCreatesValidSecretNumber() {
        GameSession session = new GameSession();
        String secret = session.getSecretNumber();

        assertEquals(4, secret.length(), "El número secreto debe tener 4 dígitos");
        assertNotEquals('0', secret.charAt(0), "El primer dígito no puede ser 0");
    }

    // ========== Tests de victoria (4 BIEN) ==========

    @Test
    void testPerfectGuessReturns4Bien() throws InvalidGuessException {
        GameSession session = new GameSession("1234");
        GuessResult result = session.guess("1234");

        assertEquals(4, result.bienCount(), "Debe haber 4 BIEN");
        assertEquals(0, result.regularCount(), "Debe haber 0 REGULAR");
        assertEquals(0, result.malCount(), "Debe haber 0 MAL");
        assertTrue(result.isWin(), "Debe ser victoria");
        assertEquals(1, result.attemptNumber(), "Debe ser el intento 1");
        assertTrue(session.isFinished(), "La partida debe estar terminada");
    }

    // ========== Tests con mezcla de B, R y M ==========

    @Test
    void testMixedResult_2Bien_1Regular_1Mal() throws InvalidGuessException {
        // Secreto: 1234
        // Intento: 1243 -> 1(B) 2(B) 4(R) 3(R) = 2B, 2R, 0M
        GameSession session = new GameSession("1234");
        GuessResult result = session.guess("1243");

        assertEquals(2, result.bienCount());
        assertEquals(2, result.regularCount());
        assertEquals(0, result.malCount());
        assertFalse(result.isWin());
    }

    @Test
    void testMixedResult_1Bien_2Regular_1Mal() throws InvalidGuessException {
        // Secreto: 1234
        // Intento: 1325 -> 1(B) 3(R) 2(R) 5(M) = 1B, 2R, 1M
        GameSession session = new GameSession("1234");
        GuessResult result = session.guess("1325");

        assertEquals(1, result.bienCount());
        assertEquals(2, result.regularCount());
        assertEquals(1, result.malCount());
        assertFalse(result.isWin());
    }

    @Test
    void testMixedResult_0Bien_4Regular() throws InvalidGuessException {
        // Secreto: 1234
        // Intento: 4321 -> todas las cifras están pero en posiciones incorrectas
        GameSession session = new GameSession("1234");
        GuessResult result = session.guess("4321");

        assertEquals(0, result.bienCount());
        assertEquals(4, result.regularCount());
        assertEquals(0, result.malCount());
        assertFalse(result.isWin());
    }

    // ========== Tests con todos MAL ==========

    @Test
    void testAllWrong_0Bien_0Regular_4Mal() throws InvalidGuessException {
        // Secreto: 1234
        // Intento: 5678 -> ninguna cifra coincide
        GameSession session = new GameSession("1234");
        GuessResult result = session.guess("5678");

        assertEquals(0, result.bienCount());
        assertEquals(0, result.regularCount());
        assertEquals(4, result.malCount());
        assertFalse(result.isWin());
    }

    // ========== Tests de validación: longitud incorrecta ==========

    @Test
    void testInvalidGuess_TooShort() {
        GameSession session = new GameSession("1234");

        InvalidGuessException exception = assertThrows(
            InvalidGuessException.class,
            () -> session.guess("123")
        );

        assertTrue(exception.getMessage().contains("exactamente 4 dígitos"));
    }

    @Test
    void testInvalidGuess_TooLong() {
        GameSession session = new GameSession("1234");

        InvalidGuessException exception = assertThrows(
            InvalidGuessException.class,
            () -> session.guess("12345")
        );

        assertTrue(exception.getMessage().contains("exactamente 4 dígitos"));
    }

    // ========== Tests de validación: primer dígito 0 ==========

    @Test
    void testInvalidGuess_StartsWithZero() {
        GameSession session = new GameSession("1234");

        InvalidGuessException exception = assertThrows(
            InvalidGuessException.class,
            () -> session.guess("0123")
        );

        assertTrue(exception.getMessage().contains("primer dígito no puede ser 0"));
    }

    // ========== Tests de validación: dígitos repetidos ==========

    @Test
    void testInvalidGuess_RepeatedDigits() {
        GameSession session = new GameSession("1234");

        InvalidGuessException exception = assertThrows(
            InvalidGuessException.class,
            () -> session.guess("1223")
        );

        assertTrue(exception.getMessage().contains("deben ser distintas"));
    }

    @Test
    void testInvalidGuess_AllSameDigit() {
        GameSession session = new GameSession("1234");

        InvalidGuessException exception = assertThrows(
            InvalidGuessException.class,
            () -> session.guess("1111")
        );

        assertTrue(exception.getMessage().contains("deben ser distintas"));
    }

    // ========== Tests de validación: caracteres no numéricos ==========

    @Test
    void testInvalidGuess_NonNumericCharacters() {
        GameSession session = new GameSession("1234");

        InvalidGuessException exception = assertThrows(
            InvalidGuessException.class,
            () -> session.guess("12a4")
        );

        assertTrue(exception.getMessage().contains("solo dígitos"));
    }

    @Test
    void testInvalidGuess_SpecialCharacters() {
        GameSession session = new GameSession("1234");

        InvalidGuessException exception = assertThrows(
            InvalidGuessException.class,
            () -> session.guess("12-4")
        );

        assertTrue(exception.getMessage().contains("solo dígitos"));
    }

    @Test
    void testInvalidGuess_Null() {
        GameSession session = new GameSession("1234");

        InvalidGuessException exception = assertThrows(
            InvalidGuessException.class,
            () -> session.guess(null)
        );

        assertTrue(exception.getMessage().contains("no puede ser null"));
    }

    // ========== Tests de contador de intentos ==========

    @Test
    void testAttemptsCounterIncreases() throws InvalidGuessException {
        GameSession session = new GameSession("1234");

        assertEquals(0, session.getAttempts(), "Inicialmente debe ser 0");

        session.guess("5678");
        assertEquals(1, session.getAttempts());

        session.guess("9012");
        assertEquals(2, session.getAttempts());

        session.guess("3456");
        assertEquals(3, session.getAttempts());
    }

    @Test
    void testAttemptNumberInResult() throws InvalidGuessException {
        GameSession session = new GameSession("1234");

        GuessResult result1 = session.guess("5678");
        assertEquals(1, result1.attemptNumber());

        GuessResult result2 = session.guess("9012");
        assertEquals(2, result2.attemptNumber());

        GuessResult result3 = session.guess("1234");
        assertEquals(3, result3.attemptNumber());
    }

    // ========== Tests de estado de partida terminada ==========

    @Test
    void testCannotGuessAfterWin() throws InvalidGuessException {
        GameSession session = new GameSession("1234");

        session.guess("1234"); // Victoria
        assertTrue(session.isFinished());

        InvalidGuessException exception = assertThrows(
            InvalidGuessException.class,
            () -> session.guess("5678")
        );

        assertTrue(exception.getMessage().contains("ya ha terminado"));
    }

    // ========== Tests de casos edge ==========

    @Test
    void testSecretWithZeroNotInFirstPosition() throws InvalidGuessException {
        GameSession session = new GameSession("1023");

        // Probar que puede manejar 0 en otras posiciones
        GuessResult result = session.guess("1023");
        assertTrue(result.isWin());
    }

    @Test
    void testMultipleGamesIndependent() throws InvalidGuessException {
        GameSession session1 = new GameSession("1234");
        GameSession session2 = new GameSession("5678");

        session1.guess("1234"); // Gana sesión 1

        assertTrue(session1.isFinished());
        assertFalse(session2.isFinished());

        // Sesión 2 puede seguir jugando
        GuessResult result = session2.guess("1234");
        assertFalse(result.isWin());
    }
}
