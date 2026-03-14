package com.catan.server.lobby.controller;

import com.catan.server.lobby.dto.CreateRoomRequest;
import com.catan.server.lobby.dto.ReadyRequest;
import com.catan.server.lobby.dto.RoomResponse;
import com.catan.server.lobby.dto.StartGameResponse;
import com.catan.server.lobby.service.RoomService;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

  private final RoomService roomService;

  public RoomController(RoomService roomService) {
    this.roomService = roomService;
  }

  @PostMapping
  public RoomResponse createRoom(
      Authentication authentication, @RequestBody(required = false) CreateRoomRequest request) {
    return roomService.createRoom(userId(authentication), request);
  }

  @PostMapping("/{roomCode}/join")
  public RoomResponse joinRoom(Authentication authentication, @PathVariable String roomCode) {
    return roomService.joinRoom(userId(authentication), roomCode);
  }

  @PostMapping("/{roomCode}/ready")
  public RoomResponse setReady(
      Authentication authentication,
      @PathVariable String roomCode,
      @RequestBody ReadyRequest request) {
    return roomService.setReady(userId(authentication), roomCode, request.ready());
  }

  @PostMapping("/{roomCode}/start")
  public StartGameResponse startGame(Authentication authentication, @PathVariable String roomCode) {
    return roomService.startGame(userId(authentication), roomCode);
  }

  @GetMapping("/{roomCode}")
  public RoomResponse getRoom(@PathVariable String roomCode) {
    return roomService.getRoom(roomCode);
  }

  private UUID userId(Authentication authentication) {
    return UUID.fromString(authentication.getName());
  }
}
