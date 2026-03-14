package com.catan.server.game.engine;

import com.catan.server.game.engine.model.EdgeState;
import com.catan.server.game.engine.model.IntersectionState;
import com.catan.server.game.engine.model.PortState;
import com.catan.server.game.engine.model.ResourceType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class BoardGraphFactory {

  private static final double SQRT_3 = Math.sqrt(3.0);
  private static final int[][] AXIAL_DIRECTIONS = {
    {1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}, {0, 1}
  };

  public BoardGraph create(BoardPreset preset) {
    List<HexGeometry> hexes = createHexGeometries(preset.terrains().size());

    Map<String, VertexBuilder> vertices = new HashMap<>();
    Map<String, EdgeBuilder> edges = new HashMap<>();
    for (HexGeometry hex : hexes) {
      List<String> cornerKeys = new ArrayList<>(6);
      for (int corner = 0; corner < 6; corner++) {
        Point point = cornerPoint(hex.x(), hex.y(), corner);
        String key = cornerKey(new AxialCoord(hex.q(), hex.r()), corner);
        cornerKeys.add(key);
        vertices
            .computeIfAbsent(key, ignored -> new VertexBuilder())
            .addPoint(point.x(), point.y(), hex.index());
      }

      for (int corner = 0; corner < 6; corner++) {
        String a = cornerKeys.get(corner);
        String b = cornerKeys.get((corner + 1) % 6);
        String key = edgeKey(a, b);
        EdgeBuilder edgeBuilder = edges.computeIfAbsent(key, ignored -> new EdgeBuilder(a, b));
        edgeBuilder.adjacentHexes.add(hex.index());
      }
    }

    List<Map.Entry<String, VertexBuilder>> sortedVertices =
        vertices.entrySet().stream()
            .sorted(
                Comparator.comparingDouble(
                        (Map.Entry<String, VertexBuilder> entry) -> entry.getValue().avgY())
                    .thenComparingDouble(entry -> entry.getValue().avgX()))
            .toList();

    Map<String, Integer> vertexIndexByKey = new HashMap<>();
    for (int index = 0; index < sortedVertices.size(); index++) {
      vertexIndexByKey.put(sortedVertices.get(index).getKey(), index);
    }

    List<Map.Entry<String, EdgeBuilder>> sortedEdges =
        edges.entrySet().stream()
            .sorted(
                Comparator.comparingInt(
                        (Map.Entry<String, EdgeBuilder> entry) ->
                            minNodeIndex(entry, vertexIndexByKey))
                    .thenComparingInt(entry -> maxNodeIndex(entry, vertexIndexByKey)))
            .toList();

    Map<Integer, Set<Integer>> adjacentNodes = new HashMap<>();
    for (Map.Entry<String, EdgeBuilder> entry : sortedEdges) {
      int nodeA = vertexIndexByKey.get(entry.getValue().nodeAKey);
      int nodeB = vertexIndexByKey.get(entry.getValue().nodeBKey);
      adjacentNodes.computeIfAbsent(nodeA, ignored -> new HashSet<>()).add(nodeB);
      adjacentNodes.computeIfAbsent(nodeB, ignored -> new HashSet<>()).add(nodeA);
    }

    List<IntersectionState> intersections = new ArrayList<>();
    for (int index = 0; index < sortedVertices.size(); index++) {
      VertexBuilder vertex = sortedVertices.get(index).getValue();
      List<Integer> adjacentHexes = vertex.hexes.stream().sorted().toList();
      List<Integer> adjacentIntersectionIndexes =
          adjacentNodes.getOrDefault(index, Set.of()).stream().sorted().toList();
      intersections.add(
          new IntersectionState(
              index,
              adjacentHexes,
              adjacentIntersectionIndexes,
              null,
              null,
              vertex.avgX(),
              vertex.avgY(),
              0.0));
    }

    List<EdgeGeometry> edgeGeometries = new ArrayList<>();
    List<EdgeState> edgeStates = new ArrayList<>();
    for (int index = 0; index < sortedEdges.size(); index++) {
      EdgeBuilder edge = sortedEdges.get(index).getValue();
      int nodeA = vertexIndexByKey.get(edge.nodeAKey);
      int nodeB = vertexIndexByKey.get(edge.nodeBKey);

      int canonicalNodeA = Math.min(nodeA, nodeB);
      int canonicalNodeB = Math.max(nodeA, nodeB);
      edgeStates.add(new EdgeState(index, canonicalNodeA, canonicalNodeB, null));

      VertexBuilder a = vertices.get(edge.nodeAKey);
      VertexBuilder b = vertices.get(edge.nodeBKey);
      edgeGeometries.add(
          new EdgeGeometry(
              index,
              (a.avgX() + b.avgX()) / 2.0,
              (a.avgY() + b.avgY()) / 2.0,
              edge.adjacentHexes.stream().sorted().toList()));
    }

    List<PortState> ports = assignPorts(preset.ports(), edgeGeometries);
    return new BoardGraph(hexes, intersections, edgeStates, ports);
  }

  private List<HexGeometry> createHexGeometries(int expectedHexes) {
    List<HexGeometry> hexes = new ArrayList<>();
    int index = 0;
    int radius = 2;

    for (int r = -radius; r <= radius; r++) {
      int qMin = Math.max(-radius, -r - radius);
      int qMax = Math.min(radius, -r + radius);
      for (int q = qMin; q <= qMax; q++) {
        double x = SQRT_3 * (q + r / 2.0);
        double y = 1.5 * r;
        hexes.add(new HexGeometry(index++, q, r, x, y, 0.0));
      }
    }

    if (hexes.size() != expectedHexes) {
      throw new IllegalStateException(
          "Hex geometry mismatch: expected " + expectedHexes + " but got " + hexes.size());
    }

    return hexes;
  }

  private List<PortState> assignPorts(List<String> portTypes, List<EdgeGeometry> edges) {
    List<EdgeGeometry> coastalEdges =
        edges.stream()
            .filter(edge -> edge.adjacentHexes().size() == 1)
            .sorted(Comparator.comparingDouble(edge -> Math.atan2(edge.midY(), edge.midX())))
            .toList();

    List<PortState> ports = new ArrayList<>();
    if (coastalEdges.isEmpty() || portTypes.isEmpty()) {
      return ports;
    }

    for (int portIndex = 0; portIndex < portTypes.size(); portIndex++) {
      int edgeIndex = (portIndex * coastalEdges.size()) / portTypes.size();
      EdgeGeometry targetEdge = coastalEdges.get(edgeIndex);

      String raw = portTypes.get(portIndex);
      int ratio = "3:1".equals(raw) ? 3 : 2;
      ResourceType resourceType =
          "3:1".equals(raw) ? null : ResourceType.valueOf(raw.toUpperCase(Locale.ROOT));
      ports.add(new PortState(portIndex, targetEdge.edgeIndex(), ratio, resourceType));
    }

    return ports;
  }

  private static String cornerKey(AxialCoord center, int corner) {
    AxialCoord dirA = AxialCoord.fromDirection(AXIAL_DIRECTIONS[corner]);
    AxialCoord dirB = AxialCoord.fromDirection(AXIAL_DIRECTIONS[(corner + 5) % 6]);

    return List.of(center, center.add(dirA), center.add(dirB)).stream()
        .map(AxialCoord::key)
        .sorted()
        .collect(Collectors.joining("|"));
  }

  private static int minNodeIndex(
      Map.Entry<String, EdgeBuilder> entry, Map<String, Integer> vertexIndexByKey) {
    int nodeA = vertexIndexByKey.get(entry.getValue().nodeAKey);
    int nodeB = vertexIndexByKey.get(entry.getValue().nodeBKey);
    return Math.min(nodeA, nodeB);
  }

  private static int maxNodeIndex(
      Map.Entry<String, EdgeBuilder> entry, Map<String, Integer> vertexIndexByKey) {
    int nodeA = vertexIndexByKey.get(entry.getValue().nodeAKey);
    int nodeB = vertexIndexByKey.get(entry.getValue().nodeBKey);
    return Math.max(nodeA, nodeB);
  }

  private static Point cornerPoint(double centerX, double centerY, int corner) {
    double angle = Math.toRadians(60.0 * corner - 30.0);
    return new Point(centerX + Math.cos(angle), centerY + Math.sin(angle));
  }

  private static String edgeKey(String a, String b) {
    return a.compareTo(b) <= 0 ? a + "|" + b : b + "|" + a;
  }

  public record BoardGraph(
      List<HexGeometry> hexes,
      List<IntersectionState> intersections,
      List<EdgeState> edges,
      List<PortState> ports) {}

  public record HexGeometry(int index, int q, int r, double x, double y, double z) {}

  private record Point(double x, double y) {}

  private record EdgeGeometry(
      int edgeIndex, double midX, double midY, List<Integer> adjacentHexes) {}

  private record AxialCoord(int q, int r) {
    private static AxialCoord fromDirection(int[] direction) {
      return new AxialCoord(direction[0], direction[1]);
    }

    private AxialCoord add(AxialCoord other) {
      return new AxialCoord(q + other.q, r + other.r);
    }

    private String key() {
      return q + ":" + r;
    }
  }

  private static final class VertexBuilder {
    private final Set<Integer> hexes = new HashSet<>();
    private double xSum = 0.0;
    private double ySum = 0.0;
    private int pointCount = 0;

    private void addPoint(double x, double y, int hexIndex) {
      this.xSum += x;
      this.ySum += y;
      this.pointCount += 1;
      this.hexes.add(hexIndex);
    }

    private double avgX() {
      return xSum / pointCount;
    }

    private double avgY() {
      return ySum / pointCount;
    }
  }

  private static final class EdgeBuilder {
    private final String nodeAKey;
    private final String nodeBKey;
    private final Set<Integer> adjacentHexes = new HashSet<>();

    private EdgeBuilder(String nodeAKey, String nodeBKey) {
      this.nodeAKey = nodeAKey;
      this.nodeBKey = nodeBKey;
    }
  }
}
