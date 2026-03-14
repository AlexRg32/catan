package com.catan.server.game.dto;

import java.time.Instant;
import java.util.UUID;

public record GameEventResponse(
    long seq, String type, UUID actorPlayerId, String payload, Instant createdAt) {}
