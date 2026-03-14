package com.catan.server.game.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.catan.server.game.cards.DevCardType;
import com.catan.server.game.engine.model.BuildingType;
import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.HexTileState;
import com.catan.server.game.engine.model.IntersectionState;
import com.catan.server.game.engine.model.ResourceType;
import com.catan.server.game.engine.model.TurnPhase;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BuildAndBuyCommandHandlersTest {

  @Test
  void buildRoadConsumesResourcesAndSetsOwner() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.graphState(p1, p2);
    state.setPhase(TurnPhase.TRADING);
    state.addResource(p1, ResourceType.CLAY, 1);
    state.addResource(p1, ResourceType.WOOD, 1);

    BuildRoadCommandHandler handler =
        new BuildRoadCommandHandler(
            new PhaseGuard(),
            Mockito.mock(SetupPhaseService.class),
            new RoadPlacementValidator(),
            new CostService(),
            new AwardService(new LongestRoadService()));

    handler.handle(state, p1, 1);

    assertEquals(p1, state.edge(1).getOwnerPlayerId());
    assertEquals(0, state.getResourceCount(p1, ResourceType.CLAY));
    assertEquals(0, state.getResourceCount(p1, ResourceType.WOOD));
  }

  @Test
  void buildSettlementPaysCostAndOccupiesIntersection() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    UUID p3 = UUID.randomUUID();
    UUID p4 = UUID.randomUUID();
    var state = GameRuntimeFixtures.setupState(p1, p2, p3, p4);
    state.setPhase(TurnPhase.TRADING);
    state.setEdgeOwner(0, p1);
    state.addResource(p1, ResourceType.CLAY, 1);
    state.addResource(p1, ResourceType.WOOD, 1);
    state.addResource(p1, ResourceType.WOOL, 1);
    state.addResource(p1, ResourceType.GRAIN, 1);

    BuildSettlementCommandHandler handler =
        new BuildSettlementCommandHandler(
            new PhaseGuard(),
            Mockito.mock(SetupPhaseService.class),
            new SettlementPlacementValidator(),
            new CostService());

    handler.handle(state, p1, 1);

    assertEquals(p1, state.intersection(1).getOwnerPlayerId());
    assertEquals(BuildingType.SETTLEMENT, state.intersection(1).getBuildingType());
    assertEquals(0, state.getResourceCount(p1, ResourceType.CLAY));
    assertEquals(0, state.getResourceCount(p1, ResourceType.WOOD));
    assertEquals(0, state.getResourceCount(p1, ResourceType.WOOL));
    assertEquals(0, state.getResourceCount(p1, ResourceType.GRAIN));
  }

  @Test
  void buildCityPaysCostAndUpgradesOwnSettlement() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);
    state.setPhase(TurnPhase.TRADING);
    state.addResource(p1, ResourceType.ORE, 3);
    state.addResource(p1, ResourceType.GRAIN, 2);

    BuildCityCommandHandler handler =
        new BuildCityCommandHandler(
            new PhaseGuard(),
            Mockito.mock(SetupPhaseService.class),
            new CityUpgradeValidator(),
            new CostService());

    handler.handle(state, p1, 0);

    assertEquals(BuildingType.CITY, state.intersection(0).getBuildingType());
    assertEquals(0, state.getResourceCount(p1, ResourceType.ORE));
    assertEquals(0, state.getResourceCount(p1, ResourceType.GRAIN));
  }

  @Test
  void buyDevelopmentCardConsumesCostAndAddsCardToHand() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();

    List<HexTileState> hexes =
        List.of(
            new HexTileState(0, com.catan.server.game.engine.model.TerrainType.WOOD, 8, false),
            new HexTileState(1, com.catan.server.game.engine.model.TerrainType.DESERT, null, true));
    List<IntersectionState> intersections =
        List.of(
            new IntersectionState(0, List.of(0), p1, BuildingType.SETTLEMENT),
            new IntersectionState(1, List.of(1), null, null));
    GameRuntimeState state =
        new GameRuntimeState(
            UUID.randomUUID(),
            111L,
            p1,
            TurnPhase.TRADING,
            List.of(p1, p2),
            hexes,
            intersections,
            List.of(),
            List.of(),
            List.of(DevCardType.KNIGHT));

    state.addResource(p1, ResourceType.ORE, 1);
    state.addResource(p1, ResourceType.WOOL, 1);
    state.addResource(p1, ResourceType.GRAIN, 1);

    BuyDevCardCommandHandler handler =
        new BuyDevCardCommandHandler(
            new PhaseGuard(), Mockito.mock(SetupPhaseService.class), new CostService());

    DevCardType drawn = handler.handle(state, p1);

    assertEquals(DevCardType.KNIGHT, drawn);
    assertEquals(0, state.remainingDevelopmentCards());
    assertNotNull(state.devCards(p1));
    assertEquals(1, state.devCards(p1).totalHandCount());
    assertEquals(0, state.getResourceCount(p1, ResourceType.ORE));
    assertEquals(0, state.getResourceCount(p1, ResourceType.WOOL));
    assertEquals(0, state.getResourceCount(p1, ResourceType.GRAIN));
  }
}
