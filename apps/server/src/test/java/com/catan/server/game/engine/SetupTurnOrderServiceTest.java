package com.catan.server.game.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SetupTurnOrderServiceTest {

  @Test
  void snakeOrderBuildsForwardThenReverseSequence() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    UUID p3 = UUID.randomUUID();
    UUID p4 = UUID.randomUUID();

    SetupTurnOrderService service = new SetupTurnOrderService();

    List<UUID> order = service.snakeOrder(List.of(p1, p2, p3, p4));

    assertEquals(List.of(p1, p2, p3, p4, p4, p3, p2, p1), order);
  }
}
