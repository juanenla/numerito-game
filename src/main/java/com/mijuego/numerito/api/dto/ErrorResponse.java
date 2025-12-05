package com.mijuego.numerito.api.dto;

import java.time.LocalDateTime;

/**
 * Respuesta estándar para errores de la API.
 */
public record ErrorResponse(
    String error,
    String message,
    LocalDateTime timestamp
) {
    public static ErrorResponse of(String error, String message) {
        return new ErrorResponse(error, message, LocalDateTime.now());
    }
}
