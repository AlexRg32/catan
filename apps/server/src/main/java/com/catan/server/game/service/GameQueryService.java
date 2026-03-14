package com.catan.server.game.service;

import com.catan.server.game.dto.GameEventResponse;
import com.catan.server.game.dto.GameEventsResponse;
import com.catan.server.game.dto.GameSnapshotResponse;
import com.catan.server.game.engine.GameRuntimeStore;
import com.catan.server.game.engine.ProjectionService;
import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.projections.GameProjection;
import com.catan.server.game.repository.GamePlayerRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GameQueryService {

  private final GameRuntimeStore gameRuntimeStore;
  private final EventStoreService eventStoreService;
  private final ProjectionService projectionService;
  private final GamePlayerRepository gamePlayerRepository;

  public GameQueryService(
      GameRuntimeStore gameRuntimeStore,
      EventStoreService eventStoreService,
      ProjectionService projectionService,
      GamePlayerRepository gamePlayerRepository) {
    this.gameRuntimeStore = gameRuntimeStore;
    this.eventStoreService = eventStoreService;
    this.projectionService = projectionService;
    this.gamePlayerRepository = gamePlayerRepository;
  }

  @Transactional(readOnly = true)
  public GameSnapshotResponse snapshot(UUID gameId, UUID actorUserId) {
    ensureUserIsGamePlayer(gameId, actorUserId);

    GameRuntimeState state = gameRuntimeStore.getOrCreate(gameId);
    long lastSequence = eventStoreService.latestSequence(gameId);
    GameProjection projection =
        projectionService.projectForPlayer(state, actorUserId, lastSequence);

    return new GameSnapshotResponse(gameId, lastSequence, projection);
  }

  @Transactional(readOnly = true)
  public GameEventsResponse events(UUID gameId, UUID actorUserId, long fromSequence) {
    ensureUserIsGamePlayer(gameId, actorUserId);

    List<GameEventResponse> events =
        eventStoreService.eventsAfter(gameId, fromSequence).stream()
            .map(
                event ->
                    new GameEventResponse(
                        event.seq(),
                        event.type(),
                        event.actorPlayerId(),
                        event.payload(),
                        event.createdAt()))
            .toList();

    long lastSequence = events.isEmpty() ? fromSequence : events.getLast().seq();
    return new GameEventsResponse(gameId, fromSequence, lastSequence, events);
  }

  private void ensureUserIsGamePlayer(UUID gameId, UUID actorUserId) {
    boolean member = gamePlayerRepository.existsByGame_IdAndUser_Id(gameId, actorUserId);
    if (!member) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not part of this game");
    }
  }
}
