package com.catan.server.game.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameAuditRepository extends JpaRepository<GameAudit, UUID> {}
