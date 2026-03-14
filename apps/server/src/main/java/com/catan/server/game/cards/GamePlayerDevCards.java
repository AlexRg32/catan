package com.catan.server.game.cards;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class GamePlayerDevCards {

  private final List<OwnedDevCard> hand = new ArrayList<>();
  private final EnumMap<DevCardType, Integer> playedCounts = new EnumMap<>(DevCardType.class);

  public GamePlayerDevCards() {
    for (DevCardType type : DevCardType.values()) {
      playedCounts.put(type, 0);
    }
  }

  public void addCard(DevCardType type, int purchasedTurn) {
    hand.add(new OwnedDevCard(type, purchasedTurn));
  }

  public boolean canPlay(DevCardType type, int currentTurn) {
    return hand.stream()
        .anyMatch(card -> card.type() == type && card.purchasedTurn() < currentTurn);
  }

  public boolean play(DevCardType type, int currentTurn) {
    for (int i = 0; i < hand.size(); i++) {
      OwnedDevCard card = hand.get(i);
      if (card.type() == type && card.purchasedTurn() < currentTurn) {
        hand.remove(i);
        playedCounts.put(type, playedCounts.get(type) + 1);
        return true;
      }
    }
    return false;
  }

  public int handCount(DevCardType type) {
    return (int) hand.stream().filter(card -> card.type() == type).count();
  }

  public int totalHandCount() {
    return hand.size();
  }

  public EnumMap<DevCardType, Integer> handSnapshot() {
    EnumMap<DevCardType, Integer> snapshot = new EnumMap<>(DevCardType.class);
    for (DevCardType type : DevCardType.values()) {
      snapshot.put(type, handCount(type));
    }
    return snapshot;
  }

  public int hiddenVictoryPointCards() {
    return handCount(DevCardType.VICTORY_POINT);
  }

  public int playedCount(DevCardType type) {
    return playedCounts.get(type);
  }

  private record OwnedDevCard(DevCardType type, int purchasedTurn) {}
}
