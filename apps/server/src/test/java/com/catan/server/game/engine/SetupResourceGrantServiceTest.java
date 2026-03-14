package com.catan.server.game.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.catan.server.game.engine.model.ResourceType;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SetupResourceGrantServiceTest {

  @Test
  void grantsResourcesFromAdjacentNonDesertHexes() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    UUID p3 = UUID.randomUUID();
    UUID p4 = UUID.randomUUID();
    var state = GameRuntimeFixtures.setupState(p1, p2, p3, p4);

    SetupResourceGrantService service = new SetupResourceGrantService();
    service.grantFromSecondSettlement(state, p1, 0);

    assertEquals(1, state.getResourceCount(p1, ResourceType.WOOD));
    assertEquals(1, state.getResourceCount(p1, ResourceType.GRAIN));
  }
}
