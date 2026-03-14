package com.catan.server.game.engine;

import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.ResourceType;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PortService {

  public boolean hasGenericPort(GameRuntimeState state, UUID playerId) {
    return state.hasGenericPort(playerId);
  }

  public boolean hasSpecificPort(GameRuntimeState state, UUID playerId, ResourceType resourceType) {
    return state.hasSpecificPort(playerId, resourceType);
  }
}
