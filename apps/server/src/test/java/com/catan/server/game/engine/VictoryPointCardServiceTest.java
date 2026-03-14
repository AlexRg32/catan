package com.catan.server.game.engine;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.catan.server.game.cards.DevCardType;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class VictoryPointCardServiceTest {

  @Test
  void hiddenVpCardsRemainCountedForWinCheck() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);

    state.addDevCardToPlayer(p1, DevCardType.VICTORY_POINT, 1);
    state.addDevCardToPlayer(p1, DevCardType.VICTORY_POINT, 1);

    VictoryPointCardService service = new VictoryPointCardService();

    assertFalse(service.canReachVictory(state, p1, 7));
    assertTrue(service.canReachVictory(state, p1, 8));
  }
}
