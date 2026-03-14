package com.catan.server.game.engine;

import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.HexTileState;
import com.catan.server.game.engine.model.ResourceType;
import com.catan.server.game.engine.model.SpecialFlow;
import com.catan.server.game.engine.model.TurnPhase;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class MoveRobberCommandHandler {

  private final PhaseGuard phaseGuard;

  public MoveRobberCommandHandler(PhaseGuard phaseGuard) {
    this.phaseGuard = phaseGuard;
  }

  public MoveRobberCommandResult handle(
      GameRuntimeState state, UUID actorUserId, int targetHexIndex, UUID targetPlayerId) {
    phaseGuard.ensureActivePlayer(state, actorUserId);
    phaseGuard.ensureSpecialFlow(state, SpecialFlow.ROBBER_RESOLUTION);

    HexTileState current = findRobberTile(state);
    if (current == null) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "No robber found on board");
    }
    if (current.getIndex() == targetHexIndex) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Robber must move to a different tile");
    }

    HexTileState target =
        state.getHexTiles().stream()
            .filter(tile -> tile.getIndex() == targetHexIndex)
            .findFirst()
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY, "Invalid target hex"));

    current.setRobber(false);
    target.setRobber(true);

    Optional<ResourceType> stolenType = Optional.empty();
    if (targetPlayerId != null) {
      validateVictimEligibility(state, actorUserId, targetHexIndex, targetPlayerId);
      stolenType = stealRandomResource(state, actorUserId, targetPlayerId);
    }

    state.setSpecialFlow(SpecialFlow.NONE);
    state.setPhase(TurnPhase.TRADING);

    return new MoveRobberCommandResult(
        current.getIndex(), targetHexIndex, targetPlayerId, stolenType);
  }

  private HexTileState findRobberTile(GameRuntimeState state) {
    return state.getHexTiles().stream().filter(HexTileState::hasRobber).findFirst().orElse(null);
  }

  private void validateVictimEligibility(
      GameRuntimeState state, UUID actorUserId, int targetHexIndex, UUID targetPlayerId) {
    if (actorUserId.equals(targetPlayerId)) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Cannot steal from yourself");
    }

    boolean adjacent =
        state.getIntersections().stream()
            .anyMatch(
                intersection ->
                    targetPlayerId.equals(intersection.getOwnerPlayerId())
                        && intersection.getAdjacentHexIndexes().contains(targetHexIndex));
    if (!adjacent) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Target player is not adjacent to robber tile");
    }
  }

  private Optional<ResourceType> stealRandomResource(
      GameRuntimeState state, UUID actorUserId, UUID targetPlayerId) {
    List<ResourceType> cards = new ArrayList<>();
    for (ResourceType resourceType : ResourceType.values()) {
      int amount = state.getResourceCount(targetPlayerId, resourceType);
      for (int i = 0; i < amount; i++) {
        cards.add(resourceType);
      }
    }

    if (cards.isEmpty()) {
      return Optional.empty();
    }

    int randomIndex = state.rollDie() - 1;
    ResourceType stolen = cards.get(randomIndex % cards.size());
    state.removeResource(targetPlayerId, stolen, 1);
    state.addResource(actorUserId, stolen, 1);
    return Optional.of(stolen);
  }
}
