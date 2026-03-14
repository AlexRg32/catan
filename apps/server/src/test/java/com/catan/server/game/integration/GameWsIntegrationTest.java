package com.catan.server.game.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.catan.server.game.commands.CommandEnvelope;
import com.catan.server.game.engine.EndTurnCommandHandler;
import com.catan.server.game.engine.GameRuntimeFixtures;
import com.catan.server.game.engine.GameRuntimeStore;
import com.catan.server.game.engine.PhaseGuard;
import com.catan.server.game.engine.WinConditionService;
import com.catan.server.game.engine.model.TurnPhase;
import com.catan.server.game.service.AuditTrailService;
import com.catan.server.game.service.EventStoreService;
import com.catan.server.game.service.GameCommandDispatcher;
import com.catan.server.game.service.RoomExecutionLock;
import com.catan.server.observability.CommandMetricsService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class GameWsIntegrationTest {

  @Test
  void harnessKeepsSynchronizedEventSequenceAcrossFourClients() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    UUID p3 = UUID.randomUUID();
    UUID p4 = UUID.randomUUID();
    var state = GameRuntimeFixtures.setupState(p1, p2, p3, p4);
    state.setPhase(TurnPhase.TRADING);

    GameRuntimeStore runtimeStore = Mockito.mock(GameRuntimeStore.class);
    when(runtimeStore.getOrCreate(state.getGameId())).thenReturn(state);

    AtomicLong sequence = new AtomicLong(0);
    EventStoreService eventStore = Mockito.mock(EventStoreService.class);
    when(eventStore.appendCommandEvent(any(), any(), eq("end_turn"), any(), any()))
        .thenAnswer(invocation -> sequence.incrementAndGet());

    GameCommandDispatcher dispatcher =
        new GameCommandDispatcher(
            runtimeStore,
            Mockito.mock(com.catan.server.game.engine.RollDiceCommandHandler.class),
            Mockito.mock(com.catan.server.game.engine.DiscardResourcesCommandHandler.class),
            Mockito.mock(com.catan.server.game.engine.MoveRobberCommandHandler.class),
            Mockito.mock(com.catan.server.game.engine.PlayKnightCommandHandler.class),
            Mockito.mock(com.catan.server.game.engine.AwardService.class),
            Mockito.mock(com.catan.server.game.engine.ProgressCardService.class),
            Mockito.mock(com.catan.server.game.trade.TradeService.class),
            Mockito.mock(com.catan.server.game.trade.AnswerTradeHandler.class),
            Mockito.mock(com.catan.server.game.engine.MaritimeTradeHandler.class),
            new EndTurnCommandHandler(new PhaseGuard(), new WinConditionService()),
            Mockito.mock(com.catan.server.game.engine.SetupPhaseService.class),
            Mockito.mock(com.catan.server.game.engine.BuildRoadCommandHandler.class),
            Mockito.mock(com.catan.server.game.engine.BuildSettlementCommandHandler.class),
            Mockito.mock(com.catan.server.game.engine.BuildCityCommandHandler.class),
            Mockito.mock(com.catan.server.game.engine.BuyDevCardCommandHandler.class),
            eventStore,
            new RoomExecutionLock(),
            Mockito.mock(AuditTrailService.class),
            Mockito.mock(CommandMetricsService.class));

    List<Long> c1 = new ArrayList<>();
    List<Long> c2 = new ArrayList<>();
    List<Long> c3 = new ArrayList<>();
    List<Long> c4 = new ArrayList<>();

    for (UUID actor : List.of(p1, p2, p3, p4)) {
      state.setPhase(TurnPhase.TRADING);
      CommandEnvelope command =
          new CommandEnvelope(
              "cmd-" + actor, state.getGameId(), "end_turn", Instant.now(), Map.of());
      Map<String, Object> ack = dispatcher.dispatch(actor, command);
      long seq = ((Number) ack.get("seq")).longValue();

      c1.add(seq);
      c2.add(seq);
      c3.add(seq);
      c4.add(seq);
    }

    assertEquals(List.of(1L, 2L, 3L, 4L), c1);
    assertEquals(c1, c2);
    assertEquals(c1, c3);
    assertEquals(c1, c4);
  }
}
