package com.catan.server.auth.dto;

public record AuthResponse(UserResponse user, String accessToken) {}
