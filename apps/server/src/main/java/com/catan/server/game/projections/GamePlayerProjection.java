package com.catan.server.game.projections;

import com.catan.server.game.cards.DevCardType;
import com.catan.server.game.engine.model.ResourceType;
import java.util.Map;
import java.util.UUID;

public record GamePlayerProjection(
    UUID playerId,
    boolean self,
    int visibleVictoryPoints,
    int totalVictoryPoints,
    int resourceCount,
    Map<ResourceType, Integer> resources,
    int devCardCount,
    Map<DevCardType, Integer> devCards,
    int playedKnights,
    boolean hasLongestRoad,
    boolean hasLargestArmy,
    Integer hiddenVictoryPointCards) {}
