package com.catan.server.game.engine.model;

public class HexTileState {

  private final int index;
  private final TerrainType terrain;
  private final Integer numberToken;
  private final double x;
  private final double y;
  private final double z;
  private boolean robber;

  public HexTileState(int index, TerrainType terrain, Integer numberToken, boolean robber) {
    this(index, terrain, numberToken, robber, 0.0, 0.0, 0.0);
  }

  public HexTileState(
      int index,
      TerrainType terrain,
      Integer numberToken,
      boolean robber,
      double x,
      double y,
      double z) {
    this.index = index;
    this.terrain = terrain;
    this.numberToken = numberToken;
    this.robber = robber;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public int getIndex() {
    return index;
  }

  public TerrainType getTerrain() {
    return terrain;
  }

  public Integer getNumberToken() {
    return numberToken;
  }

  public boolean hasRobber() {
    return robber;
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

  public void setRobber(boolean robber) {
    this.robber = robber;
  }
}
