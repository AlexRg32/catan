package com.catan.server.game.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.catan.server.game.engine.model.TurnPhase;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class EndTurnCommandHandlerTest {

  @Test
  void endTurnCyclesPlayersAndResetsPhase() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    UUID p3 = UUID.randomUUID();
    UUID p4 = UUID.randomUUID();
    var state = GameRuntimeFixtures.setupState(p1, p2, p3, p4);
    state.setPhase(TurnPhase.TRADING);

    EndTurnCommandHandler handler =
        new EndTurnCommandHandler(new PhaseGuard(), new WinConditionService());

    var r1 = handler.handle(state, p1);
    assertEquals(p2, r1.nextActivePlayerId());
    assertEquals(TurnPhase.PRE_ROLL, state.getPhase());

    state.setPhase(TurnPhase.BUILDING);
    var r2 = handler.handle(state, p2);
    assertEquals(p3, r2.nextActivePlayerId());

    state.setPhase(TurnPhase.TRADING);
    var r3 = handler.handle(state, p3);
    assertEquals(p4, r3.nextActivePlayerId());

    state.setPhase(TurnPhase.TRADING);
    var r4 = handler.handle(state, p4);
    assertEquals(p1, r4.nextActivePlayerId());
    assertFalse(r4.finished());
    assertTrue(state.getTurnNumber() >= 5);
  }

  @Test
  void endTurnMarksGameFinishedWhenActorHasTenPoints() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    UUID p3 = UUID.randomUUID();
    UUID p4 = UUID.randomUUID();
    var state = GameRuntimeFixtures.setupState(p1, p2, p3, p4);
    state.setPhase(TurnPhase.TRADING);
    state.setVisibleVictoryPoints(p1, 10);

    EndTurnCommandHandler handler =
        new EndTurnCommandHandler(new PhaseGuard(), new WinConditionService());

    var result = handler.handle(state, p1);

    assertTrue(result.finished());
    assertEquals(p1, result.winnerPlayerId());
    assertTrue(state.isFinished());
    assertEquals(p1, state.getWinnerPlayerId());
    assertEquals(p1, state.getActivePlayerId());
  }
}
