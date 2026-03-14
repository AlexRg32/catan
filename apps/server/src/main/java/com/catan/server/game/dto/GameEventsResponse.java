package com.catan.server.game.dto;

import java.util.List;
import java.util.UUID;

public record GameEventsResponse(
    UUID gameId, long fromSequence, long lastSequence, List<GameEventResponse> events) {}
