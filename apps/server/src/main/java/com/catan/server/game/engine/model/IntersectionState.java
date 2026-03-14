package com.catan.server.game.engine.model;

import java.util.List;
import java.util.UUID;

public class IntersectionState {

  private final int index;
  private final List<Integer> adjacentHexIndexes;
  private final List<Integer> adjacentIntersectionIndexes;
  private final double x;
  private final double y;
  private final double z;
  private UUID ownerPlayerId;
  private BuildingType buildingType;

  public IntersectionState(
      int index, List<Integer> adjacentHexIndexes, UUID ownerPlayerId, BuildingType buildingType) {
    this(index, adjacentHexIndexes, List.of(), ownerPlayerId, buildingType, 0.0, 0.0, 0.0);
  }

  public IntersectionState(
      int index,
      List<Integer> adjacentHexIndexes,
      List<Integer> adjacentIntersectionIndexes,
      UUID ownerPlayerId,
      BuildingType buildingType) {
    this(
        index,
        adjacentHexIndexes,
        adjacentIntersectionIndexes,
        ownerPlayerId,
        buildingType,
        0.0,
        0.0,
        0.0);
  }

  public IntersectionState(
      int index,
      List<Integer> adjacentHexIndexes,
      List<Integer> adjacentIntersectionIndexes,
      UUID ownerPlayerId,
      BuildingType buildingType,
      double x,
      double y,
      double z) {
    this.index = index;
    this.adjacentHexIndexes = adjacentHexIndexes;
    this.adjacentIntersectionIndexes = adjacentIntersectionIndexes;
    this.ownerPlayerId = ownerPlayerId;
    this.buildingType = buildingType;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public int getIndex() {
    return index;
  }

  public List<Integer> getAdjacentHexIndexes() {
    return adjacentHexIndexes;
  }

  public List<Integer> getAdjacentIntersectionIndexes() {
    return adjacentIntersectionIndexes;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getZ() {
    return z;
  }

  public UUID getOwnerPlayerId() {
    return ownerPlayerId;
  }

  public void setOwnerPlayerId(UUID ownerPlayerId) {
    this.ownerPlayerId = ownerPlayerId;
  }

  public BuildingType getBuildingType() {
    return buildingType;
  }

  public void setBuildingType(BuildingType buildingType) {
    this.buildingType = buildingType;
  }
}
