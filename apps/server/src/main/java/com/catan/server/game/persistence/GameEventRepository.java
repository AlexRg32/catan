package com.catan.server.game.persistence;

import com.catan.server.game.domain.Game;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameEventRepository extends JpaRepository<GameEvent, UUID> {

  Optional<GameEvent> findTopByGameOrderBySeqDesc(Game game);

  List<GameEvent> findByGameAndSeqGreaterThanOrderBySeqAsc(Game game, long seq);
}
