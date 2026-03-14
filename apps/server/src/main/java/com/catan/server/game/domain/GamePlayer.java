package com.catan.server.game.domain;

import com.catan.server.auth.domain.UserAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "game_players")
public class GamePlayer {

  @Id private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "game_id", nullable = false)
  private Game game;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserAccount user;

  @Column(nullable = false, length = 20)
  private String color;

  @Column(name = "victory_points", nullable = false)
  private int victoryPoints;

  @Column(name = "played_knights", nullable = false)
  private int playedKnights;

  @Column(name = "resources_summary", length = 1024)
  private String resourcesSummary;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Game getGame() {
    return game;
  }

  public void setGame(Game game) {
    this.game = game;
  }

  public UserAccount getUser() {
    return user;
  }

  public void setUser(UserAccount user) {
    this.user = user;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public int getVictoryPoints() {
    return victoryPoints;
  }

  public void setVictoryPoints(int victoryPoints) {
    this.victoryPoints = victoryPoints;
  }

  public int getPlayedKnights() {
    return playedKnights;
  }

  public void setPlayedKnights(int playedKnights) {
    this.playedKnights = playedKnights;
  }

  public String getResourcesSummary() {
    return resourcesSummary;
  }

  public void setResourcesSummary(String resourcesSummary) {
    this.resourcesSummary = resourcesSummary;
  }
}
