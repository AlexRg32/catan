package com.catan.server.game.projections;

import java.util.List;
import java.util.UUID;

public record BoardEdgeProjection(
    int edgeIndex,
    int nodeA,
    int nodeB,
    UUID ownerPlayerId,
    double x1,
    double y1,
    double z1,
    double x2,
    double y2,
    double z2,
    List<Integer> adjacentHexIndexes) {}
