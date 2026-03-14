package com.catan.server.game.engine;

import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.SpecialFlow;
import com.catan.server.game.engine.model.TurnPhase;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class BuildRoadCommandHandler {

  private final PhaseGuard phaseGuard;
  private final SetupPhaseService setupPhaseService;
  private final RoadPlacementValidator roadPlacementValidator;
  private final CostService costService;
  private final AwardService awardService;

  public BuildRoadCommandHandler(
      PhaseGuard phaseGuard,
      SetupPhaseService setupPhaseService,
      RoadPlacementValidator roadPlacementValidator,
      CostService costService,
      AwardService awardService) {
    this.phaseGuard = phaseGuard;
    this.setupPhaseService = setupPhaseService;
    this.roadPlacementValidator = roadPlacementValidator;
    this.costService = costService;
    this.awardService = awardService;
  }

  public Map<UUID, Integer> handle(GameRuntimeState state, UUID actorUserId, int edgeIndex) {
    phaseGuard.ensureActivePlayer(state, actorUserId);
    setupPhaseService.ensureSetupCompleted(state);
    ensureBuildWindow(state);

    roadPlacementValidator.validate(state, actorUserId, edgeIndex);
    costService.payRoad(state, actorUserId);
    state.setEdgeOwner(edgeIndex, actorUserId);
    state.setPhase(TurnPhase.BUILDING);
    return awardService.updateLongestRoad(state);
  }

  private void ensureBuildWindow(GameRuntimeState state) {
    if (state.getSpecialFlow() != SpecialFlow.NONE) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Cannot build while a special flow is active");
    }
    if (state.getPhase() != TurnPhase.TRADING && state.getPhase() != TurnPhase.BUILDING) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Road building is only allowed in TRADING/BUILDING");
    }
  }
}
