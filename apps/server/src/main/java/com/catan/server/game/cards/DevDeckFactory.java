package com.catan.server.game.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class DevDeckFactory {

  public List<DevCardType> createShuffled(long seed) {
    List<DevCardType> deck = new ArrayList<>();

    addCopies(deck, DevCardType.KNIGHT, 14);
    addCopies(deck, DevCardType.ROAD_BUILDING, 2);
    addCopies(deck, DevCardType.YEAR_OF_PLENTY, 2);
    addCopies(deck, DevCardType.MONOPOLY, 2);
    addCopies(deck, DevCardType.VICTORY_POINT, 5);

    Collections.shuffle(deck, new Random(seed));
    return deck;
  }

  private void addCopies(List<DevCardType> deck, DevCardType type, int copies) {
    for (int i = 0; i < copies; i++) {
      deck.add(type);
    }
  }
}
