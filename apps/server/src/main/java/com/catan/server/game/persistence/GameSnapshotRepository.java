package com.catan.server.game.persistence;

import com.catan.server.game.domain.Game;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameSnapshotRepository extends JpaRepository<GameSnapshot, UUID> {

  Optional<GameSnapshot> findTopByGameOrderBySeqDesc(Game game);
}
