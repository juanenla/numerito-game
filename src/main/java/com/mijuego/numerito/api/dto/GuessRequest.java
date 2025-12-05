package com.mijuego.numerito.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Request para realizar un intento en el juego.
 */
public record GuessRequest(
    @NotBlank(message = "El intento no puede estar vacío")
    @Pattern(regexp = "^[1-9][0-9]{3}$", message = "El intento debe ser un número de 4 dígitos que no empiece con 0")
    String guess
) {
}
