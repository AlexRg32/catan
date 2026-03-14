package com.catan.server.game.dto;

import com.catan.server.game.projections.GameProjection;
import java.util.UUID;

public record GameSnapshotResponse(UUID gameId, long lastSequence, GameProjection state) {}
