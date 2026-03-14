package com.catan.server.game.engine;

import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.SpecialFlow;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class DiscardService {

  public Map<UUID, Integer> initializeRequiredDiscards(GameRuntimeState state) {
    Map<UUID, Integer> requirements = new HashMap<>();

    for (UUID playerId : state.playerIds()) {
      int total = state.getTotalResources(playerId);
      if (total > 7) {
        requirements.put(playerId, total / 2);
      }
    }

    state.getPendingDiscards().clear();
    state.getPendingDiscards().putAll(requirements);

    if (requirements.isEmpty()) {
      state.setSpecialFlow(SpecialFlow.ROBBER_RESOLUTION);
    } else {
      state.setSpecialFlow(SpecialFlow.DISCARD_RESOLUTION);
    }

    return requirements;
  }
}
