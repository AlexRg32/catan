package com.catan.server.game.engine;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class WinConditionServiceTest {

  @Test
  void evaluateOnOwnTurnReturnsFalseBelowTenPoints() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);
    state.setVisibleVictoryPoints(p1, 9);

    WinConditionService service = new WinConditionService();

    assertFalse(service.evaluateOnOwnTurn(state, p1));
    assertFalse(state.isFinished());
  }

  @Test
  void evaluateOnOwnTurnMarksWinnerAtTenOrMore() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);
    state.setVisibleVictoryPoints(p1, 10);

    WinConditionService service = new WinConditionService();

    assertTrue(service.evaluateOnOwnTurn(state, p1));
    assertTrue(state.isFinished());
    assertTrue(p1.equals(state.getWinnerPlayerId()));
  }
}
