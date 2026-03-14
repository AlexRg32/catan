package com.catan.server.game.trade;

import com.catan.server.game.engine.model.GameRuntimeState;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class TradeCommandValidator {

  public void validatePropose(GameRuntimeState state, UUID actorUserId) {
    if (!actorUserId.equals(state.getActivePlayerId())) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Only active player can create trade offers");
    }
  }

  public void validateAnswer(TradeOffer offer, UUID actorUserId) {
    if (!offer.toPlayerIds().contains(actorUserId)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Only targeted players can answer this offer");
    }
    if (offer.status() != TradeOfferStatus.OPEN) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Trade offer is not open");
    }
  }
}
