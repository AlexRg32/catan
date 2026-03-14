package com.catan.server.websocket;

import com.catan.server.auth.domain.UserAccount;
import com.catan.server.auth.repository.UserAccountRepository;
import com.catan.server.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import java.security.Principal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AuthChannelInterceptor implements ChannelInterceptor {

  private final JwtService jwtService;
  private final UserAccountRepository userAccountRepository;

  public AuthChannelInterceptor(
      JwtService jwtService, UserAccountRepository userAccountRepository) {
    this.jwtService = jwtService;
    this.userAccountRepository = userAccountRepository;
  }

  @Override
  public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
      String authHeader = accessor.getFirstNativeHeader("Authorization");
      if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
        throw new IllegalArgumentException("Missing Authorization header");
      }

      String token = authHeader.substring(7);
      Optional<Claims> claimsOptional = jwtService.parseAccessToken(token);
      Claims claims =
          claimsOptional.orElseThrow(() -> new IllegalArgumentException("Invalid token"));

      UUID userId = UUID.fromString(claims.getSubject());
      Integer tokenVersion = claims.get("tv", Integer.class);
      UserAccount user =
          userAccountRepository
              .findById(userId)
              .orElseThrow(() -> new IllegalArgumentException("User not found"));

      if (tokenVersion == null || user.getTokenVersion() != tokenVersion) {
        throw new IllegalArgumentException("Invalid token version");
      }

      Principal principal =
          new UsernamePasswordAuthenticationToken(
              user.getId().toString(), null, Collections.emptyList());
      accessor.setUser(principal);
    }

    return message;
  }
}
