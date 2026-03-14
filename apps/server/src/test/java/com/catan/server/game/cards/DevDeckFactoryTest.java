package com.catan.server.game.cards;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class DevDeckFactoryTest {

  private final DevDeckFactory factory = new DevDeckFactory();

  @Test
  void createsExpectedComposition() {
    List<DevCardType> deck = factory.createShuffled(123L);

    assertEquals(25, deck.size());
    assertEquals(14, deck.stream().filter(card -> card == DevCardType.KNIGHT).count());
    assertEquals(2, deck.stream().filter(card -> card == DevCardType.ROAD_BUILDING).count());
    assertEquals(2, deck.stream().filter(card -> card == DevCardType.YEAR_OF_PLENTY).count());
    assertEquals(2, deck.stream().filter(card -> card == DevCardType.MONOPOLY).count());
    assertEquals(5, deck.stream().filter(card -> card == DevCardType.VICTORY_POINT).count());
  }
}
