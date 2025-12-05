package com.mijuego.numerito.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mijuego.numerito.api.dto.GuessRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para los endpoints de la API.
 */
@SpringBootTest
@AutoConfigureMockMvc
class GameControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateGame_ReturnsGameId() throws Exception {
        mockMvc.perform(post("/api/game")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.gameId", notNullValue()))
            .andExpect(jsonPath("$.message", is("Partida creada exitosamente")));
    }

    @Test
    void testMakeGuess_WithValidGameAndGuess_ReturnsResult() throws Exception {
        // Primero crear una partida
        MvcResult createResult = mockMvc.perform(post("/api/game")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();

        String response = createResult.getResponse().getContentAsString();
        String gameId = objectMapper.readTree(response).get("gameId").asText();

        // Hacer un intento
        GuessRequest guessRequest = new GuessRequest("1234");

        mockMvc.perform(post("/api/game/{gameId}/guess", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(guessRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.bien", notNullValue()))
            .andExpect(jsonPath("$.regular", notNullValue()))
            .andExpect(jsonPath("$.mal", notNullValue()))
            .andExpect(jsonPath("$.win", notNullValue()))
            .andExpect(jsonPath("$.attemptNumber", is(1)))
            .andExpect(jsonPath("$.finished", notNullValue()));
    }

    @Test
    void testMakeGuess_WithInvalidGameId_Returns404() throws Exception {
        GuessRequest guessRequest = new GuessRequest("1234");

        mockMvc.perform(post("/api/game/{gameId}/guess", "invalid-game-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(guessRequest)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error", is("GAME_NOT_FOUND")))
            .andExpect(jsonPath("$.message", containsString("no encontrada")));
    }

    @Test
    void testMakeGuess_WithInvalidGuess_Returns400() throws Exception {
        // Crear partida
        MvcResult createResult = mockMvc.perform(post("/api/game")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();

        String response = createResult.getResponse().getContentAsString();
        String gameId = objectMapper.readTree(response).get("gameId").asText();

        // Intento con dígitos repetidos
        GuessRequest invalidGuess = new GuessRequest("1122");

        mockMvc.perform(post("/api/game/{gameId}/guess", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidGuess)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", is("INVALID_GUESS")))
            .andExpect(jsonPath("$.message", notNullValue()));
    }

    @Test
    void testMakeGuess_StartingWithZero_Returns400() throws Exception {
        // Crear partida
        MvcResult createResult = mockMvc.perform(post("/api/game")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();

        String response = createResult.getResponse().getContentAsString();
        String gameId = objectMapper.readTree(response).get("gameId").asText();

        // Intento que empieza con 0
        GuessRequest invalidGuess = new GuessRequest("0123");

        mockMvc.perform(post("/api/game/{gameId}/guess", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidGuess)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetGameState_WithValidGameId_ReturnsState() throws Exception {
        // Crear partida
        MvcResult createResult = mockMvc.perform(post("/api/game")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();

        String response = createResult.getResponse().getContentAsString();
        String gameId = objectMapper.readTree(response).get("gameId").asText();

        // Consultar estado
        mockMvc.perform(get("/api/game/{gameId}", gameId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.gameId", is(gameId)))
            .andExpect(jsonPath("$.attempts", is(0)))
            .andExpect(jsonPath("$.finished", is(false)));
    }

    @Test
    void testGetGameState_WithInvalidGameId_Returns404() throws Exception {
        mockMvc.perform(get("/api/game/{gameId}", "non-existent-id")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error", is("GAME_NOT_FOUND")));
    }

    @Test
    void testGameFlow_MultipleGuesses_UpdatesAttempts() throws Exception {
        // Crear partida
        MvcResult createResult = mockMvc.perform(post("/api/game")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();

        String response = createResult.getResponse().getContentAsString();
        String gameId = objectMapper.readTree(response).get("gameId").asText();

        // Hacer múltiples intentos
        mockMvc.perform(post("/api/game/{gameId}/guess", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new GuessRequest("1234"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.attemptNumber", is(1)));

        mockMvc.perform(post("/api/game/{gameId}/guess", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new GuessRequest("5678"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.attemptNumber", is(2)));

        // Verificar estado
        mockMvc.perform(get("/api/game/{gameId}", gameId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.attempts", is(2)));
    }
}
