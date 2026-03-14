package com.catan.server.game.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.catan.server.game.engine.model.BuildingType;
import com.catan.server.game.engine.model.EdgeState;
import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.HexTileState;
import com.catan.server.game.engine.model.IntersectionState;
import com.catan.server.game.engine.model.TerrainType;
import com.catan.server.game.engine.model.TurnPhase;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LongestRoadServiceTest {

  private final LongestRoadService service = new LongestRoadService();

  @Test
  void computesLongestRoadWithBranching() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    GameRuntimeState state =
        new GameRuntimeState(
            UUID.randomUUID(),
            123,
            p1,
            TurnPhase.PRE_ROLL,
            List.of(p1, p2),
            List.of(new HexTileState(0, TerrainType.DESERT, null, true)),
            List.of(
                new IntersectionState(0, List.of(0), p1, BuildingType.SETTLEMENT),
                new IntersectionState(1, List.of(0), null, null),
                new IntersectionState(2, List.of(0), null, null),
                new IntersectionState(3, List.of(0), null, null),
                new IntersectionState(4, List.of(0), null, null)),
            List.of(
                new EdgeState(0, 0, 1, p1),
                new EdgeState(1, 1, 2, p1),
                new EdgeState(2, 2, 3, p1),
                new EdgeState(3, 1, 4, p1)));

    assertEquals(3, service.calculateLongestRoad(state, p1));
  }

  @Test
  void opponentSettlementInterruptsContinuity() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    GameRuntimeState state =
        new GameRuntimeState(
            UUID.randomUUID(),
            123,
            p1,
            TurnPhase.PRE_ROLL,
            List.of(p1, p2),
            List.of(new HexTileState(0, TerrainType.DESERT, null, true)),
            List.of(
                new IntersectionState(0, List.of(0), p1, BuildingType.SETTLEMENT),
                new IntersectionState(1, List.of(0), p2, BuildingType.SETTLEMENT),
                new IntersectionState(2, List.of(0), null, null)),
            List.of(new EdgeState(0, 0, 1, p1), new EdgeState(1, 1, 2, p1)));

    assertEquals(1, service.calculateLongestRoad(state, p1));
  }
}
