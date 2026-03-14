package com.catan.server.game.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.catan.server.game.engine.model.ResourceType;
import com.catan.server.game.engine.model.SpecialFlow;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DiscardResourcesCommandHandlerTest {

  @Test
  void discardsRequiredCardsAndAdvancesToRobberFlow() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);
    state.addResource(p1, ResourceType.WOOD, 4);
    state.addResource(p1, ResourceType.ORE, 2);

    state.getPendingDiscards().put(p1, 3);
    state.setSpecialFlow(SpecialFlow.DISCARD_RESOLUTION);

    DiscardResourcesCommandHandler handler = new DiscardResourcesCommandHandler(new PhaseGuard());

    handler.handle(state, p1, Map.of(ResourceType.WOOD, 2, ResourceType.ORE, 1));

    assertEquals(2, state.getResourceCount(p1, ResourceType.WOOD));
    assertEquals(1, state.getResourceCount(p1, ResourceType.ORE));
    assertEquals(SpecialFlow.ROBBER_RESOLUTION, state.getSpecialFlow());
  }
}
