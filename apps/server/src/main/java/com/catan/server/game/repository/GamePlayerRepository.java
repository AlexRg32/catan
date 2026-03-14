package com.catan.server.game.repository;

import com.catan.server.game.domain.Game;
import com.catan.server.game.domain.GamePlayer;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GamePlayerRepository extends JpaRepository<GamePlayer, UUID> {

  List<GamePlayer> findByGameOrderByCreatedAtAsc(Game game);

  boolean existsByGame_IdAndUser_Id(UUID gameId, UUID userId);
}
