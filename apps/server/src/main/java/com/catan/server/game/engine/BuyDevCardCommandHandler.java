package com.catan.server.game.engine;

import com.catan.server.game.cards.DevCardType;
import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.SpecialFlow;
import com.catan.server.game.engine.model.TurnPhase;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class BuyDevCardCommandHandler {

  private final PhaseGuard phaseGuard;
  private final SetupPhaseService setupPhaseService;
  private final CostService costService;

  public BuyDevCardCommandHandler(
      PhaseGuard phaseGuard, SetupPhaseService setupPhaseService, CostService costService) {
    this.phaseGuard = phaseGuard;
    this.setupPhaseService = setupPhaseService;
    this.costService = costService;
  }

  public DevCardType handle(GameRuntimeState state, UUID actorUserId) {
    phaseGuard.ensureActivePlayer(state, actorUserId);
    setupPhaseService.ensureSetupCompleted(state);
    ensureBuildWindow(state);

    if (state.remainingDevelopmentCards() <= 0) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Development deck is empty");
    }

    costService.payDevelopmentCard(state, actorUserId);
    DevCardType drawn = state.drawDevelopmentCard();
    if (drawn == null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Development deck is empty");
    }
    state.addDevCardToPlayer(actorUserId, drawn, state.getTurnNumber());
    state.setPhase(TurnPhase.BUILDING);
    return drawn;
  }

  private void ensureBuildWindow(GameRuntimeState state) {
    if (state.getSpecialFlow() != SpecialFlow.NONE) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Cannot buy development cards during a special flow");
    }
    if (state.getPhase() != TurnPhase.TRADING && state.getPhase() != TurnPhase.BUILDING) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY,
          "Development card purchase is only allowed in TRADING/BUILDING");
    }
  }
}
