package com.catan.server.game.engine;

import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.IntersectionState;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class SettlementPlacementValidator {

  public void validate(GameRuntimeState state, UUID playerId, int nodeId) {
    IntersectionState node =
        state.getIntersections().stream()
            .filter(intersection -> intersection.getIndex() == nodeId)
            .findFirst()
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid node"));

    if (node.getOwnerPlayerId() != null) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Node already occupied");
    }

    boolean blockedByDistanceRule =
        node.getAdjacentIntersectionIndexes().stream()
            .anyMatch(
                adjacentNodeId ->
                    state.getIntersections().stream()
                        .anyMatch(
                            intersection ->
                                intersection.getIndex() == adjacentNodeId
                                    && intersection.getOwnerPlayerId() != null));
    if (blockedByDistanceRule) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Distance rule violated");
    }

    boolean connectedRoad =
        state.getEdges().stream()
            .anyMatch(
                edge ->
                    playerId.equals(edge.getOwnerPlayerId())
                        && (edge.getNodeA() == nodeId || edge.getNodeB() == nodeId));
    if (!connectedRoad) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Settlement requires connected own road");
    }
  }
}
