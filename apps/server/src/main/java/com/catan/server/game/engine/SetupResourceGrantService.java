package com.catan.server.game.engine;

import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.IntersectionState;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class SetupResourceGrantService {

  public void grantFromSecondSettlement(
      GameRuntimeState state, UUID playerId, int settlementNodeIndex) {
    IntersectionState intersection =
        state
            .findIntersection(settlementNodeIndex)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY, "Settlement node does not exist"));

    for (Integer hexIndex : intersection.getAdjacentHexIndexes()) {
      state
          .findHex(hexIndex)
          .flatMap(hex -> hex.getTerrain().producedResource())
          .ifPresent(resource -> state.addResource(playerId, resource, 1));
    }
  }
}
