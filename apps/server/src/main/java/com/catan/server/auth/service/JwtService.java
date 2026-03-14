package com.catan.server.auth.service;

import com.catan.server.auth.domain.UserAccount;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final SecretKey signingKey;
  private final Duration accessTokenTtl;

  public JwtService(
      @Value("${app.jwt.secret:dev-secret-dev-secret-dev-secret-1234}") String rawSecret,
      @Value("${app.jwt.access-token-minutes:720}") long accessTokenMinutes) {
    byte[] keyBytes = rawSecret.getBytes(StandardCharsets.UTF_8);
    if (keyBytes.length < 32) {
      throw new IllegalArgumentException("JWT secret must be at least 32 bytes");
    }
    this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    this.accessTokenTtl = Duration.ofMinutes(accessTokenMinutes);
  }

  public String generateAccessToken(UserAccount user) {
    Instant now = Instant.now();
    Instant expiresAt = now.plus(accessTokenTtl);

    return Jwts.builder()
        .subject(user.getId().toString())
        .claim("email", user.getEmail())
        .claim("nickname", user.getNickname())
        .claim("tv", user.getTokenVersion())
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiresAt))
        .signWith(signingKey)
        .compact();
  }

  public Optional<Claims> parseAccessToken(String token) {
    try {
      Claims claims =
          Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
      return Optional.of(claims);
    } catch (Exception ignored) {
      return Optional.empty();
    }
  }
}
