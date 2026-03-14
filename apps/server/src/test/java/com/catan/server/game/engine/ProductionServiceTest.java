package com.catan.server.game.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.catan.server.game.engine.model.ResourceType;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProductionServiceTest {

  private final ProductionService productionService = new ProductionService();

  @Test
  void producesForSettlementAndCity() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);

    Map<UUID, Map<ResourceType, Integer>> grants = productionService.produce(state, 8);

    assertEquals(1, grants.get(p1).get(ResourceType.WOOD));
    assertEquals(2, grants.get(p2).get(ResourceType.WOOD));
    assertEquals(1, state.getResourceCount(p1, ResourceType.WOOD));
    assertEquals(2, state.getResourceCount(p2, ResourceType.WOOD));
  }

  @Test
  void robberBlocksProduction() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);

    state.getHexTiles().stream()
        .filter(tile -> tile.getIndex() == 0)
        .findFirst()
        .orElseThrow()
        .setRobber(true);

    Map<UUID, Map<ResourceType, Integer>> grants = productionService.produce(state, 8);

    assertEquals(0, grants.size());
    assertEquals(0, state.getResourceCount(p1, ResourceType.WOOD));
    assertEquals(0, state.getResourceCount(p2, ResourceType.WOOD));
  }
}
