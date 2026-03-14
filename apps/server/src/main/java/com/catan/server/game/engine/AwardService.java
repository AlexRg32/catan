package com.catan.server.game.engine;

import com.catan.server.game.engine.model.GameRuntimeState;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class AwardService {

  private final LongestRoadService longestRoadService;

  public AwardService(LongestRoadService longestRoadService) {
    this.longestRoadService = longestRoadService;
  }

  public Map<UUID, Integer> updateLongestRoad(GameRuntimeState state) {
    Map<UUID, Integer> lengths = new HashMap<>();
    for (UUID playerId : state.playerIds()) {
      lengths.put(playerId, longestRoadService.calculateLongestRoad(state, playerId));
    }

    UUID currentHolder = state.getLongestRoadHolderId();
    UUID bestPlayer = currentHolder;
    int bestLength = currentHolder == null ? 0 : lengths.getOrDefault(currentHolder, 0);

    for (UUID candidate : state.playerIds()) {
      int length = lengths.getOrDefault(candidate, 0);
      if (length >= 5 && length > bestLength) {
        bestLength = length;
        bestPlayer = candidate;
      }
    }

    state.setLongestRoadHolderId(bestPlayer);
    return lengths;
  }

  public Map<UUID, Integer> updateLargestArmy(GameRuntimeState state) {
    Map<UUID, Integer> counts = new HashMap<>();
    for (UUID playerId : state.playerIds()) {
      counts.put(playerId, state.playedKnightCount(playerId));
    }

    UUID currentHolder = state.getLargestArmyHolderId();
    UUID bestPlayer = currentHolder;
    int bestCount = currentHolder == null ? 0 : counts.getOrDefault(currentHolder, 0);

    for (UUID candidate : state.playerIds()) {
      int count = counts.getOrDefault(candidate, 0);
      if (count >= 3 && count > bestCount) {
        bestCount = count;
        bestPlayer = candidate;
      }
    }

    state.setLargestArmyHolderId(bestPlayer);
    return counts;
  }
}
