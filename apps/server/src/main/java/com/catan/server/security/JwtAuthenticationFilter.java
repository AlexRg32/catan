package com.catan.server.security;

import com.catan.server.auth.domain.UserAccount;
import com.catan.server.auth.repository.UserAccountRepository;
import com.catan.server.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserAccountRepository userAccountRepository;

  public JwtAuthenticationFilter(
      JwtService jwtService, UserAccountRepository userAccountRepository) {
    this.jwtService = jwtService;
    this.userAccountRepository = userAccountRepository;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String authHeader = request.getHeader("Authorization");

    if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      Optional<Claims> claimsOptional = jwtService.parseAccessToken(token);
      if (claimsOptional.isPresent()) {
        Claims claims = claimsOptional.get();
        String subject = claims.getSubject();
        Integer tokenVersionClaim = claims.get("tv", Integer.class);
        if (subject != null && tokenVersionClaim != null) {
          UserAccount user = userAccountRepository.findById(UUID.fromString(subject)).orElse(null);
          if (user != null && user.getTokenVersion() == tokenVersionClaim) {
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    user.getId().toString(), null, Collections.emptyList());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
          }
        }
      }
    }

    filterChain.doFilter(request, response);
  }
}
