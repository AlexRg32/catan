package com.catan.server.auth.service;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

  public String hash(String rawPassword) {
    return BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));
  }

  public boolean verify(String rawPassword, String hashedPassword) {
    return BCrypt.checkpw(rawPassword, hashedPassword);
  }
}
