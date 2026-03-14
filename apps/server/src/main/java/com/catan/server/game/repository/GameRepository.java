package com.catan.server.game.repository;

import com.catan.server.game.domain.Game;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, UUID> {}
