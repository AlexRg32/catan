package com.catan.server.game.engine;

import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.ResourceType;
import com.catan.server.game.engine.model.SpecialFlow;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class DiscardResourcesCommandHandler {

  private final PhaseGuard phaseGuard;

  public DiscardResourcesCommandHandler(PhaseGuard phaseGuard) {
    this.phaseGuard = phaseGuard;
  }

  public void handle(
      GameRuntimeState state, UUID actorUserId, Map<ResourceType, Integer> discardedCards) {
    phaseGuard.ensureSpecialFlow(state, SpecialFlow.DISCARD_RESOLUTION);

    Integer required = state.getPendingDiscards().get(actorUserId);
    if (required == null) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Player has no pending discard");
    }

    int totalDiscarded = discardedCards.values().stream().mapToInt(Integer::intValue).sum();
    if (totalDiscarded != required) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY,
          "Discard mismatch: required " + required + " got " + totalDiscarded);
    }

    for (Map.Entry<ResourceType, Integer> entry : discardedCards.entrySet()) {
      ResourceType type = entry.getKey();
      int amount = entry.getValue();
      if (amount < 0) {
        throw new ResponseStatusException(
            HttpStatus.UNPROCESSABLE_ENTITY, "Invalid discard amount");
      }
      if (state.getResourceCount(actorUserId, type) < amount) {
        throw new ResponseStatusException(
            HttpStatus.UNPROCESSABLE_ENTITY, "Cannot discard resources you do not have");
      }
    }

    for (Map.Entry<ResourceType, Integer> entry : discardedCards.entrySet()) {
      if (entry.getValue() > 0) {
        state.removeResource(actorUserId, entry.getKey(), entry.getValue());
      }
    }

    state.getPendingDiscards().remove(actorUserId);
    if (state.getPendingDiscards().isEmpty()) {
      state.setSpecialFlow(SpecialFlow.ROBBER_RESOLUTION);
    }
  }
}
