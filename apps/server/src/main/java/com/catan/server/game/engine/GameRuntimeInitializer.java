package com.catan.server.game.engine;

import com.catan.server.game.cards.DevCardType;
import com.catan.server.game.cards.DevDeckFactory;
import com.catan.server.game.domain.Game;
import com.catan.server.game.domain.GamePlayer;
import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.HexTileState;
import com.catan.server.game.engine.model.TerrainType;
import com.catan.server.game.engine.model.TurnPhase;
import com.catan.server.game.repository.GamePlayerRepository;
import com.catan.server.game.repository.GameRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class GameRuntimeInitializer {

  private final GameRepository gameRepository;
  private final GamePlayerRepository gamePlayerRepository;
  private final BoardPresetBuilder boardPresetBuilder;
  private final BoardGraphFactory boardGraphFactory;
  private final DevDeckFactory devDeckFactory;

  public GameRuntimeInitializer(
      GameRepository gameRepository,
      GamePlayerRepository gamePlayerRepository,
      BoardPresetBuilder boardPresetBuilder,
      BoardGraphFactory boardGraphFactory,
      DevDeckFactory devDeckFactory) {
    this.gameRepository = gameRepository;
    this.gamePlayerRepository = gamePlayerRepository;
    this.boardPresetBuilder = boardPresetBuilder;
    this.boardGraphFactory = boardGraphFactory;
    this.devDeckFactory = devDeckFactory;
  }

  public GameRuntimeState initialize(UUID gameId) {
    Game game =
        gameRepository
            .findById(gameId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

    List<GamePlayer> players = gamePlayerRepository.findByGameOrderByCreatedAtAsc(game);
    if (players.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Game has no players");
    }

    List<UUID> playerIds =
        players.stream().map(gamePlayer -> gamePlayer.getUser().getId()).toList();

    BoardPreset preset = boardPresetBuilder.buildBasePreset();
    BoardGraphFactory.BoardGraph boardGraph = boardGraphFactory.create(preset);
    List<HexTileState> hexTiles = new ArrayList<>();

    int numberIndex = 0;
    for (BoardGraphFactory.HexGeometry hexGeometry : boardGraph.hexes()) {
      int i = hexGeometry.index();
      TerrainType terrainType = TerrainType.valueOf(preset.terrains().get(i));
      Integer numberToken = null;
      if (terrainType != TerrainType.DESERT) {
        numberToken = preset.numberTokens().get(numberIndex++);
      }
      boolean robber = terrainType == TerrainType.DESERT;
      hexTiles.add(
          new HexTileState(
              i,
              terrainType,
              numberToken,
              robber,
              hexGeometry.x(),
              hexGeometry.y(),
              hexGeometry.z()));
    }

    UUID activePlayerId =
        game.getCurrentTurnPlayer() != null
            ? game.getCurrentTurnPlayer().getId()
            : playerIds.getFirst();

    TurnPhase phase = parsePhase(game.getPhase());
    long seed = game.getSeed() != null ? game.getSeed() : System.nanoTime();
    List<DevCardType> deck = devDeckFactory.createShuffled(seed);

    return new GameRuntimeState(
        gameId,
        seed,
        activePlayerId,
        phase,
        playerIds,
        hexTiles,
        boardGraph.intersections(),
        boardGraph.edges(),
        boardGraph.ports(),
        deck);
  }

  private TurnPhase parsePhase(String value) {
    if (value == null || value.isBlank()) {
      return TurnPhase.PRE_ROLL;
    }
    try {
      return TurnPhase.valueOf(value);
    } catch (IllegalArgumentException ignored) {
      return TurnPhase.PRE_ROLL;
    }
  }
}
