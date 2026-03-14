package com.catan.server.game.engine;

import com.catan.server.game.cards.GamePlayerDevCards;
import com.catan.server.game.engine.model.EdgeState;
import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.IntersectionState;
import com.catan.server.game.projections.BoardEdgeProjection;
import com.catan.server.game.projections.BoardHexProjection;
import com.catan.server.game.projections.BoardIntersectionProjection;
import com.catan.server.game.projections.BoardPortProjection;
import com.catan.server.game.projections.BoardProjection;
import com.catan.server.game.projections.GamePlayerProjection;
import com.catan.server.game.projections.GameProjection;
import com.catan.server.game.projections.LegalActionProjection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ProjectionService {

  private final LegalActionService legalActionService;

  public ProjectionService(LegalActionService legalActionService) {
    this.legalActionService = legalActionService;
  }

  public GameProjection projectForPlayer(
      GameRuntimeState state, UUID viewerPlayerId, long lastSequence) {
    List<LegalActionProjection> legalActions = legalActionService.forPlayer(state, viewerPlayerId);
    BoardProjection board = projectBoard(state);
    List<GamePlayerProjection> players =
        state.playerIds().stream()
            .map(playerId -> projectPlayer(state, viewerPlayerId, playerId))
            .toList();

    return new GameProjection(
        state.getGameId(),
        state.getPhase().name(),
        state.getSpecialFlow().name(),
        state.getActivePlayerId(),
        state.getTurnNumber(),
        state.getLastRoll(),
        state.isSetupCompleted(),
        state.isFinished(),
        state.getWinnerPlayerId(),
        lastSequence,
        board,
        legalActions,
        players);
  }

  private BoardProjection projectBoard(GameRuntimeState state) {
    List<BoardHexProjection> hexes =
        state.getHexTiles().stream()
            .map(
                hex ->
                    new BoardHexProjection(
                        hex.getIndex(),
                        hex.getTerrain().name(),
                        hex.getNumberToken(),
                        hex.hasRobber(),
                        hex.getX(),
                        hex.getY(),
                        hex.getZ()))
            .toList();

    List<BoardIntersectionProjection> intersections =
        state.getIntersections().stream()
            .map(
                node ->
                    new BoardIntersectionProjection(
                        node.getIndex(),
                        node.getAdjacentHexIndexes(),
                        node.getAdjacentIntersectionIndexes(),
                        node.getOwnerPlayerId(),
                        node.getBuildingType() == null ? null : node.getBuildingType().name(),
                        node.getX(),
                        node.getY(),
                        node.getZ()))
            .toList();

    Map<Integer, IntersectionState> nodeByIndex =
        state.getIntersections().stream()
            .collect(java.util.stream.Collectors.toMap(IntersectionState::getIndex, node -> node));
    List<BoardEdgeProjection> edges =
        state.getEdges().stream().map(edge -> projectEdge(edge, nodeByIndex)).toList();

    List<BoardPortProjection> ports =
        state.getPorts().stream()
            .map(
                port ->
                    new BoardPortProjection(
                        port.getIndex(),
                        port.getEdgeIndex(),
                        port.getRatio(),
                        port.getResourceType() == null ? null : port.getResourceType().name()))
            .toList();

    return new BoardProjection(hexes, intersections, edges, ports);
  }

  private BoardEdgeProjection projectEdge(
      EdgeState edge, Map<Integer, IntersectionState> nodeByIndex) {
    IntersectionState nodeA = nodeByIndex.get(edge.getNodeA());
    IntersectionState nodeB = nodeByIndex.get(edge.getNodeB());
    if (nodeA == null || nodeB == null) {
      throw new IllegalStateException("Edge references missing nodes");
    }

    HashSet<Integer> adjacentHexes = new HashSet<>(nodeA.getAdjacentHexIndexes());
    adjacentHexes.retainAll(new HashSet<>(nodeB.getAdjacentHexIndexes()));
    List<Integer> sharedHexes = adjacentHexes.stream().sorted().toList();

    return new BoardEdgeProjection(
        edge.getIndex(),
        edge.getNodeA(),
        edge.getNodeB(),
        edge.getOwnerPlayerId(),
        nodeA.getX(),
        nodeA.getY(),
        nodeA.getZ(),
        nodeB.getX(),
        nodeB.getY(),
        nodeB.getZ(),
        sharedHexes);
  }

  private GamePlayerProjection projectPlayer(
      GameRuntimeState state, UUID viewerPlayerId, UUID targetPlayerId) {
    boolean self = targetPlayerId.equals(viewerPlayerId);

    GamePlayerDevCards devCards = state.devCards(targetPlayerId);
    return new GamePlayerProjection(
        targetPlayerId,
        self,
        state.visibleVictoryPoints(targetPlayerId),
        state.totalVictoryPoints(targetPlayerId),
        state.getTotalResources(targetPlayerId),
        self ? state.resourcesSnapshot(targetPlayerId) : null,
        devCards.totalHandCount(),
        self ? devCards.handSnapshot() : null,
        state.playedKnightCount(targetPlayerId),
        targetPlayerId.equals(state.getLongestRoadHolderId()),
        targetPlayerId.equals(state.getLargestArmyHolderId()),
        self ? state.hiddenVictoryPointCards(targetPlayerId) : null);
  }
}
