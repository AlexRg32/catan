package com.catan.server.game.projections;

import java.util.List;

public record BoardProjection(
    List<BoardHexProjection> hexes,
    List<BoardIntersectionProjection> intersections,
    List<BoardEdgeProjection> edges,
    List<BoardPortProjection> ports) {}
