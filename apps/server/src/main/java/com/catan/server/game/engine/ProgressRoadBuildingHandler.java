package com.catan.server.game.engine;

import com.catan.server.game.engine.model.GameRuntimeState;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ProgressRoadBuildingHandler {

  private final RoadPlacementValidator roadPlacementValidator;

  public ProgressRoadBuildingHandler(RoadPlacementValidator roadPlacementValidator) {
    this.roadPlacementValidator = roadPlacementValidator;
  }

  public void apply(GameRuntimeState state, UUID playerId, List<Integer> edgeIndexes) {
    if (edgeIndexes.isEmpty() || edgeIndexes.size() > 2) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Road building expects one or two edge indexes");
    }

    Set<Integer> unique = new HashSet<>(edgeIndexes);
    if (unique.size() != edgeIndexes.size()) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Duplicate edge index");
    }

    for (Integer edgeIndex : edgeIndexes) {
      roadPlacementValidator.validate(state, playerId, edgeIndex);
    }

    for (Integer edgeIndex : edgeIndexes) {
      state.setEdgeOwner(edgeIndex, playerId);
    }
  }
}
