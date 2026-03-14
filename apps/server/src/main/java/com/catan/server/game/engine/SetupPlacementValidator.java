package com.catan.server.game.engine;

import com.catan.server.game.engine.model.EdgeState;
import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.IntersectionState;
import com.catan.server.game.engine.model.TurnPhase;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class SetupPlacementValidator {

  private final PhaseGuard phaseGuard;

  public SetupPlacementValidator(PhaseGuard phaseGuard) {
    this.phaseGuard = phaseGuard;
  }

  public void validateSettlement(GameRuntimeState state, UUID actorUserId, int nodeIndex) {
    phaseGuard.ensureActivePlayer(state, actorUserId);
    phaseGuard.ensurePhase(state, TurnPhase.SETUP);

    if (state.isSetupCompleted()) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Setup already completed");
    }
    if (state.isSetupAwaitingRoadPlacement()) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Place setup road before another settlement");
    }
    if (state.setupSettlementsPlaced(actorUserId) >= 2) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Player already placed two setup settlements");
    }

    IntersectionState target =
        state
            .findIntersection(nodeIndex)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY, "Intersection does not exist"));

    if (target.getOwnerPlayerId() != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Intersection already occupied");
    }

    boolean blockedByDistanceRule =
        state.getIntersections().stream()
            .filter(i -> target.getAdjacentIntersectionIndexes().contains(i.getIndex()))
            .anyMatch(i -> i.getOwnerPlayerId() != null);
    if (blockedByDistanceRule) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY,
          "Distance rule violated: adjacent intersection already occupied");
    }
  }

  public void validateRoad(GameRuntimeState state, UUID actorUserId, int edgeIndex) {
    phaseGuard.ensureActivePlayer(state, actorUserId);
    phaseGuard.ensurePhase(state, TurnPhase.SETUP);

    if (state.isSetupCompleted()) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Setup already completed");
    }
    if (!state.isSetupAwaitingRoadPlacement()) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Setup road must follow setup settlement");
    }

    Integer pendingSettlement = state.getSetupPendingSettlementNodeIndex();
    if (pendingSettlement == null) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Missing setup settlement anchor node");
    }

    EdgeState edge =
        state
            .findEdge(edgeIndex)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY, "Edge does not exist"));

    if (edge.getOwnerPlayerId() != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Edge already occupied");
    }

    boolean connected =
        edge.getNodeA() == pendingSettlement || edge.getNodeB() == pendingSettlement;
    if (!connected) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Setup road must connect to last setup settlement");
    }
  }
}
