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
public class BuildCityCommandHandler {

  private final PhaseGuard phaseGuard;
  private final SetupPhaseService setupPhaseService;
  private final CityUpgradeValidator cityUpgradeValidator;
  private final CostService costService;

  public BuildCityCommandHandler(
      PhaseGuard phaseGuard,
      SetupPhaseService setupPhaseService,
      CityUpgradeValidator cityUpgradeValidator,
      CostService costService) {
    this.phaseGuard = phaseGuard;
    this.setupPhaseService = setupPhaseService;
    this.cityUpgradeValidator = cityUpgradeValidator;
    this.costService = costService;
  }

  public void handle(GameRuntimeState state, UUID actorUserId, int nodeIndex) {
    phaseGuard.ensureActivePlayer(state, actorUserId);
    setupPhaseService.ensureSetupCompleted(state);
    ensureBuildWindow(state);

    cityUpgradeValidator.validate(state, actorUserId, nodeIndex);
    costService.payCity(state, actorUserId);
    state.setIntersectionOwner(nodeIndex, actorUserId, BuildingType.CITY);
    state.setPhase(TurnPhase.BUILDING);
  }

  private void ensureBuildWindow(GameRuntimeState state) {
    if (state.getSpecialFlow() != SpecialFlow.NONE) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Cannot build while a special flow is active");
    }
    if (state.getPhase() != TurnPhase.TRADING && state.getPhase() != TurnPhase.BUILDING) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "City building is only allowed in TRADING/BUILDING");
    }
  }
}
