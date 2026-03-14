package com.catan.server.game.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.catan.server.game.engine.model.ResourceType;
import com.catan.server.game.engine.model.SpecialFlow;
import com.catan.server.game.engine.model.TurnPhase;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MoveRobberCommandHandlerTest {

  @Test
  void movesRobberAndStealsOneResource() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);

    state.addResource(p2, ResourceType.CLAY, 1);
    state.setSpecialFlow(SpecialFlow.ROBBER_RESOLUTION);

    MoveRobberCommandHandler handler = new MoveRobberCommandHandler(new PhaseGuard());

    var result = handler.handle(state, p1, 0, p2);

    assertEquals(1, result.previousHexIndex());
    assertEquals(0, result.newHexIndex());
    assertTrue(result.stolenResourceType().isPresent());
    assertEquals(0, state.getResourceCount(p2, ResourceType.CLAY));
    assertEquals(1, state.getResourceCount(p1, ResourceType.CLAY));
    assertEquals(SpecialFlow.NONE, state.getSpecialFlow());
    assertEquals(TurnPhase.TRADING, state.getPhase());
  }
}
