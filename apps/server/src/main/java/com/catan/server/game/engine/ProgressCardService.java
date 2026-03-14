package com.catan.server.game.engine;

import com.catan.server.game.cards.DevCardType;
import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.ResourceType;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ProgressCardService {

  private final ProgressRoadBuildingHandler roadBuildingHandler;
  private final ProgressYearOfPlentyHandler yearOfPlentyHandler;
  private final ProgressMonopolyHandler monopolyHandler;

  public ProgressCardService(
      ProgressRoadBuildingHandler roadBuildingHandler,
      ProgressYearOfPlentyHandler yearOfPlentyHandler,
      ProgressMonopolyHandler monopolyHandler) {
    this.roadBuildingHandler = roadBuildingHandler;
    this.yearOfPlentyHandler = yearOfPlentyHandler;
    this.monopolyHandler = monopolyHandler;
  }

  public Map<String, Object> execute(
      GameRuntimeState state, UUID playerId, DevCardType cardType, Map<String, Object> payload) {
    return switch (cardType) {
      case ROAD_BUILDING -> {
        List<Integer> edges = parseEdges(payload);
        roadBuildingHandler.apply(state, playerId, edges);
        yield Map.of("applied", cardType.name(), "edges", edges);
      }
      case YEAR_OF_PLENTY -> {
        ResourceType resourceA = ResourceType.valueOf(String.valueOf(payload.get("resourceA")));
        ResourceType resourceB = ResourceType.valueOf(String.valueOf(payload.get("resourceB")));
        yearOfPlentyHandler.apply(state, playerId, resourceA, resourceB);
        yield Map.of("applied", cardType.name(), "resources", List.of(resourceA, resourceB));
      }
      case MONOPOLY -> {
        ResourceType resourceType =
            ResourceType.valueOf(String.valueOf(payload.get("resourceType")));
        int collected = monopolyHandler.apply(state, playerId, resourceType);
        yield Map.of("applied", cardType.name(), "resource", resourceType, "collected", collected);
      }
      default ->
          throw new ResponseStatusException(
              HttpStatus.UNPROCESSABLE_ENTITY, "Unsupported progress card type: " + cardType);
    };
  }

  private List<Integer> parseEdges(Map<String, Object> payload) {
    Object edgesObj = payload.get("edges");
    if (!(edgesObj instanceof List<?> rawEdges) || rawEdges.isEmpty() || rawEdges.size() > 2) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Road building requires one or two edges");
    }

    return rawEdges.stream().map(edge -> Integer.parseInt(String.valueOf(edge))).toList();
  }
}
