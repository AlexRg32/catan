package com.catan.server.game.projections;

import java.util.List;

public record LegalActionProjection(
    String actionType,
    boolean enabled,
    List<Integer> allowedNodeIndexes,
    List<Integer> allowedEdgeIndexes,
    List<Integer> allowedHexIndexes) {}
