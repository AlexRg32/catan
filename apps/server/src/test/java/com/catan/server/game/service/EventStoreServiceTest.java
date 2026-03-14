package com.catan.server.game.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.catan.server.game.domain.Game;
import com.catan.server.game.engine.GameRuntimeFixtures;
import com.catan.server.game.persistence.GameEvent;
import com.catan.server.game.persistence.GameEventRepository;
import com.catan.server.game.persistence.GameSnapshot;
import com.catan.server.game.persistence.GameSnapshotRepository;
import com.catan.server.game.repository.GameRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class EventStoreServiceTest {

  @Test
  void appendCommandEventPersistsEventAndSnapshotWithMonotonicSeq() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();

    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);
    UUID stateGameId = state.getGameId();

    Game game = new Game();
    game.setId(stateGameId);

    GameRepository gameRepository = Mockito.mock(GameRepository.class);
    GameEventRepository gameEventRepository = Mockito.mock(GameEventRepository.class);
    GameSnapshotRepository gameSnapshotRepository = Mockito.mock(GameSnapshotRepository.class);

    when(gameRepository.findById(stateGameId)).thenReturn(Optional.of(game));
    when(gameEventRepository.findTopByGameOrderBySeqDesc(game)).thenReturn(Optional.empty());

    EventStoreService service =
        new EventStoreService(
            gameRepository, gameEventRepository, gameSnapshotRepository, new ObjectMapper());

    long seq =
        service.appendCommandEvent(
            state, p1, "roll_dice", Map.of("foo", "bar"), Map.of("status", "accepted"));

    assertEquals(1L, seq);

    ArgumentCaptor<GameEvent> eventCaptor = ArgumentCaptor.forClass(GameEvent.class);
    ArgumentCaptor<GameSnapshot> snapshotCaptor = ArgumentCaptor.forClass(GameSnapshot.class);
    verify(gameEventRepository, times(1)).save(eventCaptor.capture());
    verify(gameSnapshotRepository, times(1)).save(snapshotCaptor.capture());

    assertEquals(1L, eventCaptor.getValue().getSeq());
    assertEquals("roll_dice", eventCaptor.getValue().getType());
    assertEquals(1L, snapshotCaptor.getValue().getSeq());
    assertTrue(snapshotCaptor.getValue().getStateJson().contains("\"seq\":1"));
  }

  @Test
  void replayFromLatestSnapshotReturnsStoredJson() {
    UUID gameId = UUID.randomUUID();
    Game game = new Game();
    game.setId(gameId);

    GameSnapshot snapshot = new GameSnapshot();
    snapshot.setId(UUID.randomUUID());
    snapshot.setGame(game);
    snapshot.setSeq(3L);
    snapshot.setStateJson("{\"seq\":3}");

    GameRepository gameRepository = Mockito.mock(GameRepository.class);
    GameEventRepository gameEventRepository = Mockito.mock(GameEventRepository.class);
    GameSnapshotRepository gameSnapshotRepository = Mockito.mock(GameSnapshotRepository.class);

    when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
    when(gameSnapshotRepository.findTopByGameOrderBySeqDesc(game))
        .thenReturn(Optional.of(snapshot));

    EventStoreService service =
        new EventStoreService(
            gameRepository, gameEventRepository, gameSnapshotRepository, new ObjectMapper());

    assertEquals("{\"seq\":3}", service.replayFromLatestSnapshot(gameId));
    verify(gameEventRepository, times(0)).save(any());
  }
}
