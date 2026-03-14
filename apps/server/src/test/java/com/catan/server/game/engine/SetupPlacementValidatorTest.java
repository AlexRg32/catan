package com.catan.server.game.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.catan.server.game.engine.model.BuildingType;
import com.catan.server.game.engine.model.TurnPhase;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class SetupPlacementValidatorTest {

  @Test
  void rejectsSettlementAdjacentToExistingBuilding() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    UUID p3 = UUID.randomUUID();
    UUID p4 = UUID.randomUUID();
    var state = GameRuntimeFixtures.setupState(p1, p2, p3, p4);
    state.setPhase(TurnPhase.SETUP);
    state.setActivePlayerId(p1);
    state.setIntersectionOwner(1, p2, BuildingType.SETTLEMENT);

    SetupPlacementValidator validator = new SetupPlacementValidator(new PhaseGuard());

    ResponseStatusException ex =
        assertThrows(
            ResponseStatusException.class, () -> validator.validateSettlement(state, p1, 0));
    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ex.getStatusCode());
  }

  @Test
  void rejectsRoadThatDoesNotTouchPendingSettlement() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    UUID p3 = UUID.randomUUID();
    UUID p4 = UUID.randomUUID();
    var state = GameRuntimeFixtures.setupState(p1, p2, p3, p4);
    state.setPhase(TurnPhase.SETUP);
    state.setActivePlayerId(p1);
    state.setSetupCompleted(false);
    state.setSetupAwaitingRoadPlacement(true);
    state.setSetupPendingSettlementNodeIndex(0);

    SetupPlacementValidator validator = new SetupPlacementValidator(new PhaseGuard());

    ResponseStatusException ex =
        assertThrows(ResponseStatusException.class, () -> validator.validateRoad(state, p1, 2));
    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ex.getStatusCode());
  }
}
