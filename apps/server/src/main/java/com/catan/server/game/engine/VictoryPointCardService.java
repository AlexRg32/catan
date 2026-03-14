package com.catan.server.game.engine;

import com.catan.server.game.engine.model.GameRuntimeState;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class VictoryPointCardService {

  public int hiddenVictoryPoints(GameRuntimeState state, UUID playerId) {
    return state.hiddenVictoryPointCards(playerId);
  }

  public boolean canReachVictory(GameRuntimeState state, UUID playerId, int visibleVictoryPoints) {
    return visibleVictoryPoints + hiddenVictoryPoints(state, playerId) >= 10;
  }
}
