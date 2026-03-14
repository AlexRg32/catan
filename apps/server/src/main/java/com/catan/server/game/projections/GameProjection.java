package com.catan.server.game.projections;

import java.util.List;
import java.util.UUID;

public record GameProjection(
    UUID gameId,
    String phase,
    String specialFlow,
    UUID activePlayerId,
    int turnNumber,
    int lastRoll,
    boolean setupCompleted,
    boolean finished,
    UUID winnerPlayerId,
    long lastSequence,
    BoardProjection board,
    List<LegalActionProjection> legalActions,
    List<GamePlayerProjection> players) {}
