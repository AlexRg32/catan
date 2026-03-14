package com.catan.server.game.trade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.catan.server.game.engine.GameRuntimeFixtures;
import com.catan.server.game.engine.model.ResourceType;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class TradeHandlersTest {

  @Test
  void onlyActivePlayerCanProposeTrade() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);

    TradeService tradeService = new TradeService(new TradeCommandValidator());

    assertThrows(
        ResponseStatusException.class,
        () -> tradeService.createOffer(state, p2, Set.of(p1), Map.of(), Map.of()));
  }

  @Test
  void onlyTargetedPlayerCanAnswerTrade() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    UUID p3 = UUID.randomUUID();
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);
    state.setActivePlayerId(p1);

    TradeService tradeService = new TradeService(new TradeCommandValidator());
    TradeOffer offer =
        tradeService.createOffer(
            state, p1, Set.of(p2), Map.of(ResourceType.WOOD, 1), Map.of(ResourceType.CLAY, 1));

    AnswerTradeHandler answerTradeHandler = new AnswerTradeHandler(new TradeCommandValidator());

    assertThrows(
        ResponseStatusException.class,
        () -> answerTradeHandler.handle(state, p3, offer.offerId(), true));
  }

  @Test
  void acceptedTradeTransfersCards() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);
    state.setActivePlayerId(p1);
    state.addResource(p1, ResourceType.WOOD, 1);
    state.addResource(p2, ResourceType.CLAY, 1);

    TradeService tradeService = new TradeService(new TradeCommandValidator());
    TradeOffer offer =
        tradeService.createOffer(
            state, p1, Set.of(p2), Map.of(ResourceType.WOOD, 1), Map.of(ResourceType.CLAY, 1));

    AnswerTradeHandler answerTradeHandler = new AnswerTradeHandler(new TradeCommandValidator());
    TradeOffer updated = answerTradeHandler.handle(state, p2, offer.offerId(), true);

    assertEquals(TradeOfferStatus.ACCEPTED, updated.status());
    assertEquals(0, state.getResourceCount(p1, ResourceType.WOOD));
    assertEquals(1, state.getResourceCount(p1, ResourceType.CLAY));
    assertEquals(1, state.getResourceCount(p2, ResourceType.WOOD));
    assertEquals(0, state.getResourceCount(p2, ResourceType.CLAY));
  }
}
