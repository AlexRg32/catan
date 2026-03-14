package com.catan.server.game.trade;

import com.catan.server.game.engine.model.ResourceType;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record TradeOffer(
    UUID offerId,
    UUID gameId,
    UUID fromPlayerId,
    Set<UUID> toPlayerIds,
    Map<ResourceType, Integer> give,
    Map<ResourceType, Integer> want,
    TradeOfferStatus status,
    UUID acceptedBy,
    Instant createdAt,
    Instant expiresAt) {

  public TradeOffer withStatus(TradeOfferStatus nextStatus, UUID acceptedByPlayer) {
    return new TradeOffer(
        offerId,
        gameId,
        fromPlayerId,
        toPlayerIds,
        give,
        want,
        nextStatus,
        acceptedByPlayer,
        createdAt,
        expiresAt);
  }
}
