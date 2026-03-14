package com.catan.server.game.engine;

import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.ResourceType;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ProgressYearOfPlentyHandler {

  public void apply(
      GameRuntimeState state, UUID playerId, ResourceType resourceA, ResourceType resourceB) {
    state.addResource(playerId, resourceA, 1);
    state.addResource(playerId, resourceB, 1);
  }
}
