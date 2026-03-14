package com.catan.server.lobby.service;

import com.catan.server.auth.domain.UserAccount;
import com.catan.server.auth.repository.UserAccountRepository;
import com.catan.server.game.domain.Game;
import com.catan.server.game.domain.GamePlayer;
import com.catan.server.game.domain.GameStatus;
import com.catan.server.game.repository.GamePlayerRepository;
import com.catan.server.game.repository.GameRepository;
import com.catan.server.lobby.domain.Room;
import com.catan.server.lobby.domain.RoomSeat;
import com.catan.server.lobby.domain.RoomStatus;
import com.catan.server.lobby.dto.CreateRoomRequest;
import com.catan.server.lobby.dto.RoomResponse;
import com.catan.server.lobby.dto.RoomSeatResponse;
import com.catan.server.lobby.dto.StartGameResponse;
import com.catan.server.lobby.repository.RoomRepository;
import com.catan.server.lobby.repository.RoomSeatRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RoomService {

  private static final String[] PLAYER_COLORS = {"RED", "BLUE", "WHITE", "ORANGE"};

  private final RoomRepository roomRepository;
  private final RoomSeatRepository roomSeatRepository;
  private final UserAccountRepository userAccountRepository;
  private final GameRepository gameRepository;
  private final GamePlayerRepository gamePlayerRepository;

  public RoomService(
      RoomRepository roomRepository,
      RoomSeatRepository roomSeatRepository,
      UserAccountRepository userAccountRepository,
      GameRepository gameRepository,
      GamePlayerRepository gamePlayerRepository) {
    this.roomRepository = roomRepository;
    this.roomSeatRepository = roomSeatRepository;
    this.userAccountRepository = userAccountRepository;
    this.gameRepository = gameRepository;
    this.gamePlayerRepository = gamePlayerRepository;
  }

  @Transactional
  public RoomResponse createRoom(UUID actorUserId, CreateRoomRequest request) {
    UserAccount actor = fetchUser(actorUserId);

    Room room = new Room();
    room.setId(UUID.randomUUID());
    room.setCode(generateRoomCode());
    room.setName(
        request != null && request.name() != null && !request.name().isBlank()
            ? request.name().trim()
            : "Catan Room");
    room.setHostUser(actor);
    room.setStatus(RoomStatus.WAITING);
    room.setMaxPlayers(4);
    roomRepository.save(room);

    RoomSeat hostSeat = new RoomSeat();
    hostSeat.setId(UUID.randomUUID());
    hostSeat.setRoom(room);
    hostSeat.setSeatIndex(0);
    hostSeat.setUser(actor);
    hostSeat.setReady(false);
    roomSeatRepository.save(hostSeat);

    return toRoomResponse(room);
  }

  @Transactional
  public RoomResponse joinRoom(UUID actorUserId, String roomCode) {
    Room room = fetchRoom(roomCode);
    if (room.getStatus() != RoomStatus.WAITING) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Room already started");
    }

    if (roomSeatRepository.existsByRoomAndUser_Id(room, actorUserId)) {
      return toRoomResponse(room);
    }

    long seatCount = roomSeatRepository.countByRoom(room);
    if (seatCount >= room.getMaxPlayers()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Room is full");
    }

    UserAccount actor = fetchUser(actorUserId);
    RoomSeat seat = new RoomSeat();
    seat.setId(UUID.randomUUID());
    seat.setRoom(room);
    seat.setSeatIndex(nextSeatIndex(room));
    seat.setUser(actor);
    seat.setReady(false);
    roomSeatRepository.save(seat);

    return toRoomResponse(room);
  }

  @Transactional
  public RoomResponse setReady(UUID actorUserId, String roomCode, boolean ready) {
    Room room = fetchRoom(roomCode);
    RoomSeat seat =
        roomSeatRepository
            .findByRoomAndUser_Id(room, actorUserId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Not in room"));

    seat.setReady(ready);
    roomSeatRepository.save(seat);
    return toRoomResponse(room);
  }

  @Transactional
  public StartGameResponse startGame(UUID actorUserId, String roomCode) {
    Room room = fetchRoom(roomCode);
    if (!room.getHostUser().getId().equals(actorUserId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only host can start game");
    }
    if (room.getStatus() != RoomStatus.WAITING) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Room already started");
    }

    List<RoomSeat> seats = roomSeatRepository.findByRoomOrderBySeatIndexAsc(room);
    if (seats.size() != 4) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Room must have 4 players");
    }
    boolean allReady = seats.stream().allMatch(RoomSeat::isReady);
    if (!allReady) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "All players must be ready");
    }

    Game game = new Game();
    game.setId(UUID.randomUUID());
    game.setRoom(room);
    game.setStatus(GameStatus.IN_PROGRESS);
    game.setSeed(ThreadLocalRandom.current().nextLong());
    game.setCurrentTurnPlayer(seats.get(0).getUser());
    game.setPhase("PRE_ROLL");
    game.setStartedAt(Instant.now());
    gameRepository.save(game);

    for (int i = 0; i < seats.size(); i++) {
      RoomSeat seat = seats.get(i);
      GamePlayer gamePlayer = new GamePlayer();
      gamePlayer.setId(UUID.randomUUID());
      gamePlayer.setGame(game);
      gamePlayer.setUser(seat.getUser());
      gamePlayer.setColor(PLAYER_COLORS[i]);
      gamePlayer.setVictoryPoints(0);
      gamePlayer.setPlayedKnights(0);
      gamePlayer.setResourcesSummary("{}");
      gamePlayerRepository.save(gamePlayer);
    }

    room.setStatus(RoomStatus.IN_GAME);
    roomRepository.save(room);

    return new StartGameResponse(game.getId());
  }

  @Transactional(readOnly = true)
  public RoomResponse getRoom(String roomCode) {
    return toRoomResponse(fetchRoom(roomCode));
  }

  private Room fetchRoom(String roomCode) {
    return roomRepository
        .findByCodeIgnoreCase(roomCode)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
  }

  private UserAccount fetchUser(UUID userId) {
    return userAccountRepository
        .findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
  }

  private int nextSeatIndex(Room room) {
    List<Integer> used =
        roomSeatRepository.findByRoomOrderBySeatIndexAsc(room).stream()
            .map(RoomSeat::getSeatIndex)
            .toList();
    for (int i = 0; i < room.getMaxPlayers(); i++) {
      if (!used.contains(i)) {
        return i;
      }
    }
    throw new ResponseStatusException(HttpStatus.CONFLICT, "Room is full");
  }

  private String generateRoomCode() {
    String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    StringBuilder builder = new StringBuilder(6);
    for (int i = 0; i < 6; i++) {
      int index = ThreadLocalRandom.current().nextInt(alphabet.length());
      builder.append(alphabet.charAt(index));
    }
    return builder.toString();
  }

  private RoomResponse toRoomResponse(Room room) {
    List<RoomSeatResponse> seats = new ArrayList<>();
    for (RoomSeat seat : roomSeatRepository.findByRoomOrderBySeatIndexAsc(room)) {
      seats.add(
          new RoomSeatResponse(
              seat.getSeatIndex(),
              seat.getUser().getId(),
              seat.getUser().getNickname(),
              seat.isReady()));
    }

    return new RoomResponse(
        room.getId(),
        room.getCode().toUpperCase(Locale.ROOT),
        room.getName(),
        room.getStatus(),
        room.getHostUser().getId(),
        room.getMaxPlayers(),
        seats);
  }
}
