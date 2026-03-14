package com.catan.server.lobby.repository;

import com.catan.server.lobby.domain.Room;
import com.catan.server.lobby.domain.RoomSeat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomSeatRepository extends JpaRepository<RoomSeat, UUID> {

  List<RoomSeat> findByRoomOrderBySeatIndexAsc(Room room);

  boolean existsByRoomAndUser_Id(Room room, UUID userId);

  Optional<RoomSeat> findByRoomAndUser_Id(Room room, UUID userId);

  long countByRoom(Room room);
}
