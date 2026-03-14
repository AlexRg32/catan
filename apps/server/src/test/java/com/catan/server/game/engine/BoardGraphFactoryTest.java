package com.catan.server.game.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class BoardGraphFactoryTest {

  @Test
  void createsCanonicalBaseBoardGraph() {
    BoardGraphFactory factory = new BoardGraphFactory();
    BoardPreset preset = new BoardPresetBuilder().buildBasePreset();

    BoardGraphFactory.BoardGraph graph = factory.create(preset);

    assertEquals(19, graph.hexes().size());
    assertEquals(54, graph.intersections().size());
    assertEquals(72, graph.edges().size());
    assertEquals(9, graph.ports().size());

    Set<Integer> portEdges =
        graph.ports().stream().map(port -> port.getEdgeIndex()).collect(Collectors.toSet());
    assertEquals(9, portEdges.size());
    assertFalse(graph.intersections().isEmpty());
    assertFalse(graph.edges().isEmpty());
    assertTrue(
        graph.intersections().stream().allMatch(node -> !node.getAdjacentHexIndexes().isEmpty()));
  }
}
