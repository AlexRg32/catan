package com.catan.server.game.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.catan.server.game.engine.GameRuntimeFixtures;
import com.catan.server.game.engine.model.ResourceType;
import com.catan.server.game.trade.TradeCommandValidator;
import com.catan.server.game.trade.TradeOffer;
import com.catan.server.game.trade.TradeOfferStatus;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class SecurityIntegrationTest {

  @Test
  void nonActivePlayerCannotProposeTrade() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);

    TradeCommandValidator validator = new TradeCommandValidator();

    ResponseStatusException ex =
        assertThrows(ResponseStatusException.class, () -> validator.validatePropose(state, p2));
    assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
  }

  @Test
  void untargetedPlayerCannotAnswerTrade() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    UUID p3 = UUID.randomUUID();

    TradeOffer offer =
        new TradeOffer(
            UUID.randomUUID(),
            UUID.randomUUID(),
            p1,
            Set.of(p2),
            Map.of(ResourceType.WOOD, 1),
            Map.of(ResourceType.CLAY, 1),
            TradeOfferStatus.OPEN,
            null,
            Instant.now(),
            Instant.now().plusSeconds(60));

    TradeCommandValidator validator = new TradeCommandValidator();

    ResponseStatusException ex =
        assertThrows(ResponseStatusException.class, () -> validator.validateAnswer(offer, p3));
    assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
  }
}
