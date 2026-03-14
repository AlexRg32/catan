package com.catan.server.game.engine;

import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.HexTileState;
import com.catan.server.game.engine.model.SpecialFlow;
import com.catan.server.game.engine.model.TurnPhase;
import com.catan.server.game.projections.LegalActionProjection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class LegalActionService {

  private final RoadPlacementValidator roadPlacementValidator;
  private final SettlementPlacementValidator settlementPlacementValidator;
  private final CityUpgradeValidator cityUpgradeValidator;
  private final SetupPlacementValidator setupPlacementValidator;

  public LegalActionService(
      RoadPlacementValidator roadPlacementValidator,
      SettlementPlacementValidator settlementPlacementValidator,
      CityUpgradeValidator cityUpgradeValidator,
      SetupPlacementValidator setupPlacementValidator) {
    this.roadPlacementValidator = roadPlacementValidator;
    this.settlementPlacementValidator = settlementPlacementValidator;
    this.cityUpgradeValidator = cityUpgradeValidator;
    this.setupPlacementValidator = setupPlacementValidator;
  }

  public List<LegalActionProjection> forPlayer(GameRuntimeState state, UUID viewerPlayerId) {
    if (!viewerPlayerId.equals(state.getActivePlayerId())) {
      return List.of();
    }

    List<LegalActionProjection> actions = new ArrayList<>();
    actions.add(
        new LegalActionProjection(
            "roll_dice", state.getPhase() == TurnPhase.PRE_ROLL, List.of(), List.of(), List.of()));
    actions.add(
        new LegalActionProjection(
            "end_turn",
            (state.getPhase() == TurnPhase.TRADING || state.getPhase() == TurnPhase.BUILDING)
                && state.getSpecialFlow() == SpecialFlow.NONE,
            List.of(),
            List.of(),
            List.of()));

    if (state.getSpecialFlow() == SpecialFlow.ROBBER_RESOLUTION) {
      List<Integer> allowedHexes =
          state.getHexTiles().stream()
              .filter(hex -> !hex.hasRobber())
              .map(HexTileState::getIndex)
              .toList();
      actions.add(
          new LegalActionProjection("move_robber", true, List.of(), List.of(), allowedHexes));
      return actions;
    }

    if (state.getPhase() == TurnPhase.SETUP) {
      if (!state.isSetupAwaitingRoadPlacement()) {
        List<Integer> nodes =
            state.getIntersections().stream()
                .map(intersection -> intersection.getIndex())
                .filter(nodeIndex -> isSetupSettlementAllowed(state, viewerPlayerId, nodeIndex))
                .toList();
        actions.add(
            new LegalActionProjection(
                "setup_place_settlement", !nodes.isEmpty(), nodes, List.of(), List.of()));
      } else {
        List<Integer> edges =
            state.getEdges().stream()
                .map(edge -> edge.getIndex())
                .filter(edgeIndex -> isSetupRoadAllowed(state, viewerPlayerId, edgeIndex))
                .toList();
        actions.add(
            new LegalActionProjection(
                "setup_place_road", !edges.isEmpty(), List.of(), edges, List.of()));
      }
      return actions;
    }

    if (state.getPhase() == TurnPhase.TRADING || state.getPhase() == TurnPhase.BUILDING) {
      List<Integer> roadEdges =
          state.getEdges().stream()
              .map(edge -> edge.getIndex())
              .filter(edgeIndex -> isRoadAllowed(state, viewerPlayerId, edgeIndex))
              .toList();
      actions.add(
          new LegalActionProjection(
              "build_road", !roadEdges.isEmpty(), List.of(), roadEdges, List.of()));

      List<Integer> settlementNodes =
          state.getIntersections().stream()
              .map(intersection -> intersection.getIndex())
              .filter(nodeIndex -> isSettlementAllowed(state, viewerPlayerId, nodeIndex))
              .toList();
      actions.add(
          new LegalActionProjection(
              "build_settlement",
              !settlementNodes.isEmpty(),
              settlementNodes,
              List.of(),
              List.of()));

      List<Integer> cityNodes =
          state.getIntersections().stream()
              .map(intersection -> intersection.getIndex())
              .filter(nodeIndex -> isCityAllowed(state, viewerPlayerId, nodeIndex))
              .toList();
      actions.add(
          new LegalActionProjection(
              "build_city", !cityNodes.isEmpty(), cityNodes, List.of(), List.of()));

      actions.add(
          new LegalActionProjection(
              "buy_dev_card",
              state.remainingDevelopmentCards() > 0,
              List.of(),
              List.of(),
              List.of()));
    }

    return actions;
  }

  private boolean isRoadAllowed(GameRuntimeState state, UUID playerId, int edgeIndex) {
    return isAllowed(() -> roadPlacementValidator.validate(state, playerId, edgeIndex));
  }

  private boolean isSettlementAllowed(GameRuntimeState state, UUID playerId, int nodeIndex) {
    return isAllowed(() -> settlementPlacementValidator.validate(state, playerId, nodeIndex));
  }

  private boolean isCityAllowed(GameRuntimeState state, UUID playerId, int nodeIndex) {
    return isAllowed(() -> cityUpgradeValidator.validate(state, playerId, nodeIndex));
  }

  private boolean isSetupSettlementAllowed(GameRuntimeState state, UUID playerId, int nodeIndex) {
    return isAllowed(() -> setupPlacementValidator.validateSettlement(state, playerId, nodeIndex));
  }

  private boolean isSetupRoadAllowed(GameRuntimeState state, UUID playerId, int edgeIndex) {
    return isAllowed(() -> setupPlacementValidator.validateRoad(state, playerId, edgeIndex));
  }

  private boolean isAllowed(Validator validator) {
    try {
      validator.run();
      return true;
    } catch (ResponseStatusException ignored) {
      return false;
    }
  }

  @FunctionalInterface
  private interface Validator {
    void run();
  }
}
