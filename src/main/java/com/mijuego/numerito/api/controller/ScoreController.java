package com.mijuego.numerito.api.controller;

import com.mijuego.numerito.api.dto.SaveScoreRequest;
import com.mijuego.numerito.api.dto.ScoreResponse;
import com.mijuego.numerito.api.service.ScoreService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @PostMapping
    public Mono<ResponseEntity<ScoreResponse>> saveScore(@Valid @RequestBody SaveScoreRequest request) {
        return scoreService.saveScore(request)
                .map(score -> ResponseEntity.status(HttpStatus.CREATED).body(score));
    }

    @GetMapping("/top")
    public Mono<ResponseEntity<List<ScoreResponse>>> getTopScores(@RequestParam(defaultValue = "10") int limit) {
        return scoreService.getTopScores(limit)
                .collectList()
                .map(ResponseEntity::ok);
    }
}
