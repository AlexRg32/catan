package com.catan.server.game.engine;

import com.catan.server.game.engine.model.BuildingType;
import com.catan.server.game.engine.model.EdgeState;
import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.HexTileState;
import com.catan.server.game.engine.model.IntersectionState;
import com.catan.server.game.engine.model.ResourceType;
import com.catan.server.game.engine.model.TerrainType;
import com.catan.server.game.engine.model.TurnPhase;
import java.util.List;
import java.util.UUID;

public final class GameRuntimeFixtures {

  private GameRuntimeFixtures() {}

  public static GameRuntimeState basicTwoPlayerState(UUID p1, UUID p2) {
    List<HexTileState> hexTiles =
        List.of(
            new HexTileState(0, TerrainType.WOOD, 8, false),
            new HexTileState(1, TerrainType.DESERT, null, true));

    List<IntersectionState> intersections =
        List.of(
            new IntersectionState(0, List.of(0), p1, BuildingType.SETTLEMENT),
            new IntersectionState(1, List.of(0), p2, BuildingType.CITY),
            new IntersectionState(2, List.of(1), p2, BuildingType.SETTLEMENT));

    return new GameRuntimeState(
        UUID.randomUUID(), 123L, p1, TurnPhase.PRE_ROLL, List.of(p1, p2), hexTiles, intersections);
  }

  public static void addResources(GameRuntimeState state, UUID playerId, int eachTypeCount) {
    for (ResourceType type : ResourceType.values()) {
      state.addResource(playerId, type, eachTypeCount);
    }
  }

  public static GameRuntimeState graphState(UUID p1, UUID p2) {
    List<HexTileState> hexTiles =
        List.of(
            new HexTileState(0, TerrainType.WOOD, 8, false),
            new HexTileState(1, TerrainType.DESERT, null, true));

    List<IntersectionState> intersections =
        List.of(
            new IntersectionState(0, List.of(0), List.of(1), p1, BuildingType.SETTLEMENT),
            new IntersectionState(1, List.of(0), List.of(0, 2), null, null),
            new IntersectionState(2, List.of(1), List.of(1), p2, BuildingType.SETTLEMENT));

    List<EdgeState> edges =
        List.of(
            new EdgeState(0, 0, 1, p1), new EdgeState(1, 1, 2, null), new EdgeState(2, 0, 2, null));

    return new GameRuntimeState(
        UUID.randomUUID(),
        321L,
        p1,
        TurnPhase.PRE_ROLL,
        List.of(p1, p2),
        hexTiles,
        intersections,
        edges);
  }

  public static GameRuntimeState setupState(UUID p1, UUID p2, UUID p3, UUID p4) {
    List<HexTileState> hexTiles =
        List.of(
            new HexTileState(0, TerrainType.WOOD, 8, false),
            new HexTileState(1, TerrainType.GRAIN, 9, false),
            new HexTileState(2, TerrainType.ORE, 5, false),
            new HexTileState(3, TerrainType.DESERT, null, true));

    List<IntersectionState> intersections =
        List.of(
            new IntersectionState(0, List.of(0, 1), List.of(1, 2), null, null),
            new IntersectionState(1, List.of(1, 2), List.of(0, 3), null, null),
            new IntersectionState(2, List.of(2, 3), List.of(0, 3), null, null),
            new IntersectionState(3, List.of(0, 3), List.of(1, 2), null, null));

    List<EdgeState> edges =
        List.of(
            new EdgeState(0, 0, 1, null),
            new EdgeState(1, 0, 2, null),
            new EdgeState(2, 1, 3, null),
            new EdgeState(3, 2, 3, null));

    return new GameRuntimeState(
        UUID.randomUUID(),
        987L,
        p1,
        TurnPhase.PRE_ROLL,
        List.of(p1, p2, p3, p4),
        hexTiles,
        intersections,
        edges);
  }
}
