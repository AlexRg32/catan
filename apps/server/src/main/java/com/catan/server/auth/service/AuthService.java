package com.catan.server.auth.service;

import com.catan.server.auth.domain.UserAccount;
import com.catan.server.auth.domain.UserSession;
import com.catan.server.auth.dto.AuthResponse;
import com.catan.server.auth.dto.LoginRequest;
import com.catan.server.auth.dto.RegisterRequest;
import com.catan.server.auth.dto.UserResponse;
import com.catan.server.auth.repository.UserAccountRepository;
import com.catan.server.auth.repository.UserSessionRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

  private final UserAccountRepository userAccountRepository;
  private final UserSessionRepository userSessionRepository;
  private final PasswordService passwordService;
  private final JwtService jwtService;

  public AuthService(
      UserAccountRepository userAccountRepository,
      UserSessionRepository userSessionRepository,
      PasswordService passwordService,
      JwtService jwtService) {
    this.userAccountRepository = userAccountRepository;
    this.userSessionRepository = userSessionRepository;
    this.passwordService = passwordService;
    this.jwtService = jwtService;
  }

  @Transactional
  public AuthResponse register(RegisterRequest request) {
    String email = request.email().trim().toLowerCase();
    String nickname = request.nickname().trim();

    if (userAccountRepository.existsByEmailIgnoreCase(email)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
    }
    if (userAccountRepository.existsByNicknameIgnoreCase(nickname)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Nickname already taken");
    }

    UserAccount user = new UserAccount();
    user.setId(UUID.randomUUID());
    user.setEmail(email);
    user.setNickname(nickname);
    user.setPasswordHash(passwordService.hash(request.password()));
    user.setTokenVersion(0);
    userAccountRepository.save(user);

    return issueAuthResponse(user);
  }

  @Transactional
  public AuthResponse login(LoginRequest request) {
    String email = request.email().trim().toLowerCase();
    UserAccount user =
        userAccountRepository
            .findByEmailIgnoreCase(email)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login"));

    if (!passwordService.verify(request.password(), user.getPasswordHash())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login");
    }

    return issueAuthResponse(user);
  }

  @Transactional(readOnly = true)
  public UserResponse me(UUID userId) {
    UserAccount user =
        userAccountRepository
            .findById(userId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    return toUserResponse(user);
  }

  private AuthResponse issueAuthResponse(UserAccount user) {
    String accessToken = jwtService.generateAccessToken(user);

    UserSession session = new UserSession();
    session.setId(UUID.randomUUID());
    session.setUser(user);
    session.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
    userSessionRepository.save(session);

    return new AuthResponse(toUserResponse(user), accessToken);
  }

  private UserResponse toUserResponse(UserAccount user) {
    return new UserResponse(user.getId(), user.getEmail(), user.getNickname());
  }
}
