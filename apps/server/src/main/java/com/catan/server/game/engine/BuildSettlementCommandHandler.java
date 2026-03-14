package com.catan.server.game.engine;

import com.catan.server.game.engine.model.BuildingType;
import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.SpecialFlow;
import com.catan.server.game.engine.model.TurnPhase;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class BuildSettlementCommandHandler {

  private final PhaseGuard phaseGuard;
  private final SetupPhaseService setupPhaseService;
  private final SettlementPlacementValidator settlementPlacementValidator;
  private final CostService costService;

  public BuildSettlementCommandHandler(
      PhaseGuard phaseGuard,
      SetupPhaseService setupPhaseService,
      SettlementPlacementValidator settlementPlacementValidator,
      CostService costService) {
    this.phaseGuard = phaseGuard;
    this.setupPhaseService = setupPhaseService;
    this.settlementPlacementValidator = settlementPlacementValidator;
    this.costService = costService;
  }

  public void handle(GameRuntimeState state, UUID actorUserId, int nodeIndex) {
    phaseGuard.ensureActivePlayer(state, actorUserId);
    setupPhaseService.ensureSetupCompleted(state);
    ensureBuildWindow(state);

    settlementPlacementValidator.validate(state, actorUserId, nodeIndex);
    costService.paySettlement(state, actorUserId);
    state.setIntersectionOwner(nodeIndex, actorUserId, BuildingType.SETTLEMENT);
    state.setPhase(TurnPhase.BUILDING);
  }

  private void ensureBuildWindow(GameRuntimeState state) {
    if (state.getSpecialFlow() != SpecialFlow.NONE) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Cannot build while a special flow is active");
    }
    if (state.getPhase() != TurnPhase.TRADING && state.getPhase() != TurnPhase.BUILDING) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY,
          "Settlement building is only allowed in TRADING/BUILDING");
    }
  }
}
