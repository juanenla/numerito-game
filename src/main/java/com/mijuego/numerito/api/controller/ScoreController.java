package com.mijuego.numerito.api.controller;

import com.mijuego.numerito.api.dto.SaveScoreRequest;
import com.mijuego.numerito.api.dto.ScoreResponse;
import com.mijuego.numerito.api.model.Score;
import com.mijuego.numerito.api.service.ScoreService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para manejar scores/puntuaciones
 *
 * Endpoints:
 * - POST /api/scores - Guardar un nuevo score
 * - GET /api/scores/top?limit=N - Obtener los mejores N scores
 */
@RestController
@RequestMapping("/api/scores")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ScoreController {

    private static final Logger log = LoggerFactory.getLogger(ScoreController.class);

    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    /**
     * Guarda un nuevo score en el ranking
     *
     * POST /api/scores
     * Body: { "playerName": "Juan", "attempts": 5, "gameId": "abc-123" }
     *
     * @param request Datos del score a guardar
     * @return El score guardado con id y timestamp
     */
    @PostMapping
    public ResponseEntity<ScoreResponse> saveScore(@Valid @RequestBody SaveScoreRequest request) {
        log.info("POST /api/scores - Saving score for player: {}, attempts: {}",
                request.getPlayerNameOrDefault(), request.attempts());

        try {
            // Crear el Score con los datos validados
            Score score = new Score(
                    request.getPlayerNameOrDefault(),
                    request.attempts(),
                    request.gameId()
            );

            // Guardar en Supabase
            Score savedScore = scoreService.saveScore(score);

            // Devolver la respuesta
            ScoreResponse response = ScoreResponse.from(savedScore);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.error("Invalid score data: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error saving score", e);
            throw new RuntimeException("Failed to save score", e);
        }
    }

    /**
     * Obtiene los mejores scores del ranking
     *
     * GET /api/scores/top?limit=10
     *
     * @param limit Número de scores a devolver (default: 10, max: 100)
     * @return Lista de scores ordenados por intentos (ascendente)
     */
    @GetMapping("/top")
    public ResponseEntity<List<ScoreResponse>> getTopScores(
            @RequestParam(defaultValue = "10") int limit) {

        // Validar y limitar el parámetro
        if (limit < 1) limit = 10;
        if (limit > 100) limit = 100;

        log.info("GET /api/scores/top?limit={}", limit);

        try {
            List<Score> scores = scoreService.getTopScores(limit);

            List<ScoreResponse> response = scores.stream()
                    .map(ScoreResponse::from)
                    .toList();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching top scores", e);
            throw new RuntimeException("Failed to fetch scores", e);
        }
    }
}
