package com.catan.server.game.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.catan.server.game.engine.EndTurnCommandHandler;
import com.catan.server.game.engine.GameRuntimeFixtures;
import com.catan.server.game.engine.PhaseGuard;
import com.catan.server.game.engine.WinConditionService;
import com.catan.server.game.engine.model.TurnPhase;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class GameFlowIntegrationTest {

  @Test
  void deterministicMiniGameEndsWithWinnerOnOwnTurn() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    UUID p3 = UUID.randomUUID();
    UUID p4 = UUID.randomUUID();
    var state = GameRuntimeFixtures.setupState(p1, p2, p3, p4);
    EndTurnCommandHandler endTurn =
        new EndTurnCommandHandler(new PhaseGuard(), new WinConditionService());

    state.setVisibleVictoryPoints(p1, 9);

    state.setPhase(TurnPhase.TRADING);
    endTurn.handle(state, p1);

    state.setPhase(TurnPhase.TRADING);
    endTurn.handle(state, p2);

    state.setPhase(TurnPhase.TRADING);
    endTurn.handle(state, p3);

    state.setPhase(TurnPhase.TRADING);
    endTurn.handle(state, p4);

    state.addVisibleVictoryPoints(p1, 1);
    state.setPhase(TurnPhase.TRADING);
    var result = endTurn.handle(state, p1);

    assertTrue(result.finished());
    assertEquals(p1, result.winnerPlayerId());
    assertTrue(state.isFinished());
  }
}
