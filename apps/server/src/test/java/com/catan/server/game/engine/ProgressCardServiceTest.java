package com.catan.server.game.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.catan.server.game.cards.DevCardType;
import com.catan.server.game.engine.model.ResourceType;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProgressCardServiceTest {

  private final ProgressCardService service =
      new ProgressCardService(
          new ProgressRoadBuildingHandler(new RoadPlacementValidator()),
          new ProgressYearOfPlentyHandler(),
          new ProgressMonopolyHandler());

  @Test
  void executesRoadBuilding() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.graphState(p1, p2);

    Map<String, Object> response =
        service.execute(state, p1, DevCardType.ROAD_BUILDING, Map.of("edges", List.of(1)));

    assertEquals("ROAD_BUILDING", response.get("applied"));
    assertEquals(
        p1,
        state.getEdges().stream()
            .filter(e -> e.getIndex() == 1)
            .findFirst()
            .orElseThrow()
            .getOwnerPlayerId());
  }

  @Test
  void executesYearOfPlenty() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.graphState(p1, p2);

    service.execute(
        state, p1, DevCardType.YEAR_OF_PLENTY, Map.of("resourceA", "WOOD", "resourceB", "ORE"));

    assertEquals(1, state.getResourceCount(p1, ResourceType.WOOD));
    assertEquals(1, state.getResourceCount(p1, ResourceType.ORE));
  }

  @Test
  void executesMonopoly() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.graphState(p1, p2);
    state.addResource(p2, ResourceType.GRAIN, 3);

    service.execute(state, p1, DevCardType.MONOPOLY, Map.of("resourceType", "GRAIN"));

    assertEquals(3, state.getResourceCount(p1, ResourceType.GRAIN));
    assertEquals(0, state.getResourceCount(p2, ResourceType.GRAIN));
  }
}
