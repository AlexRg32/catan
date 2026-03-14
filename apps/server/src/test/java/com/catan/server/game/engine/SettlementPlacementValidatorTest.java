package com.catan.server.game.engine;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class SettlementPlacementValidatorTest {

  private final SettlementPlacementValidator validator = new SettlementPlacementValidator();

  @Test
  void validatesDistanceRuleAndRoadConnection() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.graphState(p1, p2);

    state.getIntersections().get(0).setOwnerPlayerId(null);
    state.getIntersections().get(0).setBuildingType(null);
    state.getIntersections().get(2).setOwnerPlayerId(null);
    state.getIntersections().get(2).setBuildingType(null);
    state.getEdges().get(0).setOwnerPlayerId(p1);

    assertDoesNotThrow(() -> validator.validate(state, p1, 1));
  }

  @Test
  void rejectsWhenAdjacentNodeOccupied() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.graphState(p1, p2);

    assertThrows(ResponseStatusException.class, () -> validator.validate(state, p1, 1));
  }
}
