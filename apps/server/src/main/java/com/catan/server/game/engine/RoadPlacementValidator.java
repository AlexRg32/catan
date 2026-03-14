package com.catan.server.game.engine;

import com.catan.server.game.engine.model.EdgeState;
import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.IntersectionState;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class RoadPlacementValidator {

  public void validate(GameRuntimeState state, UUID playerId, int edgeIndex) {
    EdgeState edge =
        state.getEdges().stream()
            .filter(candidate -> candidate.getIndex() == edgeIndex)
            .findFirst()
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid edge"));

    if (edge.getOwnerPlayerId() != null) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Edge already occupied");
    }

    boolean connectedA = isNodeConnectedForPlayer(state, playerId, edge.getNodeA());
    boolean connectedB = isNodeConnectedForPlayer(state, playerId, edge.getNodeB());
    if (!connectedA && !connectedB) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Road is not connected");
    }
  }

  private boolean isNodeConnectedForPlayer(GameRuntimeState state, UUID playerId, int nodeId) {
    IntersectionState node =
        state.getIntersections().stream()
            .filter(intersection -> intersection.getIndex() == nodeId)
            .findFirst()
            .orElse(null);

    if (node != null
        && node.getOwnerPlayerId() != null
        && !playerId.equals(node.getOwnerPlayerId())) {
      return false;
    }

    boolean ownBuilding = node != null && playerId.equals(node.getOwnerPlayerId());
    boolean adjacentOwnRoad =
        state.getEdges().stream()
            .anyMatch(
                edge ->
                    playerId.equals(edge.getOwnerPlayerId())
                        && (edge.getNodeA() == nodeId || edge.getNodeB() == nodeId));

    return ownBuilding || adjacentOwnRoad;
  }
}
