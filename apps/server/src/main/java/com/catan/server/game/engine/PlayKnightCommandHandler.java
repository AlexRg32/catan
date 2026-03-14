package com.catan.server.game.engine;

import com.catan.server.game.cards.DevCardType;
import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.SpecialFlow;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class PlayKnightCommandHandler {

  private final PhaseGuard phaseGuard;

  public PlayKnightCommandHandler(PhaseGuard phaseGuard) {
    this.phaseGuard = phaseGuard;
  }

  public int handle(GameRuntimeState state, UUID actorUserId) {
    phaseGuard.ensureActivePlayer(state, actorUserId);

    boolean played = state.playDevCard(actorUserId, DevCardType.KNIGHT, state.getTurnNumber());
    if (!played) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "No playable knight card available");
    }

    state.incrementPlayedKnightCount(actorUserId);
    state.setSpecialFlow(SpecialFlow.ROBBER_RESOLUTION);

    return state.playedKnightCount(actorUserId);
  }
}
