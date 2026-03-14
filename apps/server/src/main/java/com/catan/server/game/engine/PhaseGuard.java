package com.catan.server.game.engine;

import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.SpecialFlow;
import com.catan.server.game.engine.model.TurnPhase;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class PhaseGuard {

  public void ensureActivePlayer(GameRuntimeState state, UUID actorUserId) {
    if (!state.getActivePlayerId().equals(actorUserId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only active player can do this");
    }
  }

  public void ensurePhase(GameRuntimeState state, TurnPhase expectedPhase) {
    if (state.getPhase() != expectedPhase) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY,
          "Invalid phase: expected " + expectedPhase + " but got " + state.getPhase());
    }
  }

  public void ensureSpecialFlow(GameRuntimeState state, SpecialFlow specialFlow) {
    if (state.getSpecialFlow() != specialFlow) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY,
          "Expected special flow " + specialFlow + " but got " + state.getSpecialFlow());
    }
  }
}
