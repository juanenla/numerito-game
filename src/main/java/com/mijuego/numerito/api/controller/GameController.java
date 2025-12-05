package com.mijuego.numerito.api.controller;

import com.mijuego.numerito.GameSession;
import com.mijuego.numerito.GuessResult;
import com.mijuego.numerito.api.dto.GameCreatedResponse;
import com.mijuego.numerito.api.dto.GameStateResponse;
import com.mijuego.numerito.api.dto.GuessRequest;
import com.mijuego.numerito.api.dto.GuessResponse;
import com.mijuego.numerito.api.service.GameNotFoundException;
import com.mijuego.numerito.api.service.GameService;
import com.mijuego.numerito.exception.InvalidGuessException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para el juego Numerito.
 *
 * Expone endpoints para:
 * - POST /api/game - Crear nueva partida
 * - POST /api/game/{gameId}/guess - Realizar un intento
 * - GET /api/game/{gameId} - Consultar estado de partida
 */
@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Crea una nueva partida.
     *
     * Ejemplo de respuesta:
     * {
     *   "gameId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
     *   "message": "Partida creada exitosamente"
     * }
     *
     * @return ResponseEntity con el gameId generado
     */
    @PostMapping
    public ResponseEntity<GameCreatedResponse> createGame() {
        String gameId = gameService.createGame();
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(GameCreatedResponse.create(gameId));
    }

    /**
     * Realiza un intento en una partida existente.
     *
     * Ejemplo de request:
     * {
     *   "guess": "1234"
     * }
     *
     * Ejemplo de respuesta:
     * {
     *   "bien": 1,
     *   "regular": 2,
     *   "mal": 1,
     *   "win": false,
     *   "attemptNumber": 3,
     *   "finished": false
     * }
     *
     * @param gameId identificador de la partida
     * @param request request con el intento
     * @return ResponseEntity con el resultado del intento
     * @throws GameNotFoundException si la partida no existe
     * @throws InvalidGuessException si el intento no es válido
     */
    @PostMapping("/{gameId}/guess")
    public ResponseEntity<GuessResponse> makeGuess(
            @PathVariable String gameId,
            @Valid @RequestBody GuessRequest request)
            throws GameNotFoundException, InvalidGuessException {

        GuessResult result = gameService.makeGuess(gameId, request.guess());
        GameSession session = gameService.getGameState(gameId);

        GuessResponse response = GuessResponse.from(result, session.isFinished());

        return ResponseEntity.ok(response);
    }

    /**
     * Consulta el estado de una partida.
     *
     * Ejemplo de respuesta:
     * {
     *   "gameId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
     *   "attempts": 5,
     *   "finished": false
     * }
     *
     * @param gameId identificador de la partida
     * @return ResponseEntity con el estado de la partida
     * @throws GameNotFoundException si la partida no existe
     */
    @GetMapping("/{gameId}")
    public ResponseEntity<GameStateResponse> getGameState(@PathVariable String gameId)
            throws GameNotFoundException {

        GameSession session = gameService.getGameState(gameId);
        GameStateResponse response = GameStateResponse.from(gameId, session);

        return ResponseEntity.ok(response);
    }
}
