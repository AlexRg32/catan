package com.catan.server.game.engine;

import com.catan.server.game.engine.model.EdgeState;
import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.IntersectionState;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class LongestRoadService {

  public int calculateLongestRoad(GameRuntimeState state, UUID playerId) {
    List<EdgeState> playerEdges =
        state.getEdges().stream().filter(edge -> playerId.equals(edge.getOwnerPlayerId())).toList();

    int best = 0;
    for (EdgeState edge : playerEdges) {
      best = Math.max(best, dfs(state, playerId, edge.getNodeA(), edge, new HashSet<>()));
      best = Math.max(best, dfs(state, playerId, edge.getNodeB(), edge, new HashSet<>()));
    }
    return best;
  }

  private int dfs(
      GameRuntimeState state,
      UUID playerId,
      int currentNode,
      EdgeState currentEdge,
      Set<Integer> usedEdgeIndexes) {
    usedEdgeIndexes.add(currentEdge.getIndex());

    int nextNode =
        currentEdge.getNodeA() == currentNode ? currentEdge.getNodeB() : currentEdge.getNodeA();
    boolean blocked = isBlockedByOpponentBuilding(state, playerId, nextNode);

    int bestContinuation = 0;
    if (!blocked) {
      for (EdgeState candidate : nextEdges(state, playerId, nextNode, usedEdgeIndexes)) {
        bestContinuation =
            Math.max(
                bestContinuation,
                dfs(state, playerId, nextNode, candidate, new HashSet<>(usedEdgeIndexes)));
      }
    }

    return 1 + bestContinuation;
  }

  private List<EdgeState> nextEdges(
      GameRuntimeState state, UUID playerId, int nodeId, Set<Integer> usedEdgeIndexes) {
    List<EdgeState> candidates = new ArrayList<>();
    for (EdgeState edge : state.getEdges()) {
      if (!playerId.equals(edge.getOwnerPlayerId())) {
        continue;
      }
      if (usedEdgeIndexes.contains(edge.getIndex())) {
        continue;
      }
      if (edge.getNodeA() == nodeId || edge.getNodeB() == nodeId) {
        candidates.add(edge);
      }
    }
    return candidates;
  }

  private boolean isBlockedByOpponentBuilding(GameRuntimeState state, UUID playerId, int nodeId) {
    IntersectionState node =
        state.getIntersections().stream()
            .filter(intersection -> intersection.getIndex() == nodeId)
            .findFirst()
            .orElse(null);

    return node != null
        && node.getOwnerPlayerId() != null
        && !playerId.equals(node.getOwnerPlayerId());
  }
}
