package com.catan.server.game.engine;

import com.catan.server.game.engine.model.ResourceType;
import com.catan.server.game.engine.model.SpecialFlow;
import java.util.Map;
import java.util.UUID;

public record RollDiceCommandResult(
    int roll,
    Map<UUID, Map<ResourceType, Integer>> production,
    Map<UUID, Integer> pendingDiscards,
    SpecialFlow specialFlow) {}
