package com.catan.server.game.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.catan.server.game.domain.Game;
import com.catan.server.game.engine.GameRuntimeFixtures;
import com.catan.server.game.persistence.GameEventRepository;
import com.catan.server.game.persistence.GameSnapshot;
import com.catan.server.game.persistence.GameSnapshotRepository;
import com.catan.server.game.repository.GameRepository;
import com.catan.server.game.service.EventStoreService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class EventReplayTest {

  @Test
  void replayStateFromLatestSnapshotMatchesStoredCheckpoint() throws Exception {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);

    Game game = new Game();
    game.setId(state.getGameId());

    GameRepository gameRepository = Mockito.mock(GameRepository.class);
    GameEventRepository eventRepository = Mockito.mock(GameEventRepository.class);
    GameSnapshotRepository snapshotRepository = Mockito.mock(GameSnapshotRepository.class);

    when(gameRepository.findById(state.getGameId())).thenReturn(Optional.of(game));
    when(eventRepository.findTopByGameOrderBySeqDesc(game)).thenReturn(Optional.empty());

    final GameSnapshot[] savedSnapshot = new GameSnapshot[1];
    when(snapshotRepository.save(any(GameSnapshot.class)))
        .thenAnswer(
            invocation -> {
              savedSnapshot[0] = invocation.getArgument(0);
              return savedSnapshot[0];
            });

    EventStoreService store =
        new EventStoreService(
            gameRepository, eventRepository, snapshotRepository, new ObjectMapper());

    long seq = store.appendCommandEvent(state, p1, "roll_dice", Map.of(), Map.of("roll", 8));
    assertEquals(1L, seq);

    when(snapshotRepository.findTopByGameOrderBySeqDesc(game))
        .thenReturn(Optional.of(savedSnapshot[0]));

    String replayed = store.replayFromLatestSnapshot(state.getGameId());
    JsonNode node = new ObjectMapper().readTree(replayed);

    assertEquals(1L, node.get("seq").asLong());
    assertEquals(state.getGameId().toString(), node.get("gameId").asText());
  }
}
