package com.catan.server.game.engine;

import com.catan.server.game.engine.model.BuildingType;
import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.IntersectionState;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CityUpgradeValidator {

  public void validate(GameRuntimeState state, UUID playerId, int nodeId) {
    IntersectionState node =
        state.getIntersections().stream()
            .filter(intersection -> intersection.getIndex() == nodeId)
            .findFirst()
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid node"));

    if (!playerId.equals(node.getOwnerPlayerId())) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Can only upgrade own settlement");
    }

    if (node.getBuildingType() != BuildingType.SETTLEMENT) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Only settlements can be upgraded to city");
    }
  }
}
