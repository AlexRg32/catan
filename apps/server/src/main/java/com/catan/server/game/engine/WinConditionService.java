package com.catan.server.game.engine;

import com.catan.server.game.engine.model.GameRuntimeState;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class WinConditionService {

  public static final int VICTORY_POINTS_TO_WIN = 10;

  public boolean evaluateOnOwnTurn(GameRuntimeState state, UUID actorUserId) {
    if (!actorUserId.equals(state.getActivePlayerId())) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Only active player can trigger win detection");
    }

    int totalVictoryPoints = state.totalVictoryPoints(actorUserId);
    if (totalVictoryPoints >= VICTORY_POINTS_TO_WIN) {
      state.setFinished(true);
      state.setWinnerPlayerId(actorUserId);
      return true;
    }

    return false;
  }
}
