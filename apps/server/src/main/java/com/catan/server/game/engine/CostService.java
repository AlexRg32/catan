package com.catan.server.game.engine;

import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.ResourceType;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CostService {

  private static final Map<ResourceType, Integer> ROAD_COST =
      Map.of(ResourceType.CLAY, 1, ResourceType.WOOD, 1);

  private static final Map<ResourceType, Integer> SETTLEMENT_COST =
      Map.of(
          ResourceType.CLAY, 1, ResourceType.WOOD, 1, ResourceType.WOOL, 1, ResourceType.GRAIN, 1);

  private static final Map<ResourceType, Integer> CITY_COST =
      Map.of(ResourceType.ORE, 3, ResourceType.GRAIN, 2);

  private static final Map<ResourceType, Integer> DEVELOPMENT_CARD_COST =
      Map.of(ResourceType.ORE, 1, ResourceType.WOOL, 1, ResourceType.GRAIN, 1);

  public void payRoad(GameRuntimeState state, UUID playerId) {
    pay(state, playerId, ROAD_COST, "road");
  }

  public void paySettlement(GameRuntimeState state, UUID playerId) {
    pay(state, playerId, SETTLEMENT_COST, "settlement");
  }

  public void payCity(GameRuntimeState state, UUID playerId) {
    pay(state, playerId, CITY_COST, "city");
  }

  public void payDevelopmentCard(GameRuntimeState state, UUID playerId) {
    pay(state, playerId, DEVELOPMENT_CARD_COST, "development card");
  }

  private void pay(
      GameRuntimeState state, UUID playerId, Map<ResourceType, Integer> cost, String buildName) {
    for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
      int available = state.getResourceCount(playerId, entry.getKey());
      if (available < entry.getValue()) {
        throw new ResponseStatusException(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "Insufficient resources for " + buildName + ": missing " + entry.getKey());
      }
    }

    for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
      state.removeResource(playerId, entry.getKey(), entry.getValue());
    }
  }
}
