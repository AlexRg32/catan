package com.catan.server.game.service;

import com.catan.server.game.domain.Game;
import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.persistence.GameEvent;
import com.catan.server.game.persistence.GameEventRepository;
import com.catan.server.game.persistence.GameSnapshot;
import com.catan.server.game.persistence.GameSnapshotRepository;
import com.catan.server.game.repository.GameRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EventStoreService {

  private final GameRepository gameRepository;
  private final GameEventRepository gameEventRepository;
  private final GameSnapshotRepository gameSnapshotRepository;
  private final ObjectMapper objectMapper;

  public EventStoreService(
      GameRepository gameRepository,
      GameEventRepository gameEventRepository,
      GameSnapshotRepository gameSnapshotRepository,
      ObjectMapper objectMapper) {
    this.gameRepository = gameRepository;
    this.gameEventRepository = gameEventRepository;
    this.gameSnapshotRepository = gameSnapshotRepository;
    this.objectMapper = objectMapper;
  }

  @Transactional
  public long appendCommandEvent(
      GameRuntimeState state,
      UUID actorPlayerId,
      String commandType,
      Map<String, Object> payload,
      Map<String, Object> ackPayload) {
    Game game = fetchGame(state.getGameId());
    long seq = nextSeq(game);

    GameEvent event = new GameEvent();
    event.setId(UUID.randomUUID());
    event.setGame(game);
    event.setSeq(seq);
    event.setType(commandType);
    event.setActorPlayerId(actorPlayerId);
    event.setPayload(
        toJson(
            Map.of(
                "commandType", commandType,
                "payload", payload != null ? payload : Map.of(),
                "ack", ackPayload != null ? ackPayload : Map.of())));
    gameEventRepository.save(event);

    GameSnapshot snapshot = new GameSnapshot();
    snapshot.setId(UUID.randomUUID());
    snapshot.setGame(game);
    snapshot.setSeq(seq);
    snapshot.setStateJson(toJson(runtimeCheckpoint(state, seq)));
    gameSnapshotRepository.save(snapshot);

    return seq;
  }

  @Transactional(readOnly = true)
  public long latestSequence(UUID gameId) {
    Game game = fetchGame(gameId);
    return gameEventRepository.findTopByGameOrderBySeqDesc(game).map(GameEvent::getSeq).orElse(0L);
  }

  @Transactional(readOnly = true)
  public List<StoredGameEvent> eventsAfter(UUID gameId, long fromSeq) {
    Game game = fetchGame(gameId);
    return gameEventRepository.findByGameAndSeqGreaterThanOrderBySeqAsc(game, fromSeq).stream()
        .map(
            event ->
                new StoredGameEvent(
                    event.getSeq(),
                    event.getType(),
                    event.getActorPlayerId(),
                    event.getPayload(),
                    event.getCreatedAt()))
        .toList();
  }

  @Transactional(readOnly = true)
  public Optional<StoredGameSnapshot> latestSnapshot(UUID gameId) {
    Game game = fetchGame(gameId);
    return gameSnapshotRepository
        .findTopByGameOrderBySeqDesc(game)
        .map(snapshot -> new StoredGameSnapshot(snapshot.getSeq(), snapshot.getStateJson()));
  }

  @Transactional(readOnly = true)
  public String replayFromLatestSnapshot(UUID gameId) {
    return latestSnapshot(gameId)
        .map(StoredGameSnapshot::stateJson)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No snapshot available for requested game"));
  }

  private Game fetchGame(UUID gameId) {
    return gameRepository
        .findById(gameId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));
  }

  private long nextSeq(Game game) {
    return gameEventRepository.findTopByGameOrderBySeqDesc(game).map(GameEvent::getSeq).orElse(0L)
        + 1;
  }

  private Map<String, Object> runtimeCheckpoint(GameRuntimeState state, long seq) {
    Map<String, Object> playerResources = new HashMap<>();
    Map<String, Object> playerVictoryPoints = new HashMap<>();

    for (UUID playerId : state.playerIds()) {
      playerResources.put(playerId.toString(), state.resourcesSnapshot(playerId));
      playerVictoryPoints.put(playerId.toString(), state.totalVictoryPoints(playerId));
    }

    Map<String, Object> checkpoint = new HashMap<>();
    checkpoint.put("gameId", state.getGameId().toString());
    checkpoint.put("seq", seq);
    checkpoint.put(
        "activePlayerId",
        state.getActivePlayerId() != null ? state.getActivePlayerId().toString() : null);
    checkpoint.put("phase", state.getPhase().name());
    checkpoint.put("specialFlow", state.getSpecialFlow().name());
    checkpoint.put("turnNumber", state.getTurnNumber());
    checkpoint.put("finished", state.isFinished());
    checkpoint.put(
        "winnerPlayerId",
        state.getWinnerPlayerId() != null ? state.getWinnerPlayerId().toString() : null);
    checkpoint.put("resources", playerResources);
    checkpoint.put("victoryPoints", playerVictoryPoints);
    return checkpoint;
  }

  private String toJson(Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Could not serialize event payload", e);
    }
  }

  public record StoredGameEvent(
      long seq, String type, UUID actorPlayerId, String payload, Instant createdAt) {}

  public record StoredGameSnapshot(long seq, String stateJson) {}
}
