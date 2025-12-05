package com.mijuego.numerito;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación principal de Spring Boot para la API del juego Numerito.
 *
 * Esta aplicación expone endpoints REST para:
 * - Iniciar nuevas partidas
 * - Realizar intentos (guesses)
 * - Consultar el estado de las partidas
 */
@SpringBootApplication
public class NumeoritoGameApplication {

    public static void main(String[] args) {
        SpringApplication.run(NumeoritoGameApplication.class, args);
    }
}
