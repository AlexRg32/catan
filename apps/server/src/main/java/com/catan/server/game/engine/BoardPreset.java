package com.catan.server.game.engine;

import java.util.List;

public record BoardPreset(List<String> terrains, List<Integer> numberTokens, List<String> ports) {}
