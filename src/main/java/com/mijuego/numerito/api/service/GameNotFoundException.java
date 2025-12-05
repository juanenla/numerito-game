package com.mijuego.numerito.api.service;

/**
 * Excepción lanzada cuando se intenta acceder a una partida que no existe.
 */
public class GameNotFoundException extends Exception {

    public GameNotFoundException(String message) {
        super(message);
    }
}
