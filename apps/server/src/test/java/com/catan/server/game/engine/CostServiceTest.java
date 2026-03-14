package com.catan.server.game.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.catan.server.game.engine.model.ResourceType;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class CostServiceTest {

  private final CostService costService = new CostService();
  private UUID p1;
  private UUID p2;

  @BeforeEach
  void setup() {
    p1 = UUID.randomUUID();
    p2 = UUID.randomUUID();
  }

  @Test
  void paysRoadCost() {
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);
    state.addResource(p1, ResourceType.CLAY, 1);
    state.addResource(p1, ResourceType.WOOD, 1);

    costService.payRoad(state, p1);

    assertEquals(0, state.getResourceCount(p1, ResourceType.CLAY));
    assertEquals(0, state.getResourceCount(p1, ResourceType.WOOD));
  }

  @Test
  void paysSettlementCost() {
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);
    state.addResource(p1, ResourceType.CLAY, 1);
    state.addResource(p1, ResourceType.WOOD, 1);
    state.addResource(p1, ResourceType.WOOL, 1);
    state.addResource(p1, ResourceType.GRAIN, 1);

    costService.paySettlement(state, p1);

    assertEquals(0, state.getTotalResources(p1));
  }

  @Test
  void paysCityCost() {
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);
    state.addResource(p1, ResourceType.ORE, 3);
    state.addResource(p1, ResourceType.GRAIN, 2);

    costService.payCity(state, p1);

    assertEquals(0, state.getResourceCount(p1, ResourceType.ORE));
    assertEquals(0, state.getResourceCount(p1, ResourceType.GRAIN));
  }

  @Test
  void paysDevelopmentCardCost() {
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);
    state.addResource(p1, ResourceType.ORE, 1);
    state.addResource(p1, ResourceType.WOOL, 1);
    state.addResource(p1, ResourceType.GRAIN, 1);

    costService.payDevelopmentCard(state, p1);

    assertEquals(0, state.getResourceCount(p1, ResourceType.ORE));
    assertEquals(0, state.getResourceCount(p1, ResourceType.WOOL));
    assertEquals(0, state.getResourceCount(p1, ResourceType.GRAIN));
  }

  @Test
  void rejectsWhenResourcesMissing() {
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);
    state.addResource(p1, ResourceType.CLAY, 1);

    assertThrows(ResponseStatusException.class, () -> costService.payRoad(state, p1));
  }
}
