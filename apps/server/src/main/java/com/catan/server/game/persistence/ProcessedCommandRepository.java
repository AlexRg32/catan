package com.catan.server.game.persistence;

import com.catan.server.game.domain.Game;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedCommandRepository extends JpaRepository<ProcessedCommand, UUID> {

  boolean existsByGameAndCommandId(Game game, String commandId);
}
