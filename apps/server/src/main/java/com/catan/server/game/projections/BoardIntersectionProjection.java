package com.catan.server.game.projections;

import java.util.List;
import java.util.UUID;

public record BoardIntersectionProjection(
    int nodeIndex,
    List<Integer> adjacentHexIndexes,
    List<Integer> adjacentNodeIndexes,
    UUID ownerPlayerId,
    String buildingType,
    double x,
    double y,
    double z) {}
