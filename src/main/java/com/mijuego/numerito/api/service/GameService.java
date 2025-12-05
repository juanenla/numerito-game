package com.mijuego.numerito.api.service;

import com.mijuego.numerito.GameSession;
import com.mijuego.numerito.GuessResult;
import com.mijuego.numerito.exception.InvalidGuessException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio que gestiona las sesiones de juego en memoria.
 *
 * Mantiene un mapa de gameId -> GameSession para permitir múltiples partidas simultáneas.
 * Las sesiones se mantienen en memoria (sin persistencia por ahora).
 */
@Service
public class GameService {

    // Mapa thread-safe para almacenar sesiones activas
    private final Map<String, GameSession> activeSessions = new ConcurrentHashMap<>();

    /**
     * Crea una nueva partida y retorna su ID único.
     *
     * @return gameId único generado para esta partida
     */
    public String createGame() {
        String gameId = UUID.randomUUID().toString();
        GameSession session = new GameSession();
        activeSessions.put(gameId, session);
        return gameId;
    }

    /**
     * Realiza un intento en una partida existente.
     *
     * @param gameId el ID de la partida
     * @param guess el intento del jugador
     * @return resultado del intento
     * @throws GameNotFoundException si la partida no existe
     * @throws InvalidGuessException si el intento no es válido
     */
    public GuessResult makeGuess(String gameId, String guess)
            throws GameNotFoundException, InvalidGuessException {

        GameSession session = activeSessions.get(gameId);
        if (session == null) {
            throw new GameNotFoundException("Partida con ID " + gameId + " no encontrada");
        }

        return session.guess(guess);
    }

    /**
     * Obtiene el estado de una partida.
     *
     * @param gameId el ID de la partida
     * @return la sesión de juego
     * @throws GameNotFoundException si la partida no existe
     */
    public GameSession getGameState(String gameId) throws GameNotFoundException {
        GameSession session = activeSessions.get(gameId);
        if (session == null) {
            throw new GameNotFoundException("Partida con ID " + gameId + " no encontrada");
        }
        return session;
    }

    /**
     * Obtiene una sesión de juego si existe.
     *
     * @param gameId el ID de la partida
     * @return Optional con la sesión si existe
     */
    public Optional<GameSession> findGame(String gameId) {
        return Optional.ofNullable(activeSessions.get(gameId));
    }

    /**
     * Elimina una partida del mapa (útil para limpieza).
     *
     * @param gameId el ID de la partida a eliminar
     * @return true si la partida existía y fue eliminada
     */
    public boolean deleteGame(String gameId) {
        return activeSessions.remove(gameId) != null;
    }

    /**
     * Retorna el número de partidas activas.
     */
    public int getActiveGamesCount() {
        return activeSessions.size();
    }
}
