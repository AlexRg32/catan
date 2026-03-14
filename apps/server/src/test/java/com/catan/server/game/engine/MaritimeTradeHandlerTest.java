package com.catan.server.game.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.catan.server.game.engine.model.ResourceType;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class MaritimeTradeHandlerTest {

  private final MaritimeTradeHandler handler = new MaritimeTradeHandler(new PortService());

  @Test
  void tradesAtFourToOne() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);
    state.addResource(p1, ResourceType.WOOD, 4);

    handler.trade4to1(state, p1, ResourceType.WOOD, 4, ResourceType.ORE);

    assertEquals(0, state.getResourceCount(p1, ResourceType.WOOD));
    assertEquals(1, state.getResourceCount(p1, ResourceType.ORE));
  }

  @Test
  void requiresGenericPortForThreeToOne() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);
    state.addResource(p1, ResourceType.WOOL, 3);

    assertThrows(
        ResponseStatusException.class,
        () -> handler.trade3to1(state, p1, ResourceType.WOOL, 3, ResourceType.CLAY));

    state.grantGenericPort(p1);
    handler.trade3to1(state, p1, ResourceType.WOOL, 3, ResourceType.CLAY);
    assertEquals(1, state.getResourceCount(p1, ResourceType.CLAY));
  }

  @Test
  void requiresSpecificPortForTwoToOne() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);
    state.addResource(p1, ResourceType.ORE, 2);

    assertThrows(
        ResponseStatusException.class,
        () -> handler.trade2to1(state, p1, ResourceType.ORE, 2, ResourceType.WOOD));

    state.grantSpecificPort(p1, ResourceType.ORE);
    handler.trade2to1(state, p1, ResourceType.ORE, 2, ResourceType.WOOD);
    assertEquals(1, state.getResourceCount(p1, ResourceType.WOOD));
  }
}
