package com.catan.server.game.engine.model;

import java.util.Optional;

public enum TerrainType {
  WOOD(ResourceType.WOOD),
  WOOL(ResourceType.WOOL),
  GRAIN(ResourceType.GRAIN),
  CLAY(ResourceType.CLAY),
  ORE(ResourceType.ORE),
  DESERT(null);

  private final ResourceType producedResource;

  TerrainType(ResourceType producedResource) {
    this.producedResource = producedResource;
  }

  public Optional<ResourceType> producedResource() {
    return Optional.ofNullable(producedResource);
  }
}
