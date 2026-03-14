package com.catan.server.game.service;

import com.catan.server.game.cards.DevCardType;
import com.catan.server.game.commands.CommandEnvelope;
import com.catan.server.game.engine.AwardService;
import com.catan.server.game.engine.BuildCityCommandHandler;
import com.catan.server.game.engine.BuildRoadCommandHandler;
import com.catan.server.game.engine.BuildSettlementCommandHandler;
import com.catan.server.game.engine.BuyDevCardCommandHandler;
import com.catan.server.game.engine.DiscardResourcesCommandHandler;
import com.catan.server.game.engine.EndTurnCommandHandler;
import com.catan.server.game.engine.EndTurnCommandResult;
import com.catan.server.game.engine.GameRuntimeStore;
import com.catan.server.game.engine.MaritimeTradeHandler;
import com.catan.server.game.engine.MoveRobberCommandHandler;
import com.catan.server.game.engine.MoveRobberCommandResult;
import com.catan.server.game.engine.PlayKnightCommandHandler;
import com.catan.server.game.engine.ProgressCardService;
import com.catan.server.game.engine.RollDiceCommandHandler;
import com.catan.server.game.engine.RollDiceCommandResult;
import com.catan.server.game.engine.SetupPhaseService;
import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.ResourceType;
import com.catan.server.game.trade.AnswerTradeHandler;
import com.catan.server.game.trade.TradeOffer;
import com.catan.server.game.trade.TradeService;
import com.catan.server.observability.CommandMetricsService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GameCommandDispatcher {

  private static final Logger log = LoggerFactory.getLogger(GameCommandDispatcher.class);

  private final GameRuntimeStore gameRuntimeStore;
  private final RollDiceCommandHandler rollDiceCommandHandler;
  private final DiscardResourcesCommandHandler discardResourcesCommandHandler;
  private final MoveRobberCommandHandler moveRobberCommandHandler;
  private final PlayKnightCommandHandler playKnightCommandHandler;
  private final AwardService awardService;
  private final ProgressCardService progressCardService;
  private final TradeService tradeService;
  private final AnswerTradeHandler answerTradeHandler;
  private final MaritimeTradeHandler maritimeTradeHandler;
  private final EndTurnCommandHandler endTurnCommandHandler;
  private final SetupPhaseService setupPhaseService;
  private final BuildRoadCommandHandler buildRoadCommandHandler;
  private final BuildSettlementCommandHandler buildSettlementCommandHandler;
  private final BuildCityCommandHandler buildCityCommandHandler;
  private final BuyDevCardCommandHandler buyDevCardCommandHandler;
  private final EventStoreService eventStoreService;
  private final RoomExecutionLock roomExecutionLock;
  private final AuditTrailService auditTrailService;
  private final CommandMetricsService commandMetricsService;

  public GameCommandDispatcher(
      GameRuntimeStore gameRuntimeStore,
      RollDiceCommandHandler rollDiceCommandHandler,
      DiscardResourcesCommandHandler discardResourcesCommandHandler,
      MoveRobberCommandHandler moveRobberCommandHandler,
      PlayKnightCommandHandler playKnightCommandHandler,
      AwardService awardService,
      ProgressCardService progressCardService,
      TradeService tradeService,
      AnswerTradeHandler answerTradeHandler,
      MaritimeTradeHandler maritimeTradeHandler,
      EndTurnCommandHandler endTurnCommandHandler,
      SetupPhaseService setupPhaseService,
      BuildRoadCommandHandler buildRoadCommandHandler,
      BuildSettlementCommandHandler buildSettlementCommandHandler,
      BuildCityCommandHandler buildCityCommandHandler,
      BuyDevCardCommandHandler buyDevCardCommandHandler,
      EventStoreService eventStoreService,
      RoomExecutionLock roomExecutionLock,
      AuditTrailService auditTrailService,
      CommandMetricsService commandMetricsService) {
    this.gameRuntimeStore = gameRuntimeStore;
    this.rollDiceCommandHandler = rollDiceCommandHandler;
    this.discardResourcesCommandHandler = discardResourcesCommandHandler;
    this.moveRobberCommandHandler = moveRobberCommandHandler;
    this.playKnightCommandHandler = playKnightCommandHandler;
    this.awardService = awardService;
    this.progressCardService = progressCardService;
    this.tradeService = tradeService;
    this.answerTradeHandler = answerTradeHandler;
    this.maritimeTradeHandler = maritimeTradeHandler;
    this.endTurnCommandHandler = endTurnCommandHandler;
    this.setupPhaseService = setupPhaseService;
    this.buildRoadCommandHandler = buildRoadCommandHandler;
    this.buildSettlementCommandHandler = buildSettlementCommandHandler;
    this.buildCityCommandHandler = buildCityCommandHandler;
    this.buyDevCardCommandHandler = buyDevCardCommandHandler;
    this.eventStoreService = eventStoreService;
    this.roomExecutionLock = roomExecutionLock;
    this.auditTrailService = auditTrailService;
    this.commandMetricsService = commandMetricsService;
  }

  public Map<String, Object> dispatch(UUID actorUserId, CommandEnvelope command) {
    long startedAt = System.nanoTime();
    MDC.put("gameId", command.gameId().toString());
    MDC.put("actorUserId", actorUserId.toString());
    MDC.put("commandType", command.type());
    return roomExecutionLock.withGameLock(
        command.gameId(),
        () -> {
          GameRuntimeState state = gameRuntimeStore.getOrCreate(command.gameId());
          try {
            if (state.isFinished()) {
              throw new ResponseStatusException(HttpStatus.CONFLICT, "Game is finished");
            }

            Map<String, Object> ack = dispatchCommand(state, actorUserId, command);
            long seq =
                eventStoreService.appendCommandEvent(
                    state, actorUserId, command.type(), command.payload(), ack);

            Map<String, Object> ackWithSeq = new HashMap<>(ack);
            ackWithSeq.put("seq", seq);
            auditTrailService.record(
                command.gameId(),
                command.commandId(),
                actorUserId,
                command.type(),
                "ACCEPTED",
                null);
            commandMetricsService.recordLatency(
                command.type(), "accepted", System.nanoTime() - startedAt);
            log.info("Command accepted: type={} seq={}", command.type(), seq);
            return ackWithSeq;
          } catch (ResponseStatusException ex) {
            auditTrailService.record(
                command.gameId(),
                command.commandId(),
                actorUserId,
                command.type(),
                "REJECTED",
                ex.getReason());
            commandMetricsService.recordLatency(
                command.type(), "rejected", System.nanoTime() - startedAt);
            if (ex.getStatusCode().is4xxClientError()) {
              commandMetricsService.recordValidationFailure(command.type());
            }
            log.warn("Command rejected: type={} reason={}", command.type(), ex.getReason());
            throw ex;
          } catch (RuntimeException ex) {
            auditTrailService.record(
                command.gameId(),
                command.commandId(),
                actorUserId,
                command.type(),
                "REJECTED",
                ex.getMessage());
            commandMetricsService.recordLatency(
                command.type(), "error", System.nanoTime() - startedAt);
            log.error("Command failed: type={}", command.type(), ex);
            throw ex;
          } finally {
            MDC.remove("gameId");
            MDC.remove("actorUserId");
            MDC.remove("commandType");
          }
        });
  }

  private Map<String, Object> dispatchCommand(
      GameRuntimeState state, UUID actorUserId, CommandEnvelope command) {
    return switch (command.type()) {
      case "roll_dice" ->
          toAck(command, actorUserId, rollDiceCommandHandler.handle(state, actorUserId));
      case "discard_resources" -> {
        discardResourcesCommandHandler.handle(
            state, actorUserId, parseResourceMap(command.payload(), "cards"));
        yield baseAck(command, actorUserId);
      }
      case "move_robber" -> {
        Integer targetHexIndex = asInt(command.payload(), "targetHexIndex");
        UUID targetPlayerId = asUuid(command.payload(), "targetPlayerId");
        MoveRobberCommandResult result =
            moveRobberCommandHandler.handle(state, actorUserId, targetHexIndex, targetPlayerId);
        yield toAck(command, actorUserId, result);
      }
      case "play_dev_knight" -> {
        int playedKnightCount = playKnightCommandHandler.handle(state, actorUserId);
        awardService.updateLargestArmy(state);
        Map<String, Object> ack = new HashMap<>(baseAck(command, actorUserId));
        ack.put("playedKnightCount", playedKnightCount);
        ack.put("largestArmyHolderId", state.getLargestArmyHolderId());
        ack.put("specialFlow", state.getSpecialFlow().name());
        yield ack;
      }
      case "play_dev_progress" -> {
        DevCardType cardType =
            DevCardType.valueOf(String.valueOf(command.payload().get("cardType")));
        Map<String, Object> result =
            progressCardService.execute(state, actorUserId, cardType, command.payload());
        Map<String, Object> ack = new HashMap<>(baseAck(command, actorUserId));
        ack.putAll(result);
        yield ack;
      }
      case "propose_trade" -> {
        Set<UUID> toPlayers = parseUuidSet(command.payload(), "toPlayerIds");
        Map<ResourceType, Integer> give = parseResourceMap(command.payload(), "give");
        Map<ResourceType, Integer> want = parseResourceMap(command.payload(), "want");
        TradeOffer offer = tradeService.createOffer(state, actorUserId, toPlayers, give, want);
        Map<String, Object> ack = new HashMap<>(baseAck(command, actorUserId));
        ack.put("offerId", offer.offerId());
        ack.put("status", offer.status().name());
        yield ack;
      }
      case "answer_trade" -> {
        UUID offerId = asUuid(command.payload(), "offerId");
        boolean accept = Boolean.parseBoolean(String.valueOf(command.payload().get("accept")));
        TradeOffer updated = answerTradeHandler.handle(state, actorUserId, offerId, accept);
        Map<String, Object> ack = new HashMap<>(baseAck(command, actorUserId));
        ack.put("offerId", updated.offerId());
        ack.put("status", updated.status().name());
        ack.put("acceptedBy", updated.acceptedBy());
        yield ack;
      }
      case "maritime_trade" -> {
        int ratio = asInt(command.payload(), "ratio");
        ResourceType giveType =
            ResourceType.valueOf(String.valueOf(command.payload().get("giveType")));
        int giveCount = asInt(command.payload(), "giveCount");
        ResourceType getType =
            ResourceType.valueOf(String.valueOf(command.payload().get("getType")));
        if (ratio == 4) {
          maritimeTradeHandler.trade4to1(state, actorUserId, giveType, giveCount, getType);
        } else if (ratio == 3) {
          maritimeTradeHandler.trade3to1(state, actorUserId, giveType, giveCount, getType);
        } else if (ratio == 2) {
          maritimeTradeHandler.trade2to1(state, actorUserId, giveType, giveCount, getType);
        } else {
          throw new ResponseStatusException(
              HttpStatus.UNPROCESSABLE_ENTITY, "Unsupported maritime ratio");
        }
        yield baseAck(command, actorUserId);
      }
      case "end_turn" -> {
        EndTurnCommandResult result = endTurnCommandHandler.handle(state, actorUserId);
        Map<String, Object> ack = new HashMap<>(baseAck(command, actorUserId));
        ack.put("nextActivePlayerId", result.nextActivePlayerId());
        ack.put("turnNumber", result.turnNumber());
        ack.put("phase", state.getPhase().name());
        ack.put("finished", result.finished());
        ack.put("winnerPlayerId", result.winnerPlayerId());
        yield ack;
      }
      case "setup_init" -> {
        setupPhaseService.initialize(state);
        Map<String, Object> ack = new HashMap<>(baseAck(command, actorUserId));
        ack.put("phase", state.getPhase().name());
        ack.put("setupTurnOrder", state.getSetupTurnOrder());
        ack.put("activePlayerId", state.getActivePlayerId());
        yield ack;
      }
      case "setup_place_settlement" -> {
        int nodeIndex = asInt(command.payload(), "nodeIndex");
        setupPhaseService.placeSettlement(state, actorUserId, nodeIndex);
        Map<String, Object> ack = new HashMap<>(baseAck(command, actorUserId));
        ack.put("nodeIndex", nodeIndex);
        ack.put("awaitingRoad", state.isSetupAwaitingRoadPlacement());
        yield ack;
      }
      case "setup_place_road" -> {
        int edgeIndex = asInt(command.payload(), "edgeIndex");
        setupPhaseService.placeRoad(state, actorUserId, edgeIndex);
        Map<String, Object> ack = new HashMap<>(baseAck(command, actorUserId));
        ack.put("edgeIndex", edgeIndex);
        ack.put("setupCompleted", state.isSetupCompleted());
        ack.put("activePlayerId", state.getActivePlayerId());
        ack.put("phase", state.getPhase().name());
        yield ack;
      }
      case "build_road" -> {
        int edgeIndex = asInt(command.payload(), "edgeIndex");
        Map<UUID, Integer> roadLengths =
            buildRoadCommandHandler.handle(state, actorUserId, edgeIndex);
        Map<String, Object> ack = new HashMap<>(baseAck(command, actorUserId));
        ack.put("edgeIndex", edgeIndex);
        ack.put("phase", state.getPhase().name());
        ack.put("longestRoadHolderId", state.getLongestRoadHolderId());
        ack.put("roadLengths", roadLengths);
        yield ack;
      }
      case "build_settlement" -> {
        int nodeIndex = asInt(command.payload(), "nodeIndex");
        buildSettlementCommandHandler.handle(state, actorUserId, nodeIndex);
        Map<String, Object> ack = new HashMap<>(baseAck(command, actorUserId));
        ack.put("nodeIndex", nodeIndex);
        ack.put("phase", state.getPhase().name());
        yield ack;
      }
      case "build_city" -> {
        int nodeIndex = asInt(command.payload(), "nodeIndex");
        buildCityCommandHandler.handle(state, actorUserId, nodeIndex);
        Map<String, Object> ack = new HashMap<>(baseAck(command, actorUserId));
        ack.put("nodeIndex", nodeIndex);
        ack.put("phase", state.getPhase().name());
        yield ack;
      }
      case "buy_dev_card" -> {
        DevCardType drawnCard = buyDevCardCommandHandler.handle(state, actorUserId);
        Map<String, Object> ack = new HashMap<>(baseAck(command, actorUserId));
        ack.put("drawnCardType", drawnCard.name());
        ack.put("remainingDevDeck", state.remainingDevelopmentCards());
        ack.put("phase", state.getPhase().name());
        yield ack;
      }
      default ->
          throw new ResponseStatusException(
              HttpStatus.UNPROCESSABLE_ENTITY, "Unsupported command type: " + command.type());
    };
  }

  private Map<String, Object> baseAck(CommandEnvelope command, UUID actorUserId) {
    return Map.of(
        "status", "accepted",
        "gameId", command.gameId().toString(),
        "commandId", command.commandId(),
        "actorUserId", actorUserId.toString(),
        "type", command.type());
  }

  private Map<String, Object> toAck(
      CommandEnvelope command, UUID actorUserId, RollDiceCommandResult result) {
    Map<String, Object> ack = new HashMap<>(baseAck(command, actorUserId));
    ack.put("roll", result.roll());
    ack.put("specialFlow", result.specialFlow().name());
    ack.put("pendingDiscards", result.pendingDiscards());
    ack.put("production", result.production());
    return ack;
  }

  private Map<String, Object> toAck(
      CommandEnvelope command, UUID actorUserId, MoveRobberCommandResult result) {
    Map<String, Object> ack = new HashMap<>(baseAck(command, actorUserId));
    ack.put("previousHexIndex", result.previousHexIndex());
    ack.put("newHexIndex", result.newHexIndex());
    ack.put("victimPlayerId", result.victimPlayerId());
    ack.put("stolenResourceType", result.stolenResourceType().map(Enum::name).orElse(null));
    return ack;
  }

  private Map<ResourceType, Integer> parseResourceMap(Map<String, Object> payload, String key) {
    if (payload == null || !payload.containsKey(key)) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Missing payload field: " + key);
    }

    Object obj = payload.get(key);
    if (!(obj instanceof Map<?, ?> cardsMap)) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Field " + key + " must be an object");
    }

    Map<ResourceType, Integer> parsed = new HashMap<>();
    for (Map.Entry<?, ?> entry : cardsMap.entrySet()) {
      ResourceType resourceType = ResourceType.valueOf(String.valueOf(entry.getKey()));
      int amount = Integer.parseInt(String.valueOf(entry.getValue()));
      parsed.put(resourceType, amount);
    }
    return parsed;
  }

  private Set<UUID> parseUuidSet(Map<String, Object> payload, String key) {
    if (payload == null || !payload.containsKey(key)) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Missing payload field: " + key);
    }
    Object raw = payload.get(key);
    if (!(raw instanceof Iterable<?> iterable)) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Field " + key + " must be an array");
    }

    Set<UUID> parsed = new HashSet<>();
    for (Object item : iterable) {
      parsed.add(UUID.fromString(String.valueOf(item)));
    }
    return parsed;
  }

  private Integer asInt(Map<String, Object> payload, String key) {
    if (payload == null || !payload.containsKey(key)) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Missing payload field: " + key);
    }
    return Integer.parseInt(String.valueOf(payload.get(key)));
  }

  private UUID asUuid(Map<String, Object> payload, String key) {
    if (payload == null || !payload.containsKey(key) || payload.get(key) == null) {
      return null;
    }
    return UUID.fromString(String.valueOf(payload.get(key)));
  }
}
