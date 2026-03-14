package com.catan.server.game.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record CommandEnvelope(
    @NotBlank String commandId,
    @NotNull UUID gameId,
    @NotBlank String type,
    @NotNull Instant sentAt,
    Map<String, Object> payload) {}
