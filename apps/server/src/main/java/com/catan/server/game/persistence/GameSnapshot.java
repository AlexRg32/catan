package com.catan.server.game.persistence;

import com.catan.server.game.domain.Game;
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
@Table(name = "game_snapshots")
public class GameSnapshot {

  @Id private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "game_id", nullable = false)
  private Game game;

  @Column(nullable = false)
  private long seq;

  @Column(name = "state_json", nullable = false, columnDefinition = "TEXT")
  private String stateJson;

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

  public long getSeq() {
    return seq;
  }

  public void setSeq(long seq) {
    this.seq = seq;
  }

  public String getStateJson() {
    return stateJson;
  }

  public void setStateJson(String stateJson) {
    this.stateJson = stateJson;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
