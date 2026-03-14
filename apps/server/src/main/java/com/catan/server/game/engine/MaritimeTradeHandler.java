package com.catan.server.game.engine;

import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.ResourceType;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class MaritimeTradeHandler {

  private final PortService portService;

  public MaritimeTradeHandler(PortService portService) {
    this.portService = portService;
  }

  public void trade4to1(
      GameRuntimeState state,
      UUID playerId,
      ResourceType giveType,
      int giveCount,
      ResourceType getType) {
    if (giveCount != 4) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "4:1 trade requires 4 cards");
    }
    execute(state, playerId, giveType, giveCount, getType);
  }

  public void trade3to1(
      GameRuntimeState state,
      UUID playerId,
      ResourceType giveType,
      int giveCount,
      ResourceType getType) {
    if (giveCount != 3) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "3:1 trade requires 3 cards");
    }
    if (!portService.hasGenericPort(state, playerId)) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Player does not own a 3:1 port");
    }
    execute(state, playerId, giveType, giveCount, getType);
  }

  public void trade2to1(
      GameRuntimeState state,
      UUID playerId,
      ResourceType giveType,
      int giveCount,
      ResourceType getType) {
    if (giveCount != 2) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "2:1 trade requires 2 cards");
    }
    if (!portService.hasSpecificPort(state, playerId, giveType)) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Player does not own matching 2:1 resource port");
    }
    execute(state, playerId, giveType, giveCount, getType);
  }

  private void execute(
      GameRuntimeState state,
      UUID playerId,
      ResourceType giveType,
      int giveCount,
      ResourceType getType) {
    int available = state.getResourceCount(playerId, giveType);
    if (available < giveCount) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Insufficient cards for maritime trade");
    }
    state.removeResource(playerId, giveType, giveCount);
    state.addResource(playerId, getType, 1);
  }
}
