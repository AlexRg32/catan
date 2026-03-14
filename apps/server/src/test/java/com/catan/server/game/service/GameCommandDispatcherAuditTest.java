package com.catan.server.game.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.catan.server.game.commands.CommandEnvelope;
import com.catan.server.game.engine.AwardService;
import com.catan.server.game.engine.DiscardResourcesCommandHandler;
import com.catan.server.game.engine.EndTurnCommandHandler;
import com.catan.server.game.engine.GameRuntimeFixtures;
import com.catan.server.game.engine.GameRuntimeStore;
import com.catan.server.game.engine.MaritimeTradeHandler;
import com.catan.server.game.engine.MoveRobberCommandHandler;
import com.catan.server.game.engine.PlayKnightCommandHandler;
import com.catan.server.game.engine.ProgressCardService;
import com.catan.server.game.engine.RollDiceCommandHandler;
import com.catan.server.game.engine.SetupPhaseService;
import com.catan.server.game.trade.AnswerTradeHandler;
import com.catan.server.game.trade.TradeService;
import com.catan.server.observability.CommandMetricsService;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

class GameCommandDispatcherAuditTest {

  @Test
  void rejectedCommandIsAudited() {
    UUID gameId = UUID.randomUUID();
    UUID actor = UUID.randomUUID();
    UUID other = UUID.randomUUID();

    GameRuntimeStore runtimeStore = Mockito.mock(GameRuntimeStore.class);
    var state = GameRuntimeFixtures.basicTwoPlayerState(actor, other);
    when(runtimeStore.getOrCreate(gameId)).thenReturn(state);

    EventStoreService eventStoreService = Mockito.mock(EventStoreService.class);
    AuditTrailService auditTrailService = Mockito.mock(AuditTrailService.class);

    GameCommandDispatcher dispatcher =
        new GameCommandDispatcher(
            runtimeStore,
            Mockito.mock(RollDiceCommandHandler.class),
            Mockito.mock(DiscardResourcesCommandHandler.class),
            Mockito.mock(MoveRobberCommandHandler.class),
            Mockito.mock(PlayKnightCommandHandler.class),
            Mockito.mock(AwardService.class),
            Mockito.mock(ProgressCardService.class),
            Mockito.mock(TradeService.class),
            Mockito.mock(AnswerTradeHandler.class),
            Mockito.mock(MaritimeTradeHandler.class),
            Mockito.mock(EndTurnCommandHandler.class),
            Mockito.mock(SetupPhaseService.class),
            Mockito.mock(com.catan.server.game.engine.BuildRoadCommandHandler.class),
            Mockito.mock(com.catan.server.game.engine.BuildSettlementCommandHandler.class),
            Mockito.mock(com.catan.server.game.engine.BuildCityCommandHandler.class),
            Mockito.mock(com.catan.server.game.engine.BuyDevCardCommandHandler.class),
            eventStoreService,
            new RoomExecutionLock(),
            auditTrailService,
            Mockito.mock(CommandMetricsService.class));

    CommandEnvelope command =
        new CommandEnvelope("cmd-1", gameId, "unknown_cmd", Instant.now(), Map.of());

    assertThrows(ResponseStatusException.class, () -> dispatcher.dispatch(actor, command));

    verify(auditTrailService)
        .record(
            eq(gameId),
            eq("cmd-1"),
            eq(actor),
            eq("unknown_cmd"),
            eq("REJECTED"),
            eq("Unsupported command type: unknown_cmd"));
  }
}
