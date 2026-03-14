package com.catan.server.game.engine;

import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.SpecialFlow;
import com.catan.server.game.engine.model.TurnPhase;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class RollDiceCommandHandler {

  private final PhaseGuard phaseGuard;
  private final DiceRoller diceRoller;
  private final ProductionService productionService;
  private final DiscardService discardService;

  public RollDiceCommandHandler(
      PhaseGuard phaseGuard,
      DiceRoller diceRoller,
      ProductionService productionService,
      DiscardService discardService) {
    this.phaseGuard = phaseGuard;
    this.diceRoller = diceRoller;
    this.productionService = productionService;
    this.discardService = discardService;
  }

  public RollDiceCommandResult handle(GameRuntimeState state, UUID actorUserId) {
    phaseGuard.ensureActivePlayer(state, actorUserId);
    phaseGuard.ensurePhase(state, TurnPhase.PRE_ROLL);

    int roll = diceRoller.roll2d6(state);
    state.setLastRoll(roll);
    state.setPhase(TurnPhase.POST_ROLL);

    if (roll == 7) {
      Map<UUID, Integer> discards = discardService.initializeRequiredDiscards(state);
      return new RollDiceCommandResult(roll, Map.of(), discards, state.getSpecialFlow());
    }

    Map<UUID, Map<com.catan.server.game.engine.model.ResourceType, Integer>> production =
        productionService.produce(state, roll);
    state.setSpecialFlow(SpecialFlow.NONE);
    state.setPhase(TurnPhase.TRADING);

    return new RollDiceCommandResult(roll, production, Map.of(), SpecialFlow.NONE);
  }
}
