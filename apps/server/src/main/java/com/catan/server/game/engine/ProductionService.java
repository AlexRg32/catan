package com.catan.server.game.engine;

import com.catan.server.game.engine.model.BuildingType;
import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.HexTileState;
import com.catan.server.game.engine.model.IntersectionState;
import com.catan.server.game.engine.model.ResourceType;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ProductionService {

  public Map<UUID, Map<ResourceType, Integer>> produce(GameRuntimeState state, int roll) {
    Map<UUID, Map<ResourceType, Integer>> grants = new HashMap<>();

    for (HexTileState hexTile : state.getHexTiles()) {
      if (hexTile.getNumberToken() == null
          || hexTile.getNumberToken() != roll
          || hexTile.hasRobber()) {
        continue;
      }

      hexTile
          .getTerrain()
          .producedResource()
          .ifPresent(
              resourceType -> {
                for (IntersectionState intersection : state.getIntersections()) {
                  if (!intersection.getAdjacentHexIndexes().contains(hexTile.getIndex())) {
                    continue;
                  }
                  if (intersection.getOwnerPlayerId() == null
                      || intersection.getBuildingType() == null) {
                    continue;
                  }

                  int amount = intersection.getBuildingType() == BuildingType.CITY ? 2 : 1;
                  UUID ownerId = intersection.getOwnerPlayerId();
                  grants.computeIfAbsent(ownerId, ignored -> createEmptyGrant());
                  int current = grants.get(ownerId).get(resourceType);
                  grants.get(ownerId).put(resourceType, current + amount);
                }
              });
    }

    apply(state, grants);
    return grants;
  }

  private void apply(GameRuntimeState state, Map<UUID, Map<ResourceType, Integer>> grants) {
    for (Map.Entry<UUID, Map<ResourceType, Integer>> entry : grants.entrySet()) {
      UUID playerId = entry.getKey();
      for (Map.Entry<ResourceType, Integer> resourceGrant : entry.getValue().entrySet()) {
        if (resourceGrant.getValue() > 0) {
          state.addResource(playerId, resourceGrant.getKey(), resourceGrant.getValue());
        }
      }
    }
  }

  private Map<ResourceType, Integer> createEmptyGrant() {
    Map<ResourceType, Integer> grant = new EnumMap<>(ResourceType.class);
    for (ResourceType type : ResourceType.values()) {
      grant.put(type, 0);
    }
    return grant;
  }
}
