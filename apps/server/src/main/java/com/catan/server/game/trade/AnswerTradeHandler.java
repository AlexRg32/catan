package com.catan.server.game.trade;

import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.ResourceType;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AnswerTradeHandler {

  private final TradeCommandValidator tradeCommandValidator;

  public AnswerTradeHandler(TradeCommandValidator tradeCommandValidator) {
    this.tradeCommandValidator = tradeCommandValidator;
  }

  public TradeOffer handle(GameRuntimeState state, UUID actorUserId, UUID offerId, boolean accept) {
    TradeOffer offer =
        state
            .getTradeOffer(offerId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found"));

    tradeCommandValidator.validateAnswer(offer, actorUserId);

    TradeOffer updated;
    if (accept) {
      updated = offer.withStatus(TradeOfferStatus.ACCEPTED, actorUserId);
      executeTrade(state, updated);
    } else {
      updated = offer.withStatus(TradeOfferStatus.REJECTED, null);
    }

    state.addTradeOffer(updated);
    return updated;
  }

  private void executeTrade(GameRuntimeState state, TradeOffer offer) {
    UUID from = offer.fromPlayerId();
    UUID to = offer.acceptedBy();
    if (to == null) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Accepted player is missing");
    }

    transfer(state, from, to, offer.give());
    transfer(state, to, from, offer.want());
  }

  private void transfer(
      GameRuntimeState state,
      UUID fromPlayerId,
      UUID toPlayerId,
      Map<ResourceType, Integer> cards) {
    for (Map.Entry<ResourceType, Integer> entry : cards.entrySet()) {
      int available = state.getResourceCount(fromPlayerId, entry.getKey());
      if (available < entry.getValue()) {
        throw new ResponseStatusException(
            HttpStatus.UNPROCESSABLE_ENTITY, "Insufficient cards for trade");
      }
    }
    for (Map.Entry<ResourceType, Integer> entry : cards.entrySet()) {
      if (entry.getValue() > 0) {
        state.removeResource(fromPlayerId, entry.getKey(), entry.getValue());
        state.addResource(toPlayerId, entry.getKey(), entry.getValue());
      }
    }
  }
}
