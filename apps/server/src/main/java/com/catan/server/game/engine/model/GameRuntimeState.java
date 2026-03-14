package com.catan.server.game.engine.model;

import com.catan.server.game.cards.DevCardType;
import com.catan.server.game.cards.GamePlayerDevCards;
import com.catan.server.game.trade.TradeOffer;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class GameRuntimeState {

  private final UUID gameId;
  private final Random random;
  private final List<UUID> orderedPlayerIds;
  private final Map<UUID, EnumMap<ResourceType, Integer>> resourcesByPlayer;
  private final List<HexTileState> hexTiles;
  private final List<IntersectionState> intersections;
  private final List<EdgeState> edges;
  private final List<PortState> ports;
  private final Map<UUID, Integer> pendingDiscards;
  private final Map<UUID, GamePlayerDevCards> devCardsByPlayer;
  private final List<DevCardType> developmentDeck;
  private final Map<UUID, Integer> playedKnightCountByPlayer;
  private final Map<UUID, Integer> visibleVictoryPointsByPlayer;
  private final Map<UUID, Boolean> genericPortByPlayer;
  private final Map<UUID, Set<ResourceType>> specificPortsByPlayer;
  private final Map<UUID, Integer> setupSettlementsPlacedByPlayer;
  private final Map<UUID, TradeOffer> tradeOffers;

  private UUID activePlayerId;
  private UUID longestRoadHolderId;
  private UUID largestArmyHolderId;
  private UUID winnerPlayerId;
  private TurnPhase phase;
  private SpecialFlow specialFlow;
  private List<UUID> setupTurnOrder;
  private int setupTurnIndex;
  private boolean setupAwaitingRoadPlacement;
  private Integer setupPendingSettlementNodeIndex;
  private boolean setupCompleted;
  private boolean finished;
  private int lastRoll;
  private int turnNumber;

  public GameRuntimeState(
      UUID gameId,
      long seed,
      UUID activePlayerId,
      TurnPhase phase,
      List<UUID> playerIds,
      List<HexTileState> hexTiles,
      List<IntersectionState> intersections) {
    this(
        gameId,
        seed,
        activePlayerId,
        phase,
        playerIds,
        hexTiles,
        intersections,
        List.of(),
        List.of(),
        List.of());
  }

  public GameRuntimeState(
      UUID gameId,
      long seed,
      UUID activePlayerId,
      TurnPhase phase,
      List<UUID> playerIds,
      List<HexTileState> hexTiles,
      List<IntersectionState> intersections,
      List<EdgeState> edges) {
    this(
        gameId,
        seed,
        activePlayerId,
        phase,
        playerIds,
        hexTiles,
        intersections,
        edges,
        List.of(),
        List.of());
  }

  public GameRuntimeState(
      UUID gameId,
      long seed,
      UUID activePlayerId,
      TurnPhase phase,
      List<UUID> playerIds,
      List<HexTileState> hexTiles,
      List<IntersectionState> intersections,
      List<EdgeState> edges,
      List<PortState> ports) {
    this(
        gameId,
        seed,
        activePlayerId,
        phase,
        playerIds,
        hexTiles,
        intersections,
        edges,
        ports,
        List.of());
  }

  public GameRuntimeState(
      UUID gameId,
      long seed,
      UUID activePlayerId,
      TurnPhase phase,
      List<UUID> playerIds,
      List<HexTileState> hexTiles,
      List<IntersectionState> intersections,
      List<EdgeState> edges,
      List<PortState> ports,
      List<DevCardType> developmentDeck) {
    this.gameId = gameId;
    this.random = new Random(seed);
    this.orderedPlayerIds = new ArrayList<>(playerIds);
    this.activePlayerId = activePlayerId;
    this.longestRoadHolderId = null;
    this.largestArmyHolderId = null;
    this.winnerPlayerId = null;
    this.phase = phase;
    this.specialFlow = SpecialFlow.NONE;
    this.setupTurnOrder = List.of();
    this.setupTurnIndex = 0;
    this.setupAwaitingRoadPlacement = false;
    this.setupPendingSettlementNodeIndex = null;
    this.setupCompleted = true;
    this.finished = false;
    this.lastRoll = 0;
    this.turnNumber = 1;
    this.resourcesByPlayer = new HashMap<>();
    this.hexTiles = new ArrayList<>(hexTiles);
    this.intersections = new ArrayList<>(intersections);
    this.edges = new ArrayList<>(edges);
    this.ports = new ArrayList<>(ports);
    this.pendingDiscards = new HashMap<>();
    this.devCardsByPlayer = new HashMap<>();
    this.developmentDeck = new ArrayList<>(developmentDeck);
    this.playedKnightCountByPlayer = new HashMap<>();
    this.visibleVictoryPointsByPlayer = new HashMap<>();
    this.genericPortByPlayer = new HashMap<>();
    this.specificPortsByPlayer = new HashMap<>();
    this.setupSettlementsPlacedByPlayer = new HashMap<>();
    this.tradeOffers = new HashMap<>();

    for (UUID playerId : orderedPlayerIds) {
      EnumMap<ResourceType, Integer> initial = new EnumMap<>(ResourceType.class);
      for (ResourceType resourceType : ResourceType.values()) {
        initial.put(resourceType, 0);
      }
      resourcesByPlayer.put(playerId, initial);
      devCardsByPlayer.put(playerId, new GamePlayerDevCards());
      playedKnightCountByPlayer.put(playerId, 0);
      visibleVictoryPointsByPlayer.put(playerId, 0);
      genericPortByPlayer.put(playerId, false);
      specificPortsByPlayer.put(playerId, new HashSet<>());
      setupSettlementsPlacedByPlayer.put(playerId, 0);
    }
  }

  public UUID getGameId() {
    return gameId;
  }

  public UUID getActivePlayerId() {
    return activePlayerId;
  }

  public void setActivePlayerId(UUID activePlayerId) {
    this.activePlayerId = activePlayerId;
  }

  public UUID getLongestRoadHolderId() {
    return longestRoadHolderId;
  }

  public void setLongestRoadHolderId(UUID longestRoadHolderId) {
    this.longestRoadHolderId = longestRoadHolderId;
  }

  public UUID getLargestArmyHolderId() {
    return largestArmyHolderId;
  }

  public void setLargestArmyHolderId(UUID largestArmyHolderId) {
    this.largestArmyHolderId = largestArmyHolderId;
  }

  public UUID getWinnerPlayerId() {
    return winnerPlayerId;
  }

  public void setWinnerPlayerId(UUID winnerPlayerId) {
    this.winnerPlayerId = winnerPlayerId;
  }

  public TurnPhase getPhase() {
    return phase;
  }

  public void setPhase(TurnPhase phase) {
    this.phase = phase;
  }

  public SpecialFlow getSpecialFlow() {
    return specialFlow;
  }

  public void setSpecialFlow(SpecialFlow specialFlow) {
    this.specialFlow = specialFlow;
  }

  public boolean isSetupCompleted() {
    return setupCompleted;
  }

  public void setSetupCompleted(boolean setupCompleted) {
    this.setupCompleted = setupCompleted;
  }

  public boolean isFinished() {
    return finished;
  }

  public void setFinished(boolean finished) {
    this.finished = finished;
  }

  public int getLastRoll() {
    return lastRoll;
  }

  public void setLastRoll(int lastRoll) {
    this.lastRoll = lastRoll;
  }

  public int getTurnNumber() {
    return turnNumber;
  }

  public void setTurnNumber(int turnNumber) {
    this.turnNumber = turnNumber;
  }

  public void incrementTurnNumber() {
    this.turnNumber += 1;
  }

  public List<HexTileState> getHexTiles() {
    return hexTiles;
  }

  public List<IntersectionState> getIntersections() {
    return intersections;
  }

  public List<EdgeState> getEdges() {
    return edges;
  }

  public List<PortState> getPorts() {
    return ports;
  }

  public Map<UUID, Integer> getPendingDiscards() {
    return pendingDiscards;
  }

  public List<UUID> playerIds() {
    return List.copyOf(orderedPlayerIds);
  }

  public int rollDie() {
    return random.nextInt(6) + 1;
  }

  public void addResource(UUID playerId, ResourceType resourceType, int amount) {
    int current = getResourceCount(playerId, resourceType);
    resourcesByPlayer.get(playerId).put(resourceType, current + amount);
  }

  public int getResourceCount(UUID playerId, ResourceType resourceType) {
    return resourcesByPlayer
        .getOrDefault(playerId, new EnumMap<>(ResourceType.class))
        .getOrDefault(resourceType, 0);
  }

  public int getTotalResources(UUID playerId) {
    EnumMap<ResourceType, Integer> cards = resourcesByPlayer.get(playerId);
    if (cards == null) {
      return 0;
    }
    return cards.values().stream().mapToInt(Integer::intValue).sum();
  }

  public void removeResource(UUID playerId, ResourceType resourceType, int amount) {
    int current = getResourceCount(playerId, resourceType);
    if (current < amount) {
      throw new IllegalArgumentException("Not enough resources to remove");
    }
    resourcesByPlayer.get(playerId).put(resourceType, current - amount);
  }

  public Map<ResourceType, Integer> resourcesSnapshot(UUID playerId) {
    EnumMap<ResourceType, Integer> source = resourcesByPlayer.get(playerId);
    EnumMap<ResourceType, Integer> copy = new EnumMap<>(ResourceType.class);
    if (source == null) {
      for (ResourceType type : ResourceType.values()) {
        copy.put(type, 0);
      }
      return copy;
    }
    copy.putAll(source);
    return copy;
  }

  public boolean hasEdge(int edgeIndex) {
    return edges.stream().anyMatch(edge -> edge.getIndex() == edgeIndex);
  }

  public void setEdgeOwner(int edgeIndex, UUID ownerPlayerId) {
    EdgeState edge = edge(edgeIndex);
    edge.setOwnerPlayerId(ownerPlayerId);
  }

  public void setIntersectionOwner(int intersectionIndex, UUID ownerPlayerId, BuildingType type) {
    IntersectionState intersection = intersection(intersectionIndex);
    intersection.setOwnerPlayerId(ownerPlayerId);
    intersection.setBuildingType(type);
  }

  public IntersectionState intersection(int intersectionIndex) {
    return intersections.stream()
        .filter(candidate -> candidate.getIndex() == intersectionIndex)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Intersection not found"));
  }

  public Optional<IntersectionState> findIntersection(int intersectionIndex) {
    return intersections.stream()
        .filter(candidate -> candidate.getIndex() == intersectionIndex)
        .findFirst();
  }

  public EdgeState edge(int edgeIndex) {
    return edges.stream()
        .filter(candidate -> candidate.getIndex() == edgeIndex)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Edge not found"));
  }

  public Optional<EdgeState> findEdge(int edgeIndex) {
    return edges.stream().filter(candidate -> candidate.getIndex() == edgeIndex).findFirst();
  }

  public Optional<HexTileState> findHex(int hexIndex) {
    return hexTiles.stream().filter(candidate -> candidate.getIndex() == hexIndex).findFirst();
  }

  public GamePlayerDevCards devCards(UUID playerId) {
    return devCardsByPlayer.get(playerId);
  }

  public void addDevCardToPlayer(UUID playerId, DevCardType type, int purchasedTurn) {
    devCards(playerId).addCard(type, purchasedTurn);
  }

  public int remainingDevelopmentCards() {
    return developmentDeck.size();
  }

  public DevCardType drawDevelopmentCard() {
    if (developmentDeck.isEmpty()) {
      return null;
    }
    return developmentDeck.removeFirst();
  }

  public boolean playDevCard(UUID playerId, DevCardType type, int currentTurn) {
    return devCards(playerId).play(type, currentTurn);
  }

  public int hiddenVictoryPointCards(UUID playerId) {
    return devCards(playerId).hiddenVictoryPointCards();
  }

  public int playedKnightCount(UUID playerId) {
    return playedKnightCountByPlayer.getOrDefault(playerId, 0);
  }

  public void incrementPlayedKnightCount(UUID playerId) {
    playedKnightCountByPlayer.put(playerId, playedKnightCount(playerId) + 1);
  }

  public int visibleVictoryPoints(UUID playerId) {
    return visibleVictoryPointsByPlayer.getOrDefault(playerId, 0);
  }

  public void setVisibleVictoryPoints(UUID playerId, int points) {
    visibleVictoryPointsByPlayer.put(playerId, points);
  }

  public void addVisibleVictoryPoints(UUID playerId, int delta) {
    visibleVictoryPointsByPlayer.put(playerId, visibleVictoryPoints(playerId) + delta);
  }

  public int totalVictoryPoints(UUID playerId) {
    int points = visibleVictoryPoints(playerId);
    points += hiddenVictoryPointCards(playerId);
    if (playerId.equals(longestRoadHolderId)) {
      points += 2;
    }
    if (playerId.equals(largestArmyHolderId)) {
      points += 2;
    }
    return points;
  }

  public void grantGenericPort(UUID playerId) {
    genericPortByPlayer.put(playerId, true);
  }

  public boolean hasGenericPort(UUID playerId) {
    return genericPortByPlayer.getOrDefault(playerId, false);
  }

  public void grantSpecificPort(UUID playerId, ResourceType resourceType) {
    specificPortsByPlayer.computeIfAbsent(playerId, ignored -> new HashSet<>()).add(resourceType);
  }

  public boolean hasSpecificPort(UUID playerId, ResourceType resourceType) {
    return specificPortsByPlayer.getOrDefault(playerId, Set.of()).contains(resourceType);
  }

  public int setupSettlementsPlaced(UUID playerId) {
    return setupSettlementsPlacedByPlayer.getOrDefault(playerId, 0);
  }

  public void incrementSetupSettlementsPlaced(UUID playerId) {
    setupSettlementsPlacedByPlayer.put(playerId, setupSettlementsPlaced(playerId) + 1);
  }

  public List<UUID> getSetupTurnOrder() {
    return setupTurnOrder;
  }

  public void setSetupTurnOrder(List<UUID> setupTurnOrder) {
    this.setupTurnOrder = List.copyOf(setupTurnOrder);
  }

  public int getSetupTurnIndex() {
    return setupTurnIndex;
  }

  public void setSetupTurnIndex(int setupTurnIndex) {
    this.setupTurnIndex = setupTurnIndex;
  }

  public boolean isSetupAwaitingRoadPlacement() {
    return setupAwaitingRoadPlacement;
  }

  public void setSetupAwaitingRoadPlacement(boolean setupAwaitingRoadPlacement) {
    this.setupAwaitingRoadPlacement = setupAwaitingRoadPlacement;
  }

  public Integer getSetupPendingSettlementNodeIndex() {
    return setupPendingSettlementNodeIndex;
  }

  public void setSetupPendingSettlementNodeIndex(Integer setupPendingSettlementNodeIndex) {
    this.setupPendingSettlementNodeIndex = setupPendingSettlementNodeIndex;
  }

  public void addTradeOffer(TradeOffer offer) {
    tradeOffers.put(offer.offerId(), offer);
  }

  public Optional<TradeOffer> getTradeOffer(UUID offerId) {
    return Optional.ofNullable(tradeOffers.get(offerId));
  }
}
