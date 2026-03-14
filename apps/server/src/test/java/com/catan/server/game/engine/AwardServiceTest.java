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

class AwardServiceTest {

  @Test
  void assignsAndTransfersLongestRoadAward() {
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
                new IntersectionState(4, List.of(0), null, null),
                new IntersectionState(5, List.of(0), null, null),
                new IntersectionState(6, List.of(0), null, null)),
            List.of(
                new EdgeState(0, 0, 1, p1),
                new EdgeState(1, 1, 2, p1),
                new EdgeState(2, 2, 3, p1),
                new EdgeState(3, 3, 4, p1),
                new EdgeState(4, 4, 5, p1),
                new EdgeState(5, 1, 6, p2),
                new EdgeState(6, 6, 3, p2),
                new EdgeState(7, 3, 5, p2),
                new EdgeState(8, 5, 4, p2),
                new EdgeState(9, 4, 2, p2),
                new EdgeState(10, 2, 0, null)));

    AwardService awardService = new AwardService(new LongestRoadService());

    awardService.updateLongestRoad(state);
    assertEquals(p1, state.getLongestRoadHolderId());

    state.getEdges().stream()
        .filter(edge -> edge.getIndex() == 10)
        .findFirst()
        .orElseThrow()
        .setOwnerPlayerId(p2);

    awardService.updateLongestRoad(state);
    assertEquals(p2, state.getLongestRoadHolderId());
  }

  @Test
  void assignsAndTransfersLargestArmyAward() {
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
                new IntersectionState(1, List.of(0), p2, BuildingType.SETTLEMENT)));

    state.incrementPlayedKnightCount(p1);
    state.incrementPlayedKnightCount(p1);
    state.incrementPlayedKnightCount(p1);

    AwardService awardService = new AwardService(new LongestRoadService());
    awardService.updateLargestArmy(state);
    assertEquals(p1, state.getLargestArmyHolderId());

    state.incrementPlayedKnightCount(p2);
    state.incrementPlayedKnightCount(p2);
    state.incrementPlayedKnightCount(p2);
    state.incrementPlayedKnightCount(p2);
    awardService.updateLargestArmy(state);
    assertEquals(p2, state.getLargestArmyHolderId());
  }
}
