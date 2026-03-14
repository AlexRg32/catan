package com.catan.server.auth.repository;

import com.catan.server.auth.domain.UserSession;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {}
