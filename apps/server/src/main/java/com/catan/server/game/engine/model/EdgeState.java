package com.catan.server.game.engine.model;

import java.util.UUID;

public class EdgeState {

  private final int index;
  private final int nodeA;
  private final int nodeB;
  private UUID ownerPlayerId;

  public EdgeState(int index, int nodeA, int nodeB, UUID ownerPlayerId) {
    this.index = index;
    this.nodeA = nodeA;
    this.nodeB = nodeB;
    this.ownerPlayerId = ownerPlayerId;
  }

  public int getIndex() {
    return index;
  }

  public int getNodeA() {
    return nodeA;
  }

  public int getNodeB() {
    return nodeB;
  }

  public UUID getOwnerPlayerId() {
    return ownerPlayerId;
  }

  public void setOwnerPlayerId(UUID ownerPlayerId) {
    this.ownerPlayerId = ownerPlayerId;
  }
}
