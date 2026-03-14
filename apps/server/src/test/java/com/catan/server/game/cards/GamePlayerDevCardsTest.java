package com.catan.server.game.cards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GamePlayerDevCardsTest {

  @Test
  void boughtCardCannotBePlayedSameTurn() {
    GamePlayerDevCards cards = new GamePlayerDevCards();
    cards.addCard(DevCardType.KNIGHT, 5);

    assertFalse(cards.canPlay(DevCardType.KNIGHT, 5));
    assertFalse(cards.play(DevCardType.KNIGHT, 5));

    assertTrue(cards.canPlay(DevCardType.KNIGHT, 6));
    assertTrue(cards.play(DevCardType.KNIGHT, 6));
    assertEquals(0, cards.handCount(DevCardType.KNIGHT));
    assertEquals(1, cards.playedCount(DevCardType.KNIGHT));
  }
}
