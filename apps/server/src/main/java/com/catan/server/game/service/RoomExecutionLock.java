package com.catan.server.game.service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

@Component
public class RoomExecutionLock {

  private final ConcurrentMap<UUID, ReentrantLock> locks = new ConcurrentHashMap<>();

  public <T> T withGameLock(UUID gameId, Supplier<T> action) {
    ReentrantLock lock = locks.computeIfAbsent(gameId, ignored -> new ReentrantLock());
    lock.lock();
    try {
      return action.get();
    } finally {
      lock.unlock();
    }
  }
}
