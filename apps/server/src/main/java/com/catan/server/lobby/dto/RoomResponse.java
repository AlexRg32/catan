package com.catan.server.lobby.dto;

import com.catan.server.lobby.domain.RoomStatus;
import java.util.List;
import java.util.UUID;

public record RoomResponse(
    UUID roomId,
    String roomCode,
    String roomName,
    RoomStatus status,
    UUID hostUserId,
    int maxPlayers,
    List<RoomSeatResponse> seats) {}
