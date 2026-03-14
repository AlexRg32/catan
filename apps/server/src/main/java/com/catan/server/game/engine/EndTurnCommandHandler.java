package com.catan.server.game.engine;

import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.SpecialFlow;
import com.catan.server.game.engine.model.TurnPhase;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class EndTurnCommandHandler {

  private final PhaseGuard phaseGuard;
  private final WinConditionService winConditionService;

  public EndTurnCommandHandler(PhaseGuard phaseGuard, WinConditionService winConditionService) {
    this.phaseGuard = phaseGuard;
    this.winConditionService = winConditionService;
  }

  public EndTurnCommandResult handle(GameRuntimeState state, UUID actorUserId) {
    phaseGuard.ensureActivePlayer(state, actorUserId);

    if (state.isFinished()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Game is already finished");
    }

    TurnPhase phase = state.getPhase();
    if (phase != TurnPhase.TRADING && phase != TurnPhase.BUILDING) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "End turn is only allowed during TRADING or BUILDING");
    }

    if (state.getSpecialFlow() != SpecialFlow.NONE) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Cannot end turn while a special flow is active");
    }

    boolean won = winConditionService.evaluateOnOwnTurn(state, actorUserId);
    if (won) {
      return new EndTurnCommandResult(actorUserId, state.getTurnNumber(), true, actorUserId);
    }

    List<UUID> order = state.playerIds();
    int currentIndex = order.indexOf(actorUserId);
    if (currentIndex < 0) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Active player missing");
    }

    UUID nextPlayer = order.get((currentIndex + 1) % order.size());
    state.setActivePlayerId(nextPlayer);
    state.setPhase(TurnPhase.PRE_ROLL);
    state.setSpecialFlow(SpecialFlow.NONE);
    state.setLastRoll(0);
    state.incrementTurnNumber();

    return new EndTurnCommandResult(nextPlayer, state.getTurnNumber(), false, null);
  }
}
