package com.catan.server.game.projections;

public record BoardHexProjection(
    int hexIndex,
    String terrain,
    Integer numberToken,
    boolean hasRobber,
    double x,
    double y,
    double z) {}
