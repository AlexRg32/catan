package com.catan.server.game.engine;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BoardPresetBuilder {

  public BoardPreset buildBasePreset() {
    List<String> terrains =
        List.of(
            "WOOD", "WOOD", "WOOD", "WOOD", "WOOL", "WOOL", "WOOL", "WOOL", "GRAIN", "GRAIN",
            "GRAIN", "GRAIN", "CLAY", "CLAY", "CLAY", "ORE", "ORE", "ORE", "DESERT");

    List<Integer> numberTokens = List.of(2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12);

    List<String> ports =
        List.of("3:1", "3:1", "3:1", "3:1", "WOOD", "WOOL", "GRAIN", "CLAY", "ORE");

    return new BoardPreset(terrains, numberTokens, ports);
  }
}
