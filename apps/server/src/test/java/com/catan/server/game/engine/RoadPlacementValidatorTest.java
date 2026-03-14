package com.catan.server.game.engine;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class RoadPlacementValidatorTest {

  private final RoadPlacementValidator validator = new RoadPlacementValidator();

  @Test
  void allowsConnectedRoadPlacement() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.graphState(p1, p2);

    assertDoesNotThrow(() -> validator.validate(state, p1, 1));
  }

  @Test
  void rejectsDisconnectedRoadPlacement() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.graphState(p1, p2);

    state.getEdges().forEach(edge -> edge.setOwnerPlayerId(null));
    state.getIntersections().forEach(intersection -> intersection.setOwnerPlayerId(null));
    state.getIntersections().forEach(intersection -> intersection.setBuildingType(null));

    assertThrows(ResponseStatusException.class, () -> validator.validate(state, p1, 2));
  }
}
