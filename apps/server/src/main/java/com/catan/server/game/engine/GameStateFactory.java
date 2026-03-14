package com.catan.server.game.engine;

import com.catan.server.game.projections.GameView;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class GameStateFactory {

  private final BoardPresetBuilder boardPresetBuilder;

  public GameStateFactory(BoardPresetBuilder boardPresetBuilder) {
    this.boardPresetBuilder = boardPresetBuilder;
  }

  public GameView initial(UUID gameId, long seed, List<UUID> playerIds) {
    BoardPreset preset = boardPresetBuilder.buildBasePreset();
    return new GameView(
        gameId, "PRE_ROLL", playerIds.isEmpty() ? null : playerIds.getFirst(), seed, preset);
  }
}
