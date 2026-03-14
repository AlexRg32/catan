package com.catan.server.game.engine;

import com.catan.server.game.engine.model.GameRuntimeState;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Component;

@Component
public class GameRuntimeStore {

  private final ConcurrentMap<UUID, GameRuntimeState> states = new ConcurrentHashMap<>();
  private final GameRuntimeInitializer gameRuntimeInitializer;

  public GameRuntimeStore(GameRuntimeInitializer gameRuntimeInitializer) {
    this.gameRuntimeInitializer = gameRuntimeInitializer;
  }

  public GameRuntimeState getOrCreate(UUID gameId) {
    return states.computeIfAbsent(gameId, gameRuntimeInitializer::initialize);
  }

  public void put(GameRuntimeState state) {
    states.put(state.getGameId(), state);
  }
}
