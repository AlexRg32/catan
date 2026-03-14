package com.catan.server.game.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.catan.server.game.engine.model.SpecialFlow;
import com.catan.server.game.projections.LegalActionProjection;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LegalActionServiceTest {

  @Test
  void exposesRobberTargetsWhenRobberResolutionIsActive() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);
    state.setSpecialFlow(SpecialFlow.ROBBER_RESOLUTION);

    LegalActionService service =
        new LegalActionService(
            new RoadPlacementValidator(),
            new SettlementPlacementValidator(),
            new CityUpgradeValidator(),
            new SetupPlacementValidator(new PhaseGuard()));

    LegalActionProjection moveRobber =
        service.forPlayer(state, p1).stream()
            .filter(action -> action.actionType().equals("move_robber"))
            .findFirst()
            .orElseThrow();

    assertTrue(moveRobber.enabled());
    assertEquals(1, moveRobber.allowedHexIndexes().size());
    assertEquals(0, moveRobber.allowedHexIndexes().getFirst());
  }
}
