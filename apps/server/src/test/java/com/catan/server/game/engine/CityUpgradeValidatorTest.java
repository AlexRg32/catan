package com.catan.server.game.engine;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class CityUpgradeValidatorTest {

  private final CityUpgradeValidator validator = new CityUpgradeValidator();

  @Test
  void allowsOwnSettlementUpgrade() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.graphState(p1, p2);

    assertDoesNotThrow(() -> validator.validate(state, p1, 0));
  }

  @Test
  void rejectsOpponentSettlementUpgrade() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.graphState(p1, p2);

    assertThrows(ResponseStatusException.class, () -> validator.validate(state, p1, 2));
  }
}
