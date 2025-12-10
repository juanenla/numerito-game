package com.mijuego.numerito.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ScoreResponse(
        Long id,

        @JsonAlias("player_name") @JsonProperty("playerName") String playerName,

        int attempts,

        @JsonAlias("game_id") @JsonProperty("gameId") String gameId,

        @JsonAlias("created_at") @JsonProperty("createdAt") String createdAt) {
}
