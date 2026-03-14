package com.catan.server.game.engine.model;

public class PortState {

  private final int index;
  private final int edgeIndex;
  private final int ratio;
  private final ResourceType resourceType;

  public PortState(int index, int edgeIndex, int ratio, ResourceType resourceType) {
    this.index = index;
    this.edgeIndex = edgeIndex;
    this.ratio = ratio;
    this.resourceType = resourceType;
  }

  public int getIndex() {
    return index;
  }

  public int getEdgeIndex() {
    return edgeIndex;
  }

  public int getRatio() {
    return ratio;
  }

  public ResourceType getResourceType() {
    return resourceType;
  }
}
