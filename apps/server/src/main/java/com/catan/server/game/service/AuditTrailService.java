package com.catan.server.game.service;

import com.catan.server.game.domain.Game;
import com.catan.server.game.persistence.GameAudit;
import com.catan.server.game.persistence.GameAuditRepository;
import com.catan.server.game.repository.GameRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditTrailService {

  private final GameRepository gameRepository;
  private final GameAuditRepository gameAuditRepository;

  public AuditTrailService(GameRepository gameRepository, GameAuditRepository gameAuditRepository) {
    this.gameRepository = gameRepository;
    this.gameAuditRepository = gameAuditRepository;
  }

  @Transactional
  public void record(
      UUID gameId,
      String commandId,
      UUID actorPlayerId,
      String commandType,
      String outcome,
      String errorMessage) {
    Optional<Game> game = gameRepository.findById(gameId);
    if (game.isEmpty()) {
      return;
    }

    GameAudit audit = new GameAudit();
    audit.setId(UUID.randomUUID());
    audit.setGame(game.get());
    audit.setCommandId(commandId);
    audit.setActorPlayerId(actorPlayerId);
    audit.setCommandType(commandType);
    audit.setOutcome(outcome);
    audit.setErrorMessage(errorMessage);
    gameAuditRepository.save(audit);
  }
}
