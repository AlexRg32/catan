package com.catan.server.game.engine;

import com.catan.server.game.engine.model.BuildingType;
import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.TurnPhase;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class SetupPhaseService {

  private final SetupTurnOrderService setupTurnOrderService;
  private final SetupPlacementValidator setupPlacementValidator;
  private final SetupResourceGrantService setupResourceGrantService;

  public SetupPhaseService(
      SetupTurnOrderService setupTurnOrderService,
      SetupPlacementValidator setupPlacementValidator,
      SetupResourceGrantService setupResourceGrantService) {
    this.setupTurnOrderService = setupTurnOrderService;
    this.setupPlacementValidator = setupPlacementValidator;
    this.setupResourceGrantService = setupResourceGrantService;
  }

  public void initialize(GameRuntimeState state) {
    List<UUID> snakeOrder = setupTurnOrderService.snakeOrder(state.playerIds());
    state.setSetupTurnOrder(snakeOrder);
    state.setSetupTurnIndex(0);
    state.setSetupCompleted(false);
    state.setSetupAwaitingRoadPlacement(false);
    state.setSetupPendingSettlementNodeIndex(null);
    state.setPhase(TurnPhase.SETUP);
    state.setActivePlayerId(snakeOrder.getFirst());
  }

  public void placeSettlement(GameRuntimeState state, UUID actorUserId, int nodeIndex) {
    setupPlacementValidator.validateSettlement(state, actorUserId, nodeIndex);

    state.setIntersectionOwner(nodeIndex, actorUserId, BuildingType.SETTLEMENT);
    state.incrementSetupSettlementsPlaced(actorUserId);
    state.setSetupPendingSettlementNodeIndex(nodeIndex);
    state.setSetupAwaitingRoadPlacement(true);
  }

  public void placeRoad(GameRuntimeState state, UUID actorUserId, int edgeIndex) {
    setupPlacementValidator.validateRoad(state, actorUserId, edgeIndex);

    state.setEdgeOwner(edgeIndex, actorUserId);
    int lastSettlementNode = state.getSetupPendingSettlementNodeIndex();

    if (state.setupSettlementsPlaced(actorUserId) == 2) {
      setupResourceGrantService.grantFromSecondSettlement(state, actorUserId, lastSettlementNode);
    }

    state.setSetupAwaitingRoadPlacement(false);
    state.setSetupPendingSettlementNodeIndex(null);

    int nextIndex = state.getSetupTurnIndex() + 1;
    if (nextIndex >= state.getSetupTurnOrder().size()) {
      state.setSetupCompleted(true);
      state.setPhase(TurnPhase.PRE_ROLL);
      state.setActivePlayerId(state.playerIds().getFirst());
      return;
    }

    state.setSetupTurnIndex(nextIndex);
    state.setActivePlayerId(state.getSetupTurnOrder().get(nextIndex));
  }

  public void ensureSetupCompleted(GameRuntimeState state) {
    if (!state.isSetupCompleted()) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Setup must complete before normal turns");
    }
  }
}
