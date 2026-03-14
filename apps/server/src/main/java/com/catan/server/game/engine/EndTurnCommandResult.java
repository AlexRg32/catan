package com.catan.server.game.engine;

import java.util.UUID;

public record EndTurnCommandResult(
    UUID nextActivePlayerId, int turnNumber, boolean finished, UUID winnerPlayerId) {}
