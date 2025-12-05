package com.mijuego.numerito.exception;

/**
 * Excepción lanzada cuando un intento del usuario no cumple con las reglas del juego.
 */
public class InvalidGuessException extends Exception {

    public InvalidGuessException(String message) {
        super(message);
    }
}
