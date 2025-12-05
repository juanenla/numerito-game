package com.mijuego.numerito.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mijuego.numerito.api.dto.SaveScoreRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para el ScoreController.
 *
 * NOTA: Estos tests hacen llamadas reales a Supabase si está configurado.
 * Si prefieres mockear Supabase, usa @MockBean para ScoreService.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ScoreControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSaveScore_WithValidData_Returns201() throws Exception {
        SaveScoreRequest request = new SaveScoreRequest(
            "TestPlayer",
            5,
            "test-game-123"
        );

        mockMvc.perform(post("/api/scores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message", is("Score guardado exitosamente")))
            .andExpect(jsonPath("$.scoreId", notNullValue()));
    }

    @Test
    void testSaveScore_WithInvalidAttempts_Returns400() throws Exception {
        SaveScoreRequest request = new SaveScoreRequest(
            "TestPlayer",
            -1,  // Attempts inválidos
            "test-game-123"
        );

        mockMvc.perform(post("/api/scores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", notNullValue()));
    }

    @Test
    void testSaveScore_WithEmptyPlayerName_Returns400() throws Exception {
        SaveScoreRequest request = new SaveScoreRequest(
            "",  // Nombre vacío
            5,
            "test-game-123"
        );

        mockMvc.perform(post("/api/scores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetTopScores_ReturnsListOfScores() throws Exception {
        mockMvc.perform(get("/api/scores/top")
                .param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", isA(java.util.List.class)));
    }

    @Test
    void testGetTopScores_WithCustomLimit_ReturnsCorrectAmount() throws Exception {
        mockMvc.perform(get("/api/scores/top")
                .param("limit", "5")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", isA(java.util.List.class)))
            .andExpect(jsonPath("$.length()", lessThanOrEqualTo(5)));
    }

    @Test
    void testGetTopScores_DefaultLimit_Returns10OrLess() throws Exception {
        mockMvc.perform(get("/api/scores/top")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", isA(java.util.List.class)))
            .andExpect(jsonPath("$.length()", lessThanOrEqualTo(10)));
    }

    @Test
    void testScoreResponseStructure() throws Exception {
        // Guardar un score primero
        SaveScoreRequest saveRequest = new SaveScoreRequest(
            "StructureTest",
            3,
            "test-structure-123"
        );

        mockMvc.perform(post("/api/scores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(saveRequest)))
            .andExpect(status().isCreated());

        // Obtener el top scores y verificar estructura
        mockMvc.perform(get("/api/scores/top")
                .param("limit", "1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", notNullValue()))
            .andExpect(jsonPath("$[0].playerName", notNullValue()))
            .andExpect(jsonPath("$[0].attempts", isA(Number.class)))
            .andExpect(jsonPath("$[0].createdAt", notNullValue()));
    }
}
