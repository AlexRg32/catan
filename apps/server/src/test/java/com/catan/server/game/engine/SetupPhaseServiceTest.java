package com.catan.server.game.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.catan.server.game.engine.model.EdgeState;
import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.HexTileState;
import com.catan.server.game.engine.model.IntersectionState;
import com.catan.server.game.engine.model.TerrainType;
import com.catan.server.game.engine.model.TurnPhase;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class SetupPhaseServiceTest {

  @Test
  void completesSetupBeforeNormalPhaseStarts() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    UUID p3 = UUID.randomUUID();
    UUID p4 = UUID.randomUUID();
    GameRuntimeState state = setupFriendlyState(p1, p2, p3, p4);

    SetupPhaseService service =
        new SetupPhaseService(
            new SetupTurnOrderService(),
            new SetupPlacementValidator(new PhaseGuard()),
            new SetupResourceGrantService());

    service.initialize(state);
    List<UUID> expectedOrder = List.of(p1, p2, p3, p4, p4, p3, p2, p1);
    assertEquals(TurnPhase.SETUP, state.getPhase());
    assertEquals(expectedOrder, state.getSetupTurnOrder());

    for (int i = 0; i < expectedOrder.size(); i++) {
      UUID actor = state.getActivePlayerId();
      assertEquals(expectedOrder.get(i), actor);
      service.placeSettlement(state, actor, i);
      service.placeRoad(state, actor, i);
    }

    assertTrue(state.isSetupCompleted());
    assertEquals(TurnPhase.PRE_ROLL, state.getPhase());
    assertEquals(p1, state.getActivePlayerId());
  }

  @Test
  void ensureSetupCompletedBlocksNormalFlowUntilFinished() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    UUID p3 = UUID.randomUUID();
    UUID p4 = UUID.randomUUID();
    GameRuntimeState state = setupFriendlyState(p1, p2, p3, p4);

    SetupPhaseService service =
        new SetupPhaseService(
            new SetupTurnOrderService(),
            new SetupPlacementValidator(new PhaseGuard()),
            new SetupResourceGrantService());

    service.initialize(state);

    assertThrows(ResponseStatusException.class, () -> service.ensureSetupCompleted(state));
  }

  private GameRuntimeState setupFriendlyState(UUID p1, UUID p2, UUID p3, UUID p4) {
    List<HexTileState> hexTiles = List.of(new HexTileState(0, TerrainType.WOOD, 8, false));

    List<IntersectionState> intersections = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      intersections.add(new IntersectionState(i, List.of(0), List.of(), null, null));
    }

    List<EdgeState> edges = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      edges.add(new EdgeState(i, i, (i + 1) % 8, null));
    }

    return new GameRuntimeState(
        UUID.randomUUID(),
        123L,
        p1,
        TurnPhase.PRE_ROLL,
        List.of(p1, p2, p3, p4),
        hexTiles,
        intersections,
        edges);
  }
}
