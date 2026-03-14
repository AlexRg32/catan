package com.catan.server.game.engine;

import com.catan.server.game.engine.model.ResourceType;
import java.util.Optional;
import java.util.UUID;

public record MoveRobberCommandResult(
    int previousHexIndex,
    int newHexIndex,
    UUID victimPlayerId,
    Optional<ResourceType> stolenResourceType) {}
