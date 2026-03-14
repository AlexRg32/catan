package com.catan.server.game.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.catan.server.game.engine.GameRuntimeFixtures;
import com.catan.server.game.engine.GameRuntimeStore;
import com.catan.server.game.engine.LegalActionService;
import com.catan.server.game.engine.ProjectionService;
import com.catan.server.game.repository.GamePlayerRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

class GameQueryServiceTest {

  @Test
  void snapshotReturnsProjectionWithLastSequence() {
    UUID gameId = UUID.randomUUID();
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);

    GameRuntimeStore runtimeStore = Mockito.mock(GameRuntimeStore.class);
    EventStoreService eventStoreService = Mockito.mock(EventStoreService.class);
    GamePlayerRepository gamePlayerRepository = Mockito.mock(GamePlayerRepository.class);

    when(gamePlayerRepository.existsByGame_IdAndUser_Id(gameId, p1)).thenReturn(true);
    when(runtimeStore.getOrCreate(gameId)).thenReturn(state);
    when(eventStoreService.latestSequence(gameId)).thenReturn(12L);
    LegalActionService legalActionService = Mockito.mock(LegalActionService.class);
    when(legalActionService.forPlayer(state, p1)).thenReturn(List.of());

    GameQueryService service =
        new GameQueryService(
            runtimeStore,
            eventStoreService,
            new ProjectionService(legalActionService),
            gamePlayerRepository);

    var response = service.snapshot(gameId, p1);

    assertEquals(12L, response.lastSequence());
    assertEquals(gameId, response.gameId());
    assertEquals(12L, response.state().lastSequence());
  }

  @Test
  void eventsRejectsNonMemberPlayer() {
    UUID gameId = UUID.randomUUID();
    UUID actor = UUID.randomUUID();

    GameRuntimeStore runtimeStore = Mockito.mock(GameRuntimeStore.class);
    EventStoreService eventStoreService = Mockito.mock(EventStoreService.class);
    GamePlayerRepository gamePlayerRepository = Mockito.mock(GamePlayerRepository.class);

    when(gamePlayerRepository.existsByGame_IdAndUser_Id(gameId, actor)).thenReturn(false);

    GameQueryService service =
        new GameQueryService(
            runtimeStore,
            eventStoreService,
            new ProjectionService(Mockito.mock(LegalActionService.class)),
            gamePlayerRepository);

    assertThrows(ResponseStatusException.class, () -> service.events(gameId, actor, 0));
  }

  @Test
  void eventsReturnsOnlyMissingDeltas() {
    UUID gameId = UUID.randomUUID();
    UUID actor = UUID.randomUUID();

    GameRuntimeStore runtimeStore = Mockito.mock(GameRuntimeStore.class);
    EventStoreService eventStoreService = Mockito.mock(EventStoreService.class);
    GamePlayerRepository gamePlayerRepository = Mockito.mock(GamePlayerRepository.class);

    when(gamePlayerRepository.existsByGame_IdAndUser_Id(gameId, actor)).thenReturn(true);
    when(eventStoreService.eventsAfter(gameId, 10L))
        .thenReturn(
            List.of(
                new EventStoreService.StoredGameEvent(
                    11L, "roll_dice", actor, "{\"roll\":8}", Instant.now()),
                new EventStoreService.StoredGameEvent(
                    12L, "end_turn", actor, "{}", Instant.now())));
    LegalActionService legalActionService = Mockito.mock(LegalActionService.class);

    GameQueryService service =
        new GameQueryService(
            runtimeStore,
            eventStoreService,
            new ProjectionService(legalActionService),
            gamePlayerRepository);

    var response = service.events(gameId, actor, 10L);

    assertEquals(2, response.events().size());
    assertEquals(12L, response.lastSequence());
    assertEquals(11L, response.events().getFirst().seq());
  }
}
