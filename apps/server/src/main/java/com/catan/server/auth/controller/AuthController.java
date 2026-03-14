package com.catan.server.auth.controller;

import com.catan.server.auth.dto.AuthResponse;
import com.catan.server.auth.dto.LoginRequest;
import com.catan.server.auth.dto.RegisterRequest;
import com.catan.server.auth.dto.UserResponse;
import com.catan.server.auth.service.AuthService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
    return authService.register(request);
  }

  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest request) {
    return authService.login(request);
  }

  @GetMapping("/me")
  public UserResponse me(Authentication authentication) {
    UUID userId = UUID.fromString(authentication.getName());
    return authService.me(userId);
  }
}
