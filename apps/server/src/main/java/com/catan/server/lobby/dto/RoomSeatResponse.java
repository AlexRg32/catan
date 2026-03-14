package com.catan.server.lobby.dto;

import java.util.UUID;

public record RoomSeatResponse(int seatIndex, UUID userId, String nickname, boolean ready) {}
