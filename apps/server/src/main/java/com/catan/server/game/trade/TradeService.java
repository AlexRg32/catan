package com.catan.server.game.trade;

import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.ResourceType;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class TradeService {

  private final TradeCommandValidator tradeCommandValidator;

  public TradeService(TradeCommandValidator tradeCommandValidator) {
    this.tradeCommandValidator = tradeCommandValidator;
  }

  public TradeOffer createOffer(
      GameRuntimeState state,
      UUID actorUserId,
      Set<UUID> toPlayerIds,
      Map<ResourceType, Integer> give,
      Map<ResourceType, Integer> want) {
    tradeCommandValidator.validatePropose(state, actorUserId);

    if (toPlayerIds == null || toPlayerIds.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Offer must have targets");
    }

    UUID offerId = UUID.randomUUID();
    TradeOffer offer =
        new TradeOffer(
            offerId,
            state.getGameId(),
            actorUserId,
            toPlayerIds,
            give,
            want,
            TradeOfferStatus.OPEN,
            null,
            Instant.now(),
            Instant.now().plusSeconds(60));

    state.addTradeOffer(offer);
    return offer;
  }
}
