package com.catan.server.game.engine;

import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.ResourceType;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ProgressMonopolyHandler {

  public int apply(GameRuntimeState state, UUID playerId, ResourceType resourceType) {
    int totalCollected = 0;
    for (UUID otherPlayerId : state.playerIds()) {
      if (otherPlayerId.equals(playerId)) {
        continue;
      }
      int available = state.getResourceCount(otherPlayerId, resourceType);
      if (available > 0) {
        state.removeResource(otherPlayerId, resourceType, available);
        state.addResource(playerId, resourceType, available);
        totalCollected += available;
      }
    }
    return totalCollected;
  }
}
