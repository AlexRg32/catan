package com.catan.server.game.projections;

import com.catan.server.game.engine.BoardPreset;
import java.util.UUID;

public record GameView(
    UUID gameId, String phase, UUID currentTurnPlayerId, long seed, BoardPreset board) {}
