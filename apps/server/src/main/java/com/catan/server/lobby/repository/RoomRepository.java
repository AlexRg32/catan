package com.catan.server.lobby.repository;

import com.catan.server.lobby.domain.Room;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, UUID> {

  Optional<Room> findByCodeIgnoreCase(String code);
}
