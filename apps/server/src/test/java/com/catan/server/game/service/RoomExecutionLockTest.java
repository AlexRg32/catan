package com.catan.server.game.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class RoomExecutionLockTest {

  @Test
  void lockSerializesCommandsPerGame() throws Exception {
    RoomExecutionLock lock = new RoomExecutionLock();
    UUID gameId = UUID.randomUUID();

    AtomicInteger inFlight = new AtomicInteger(0);
    AtomicInteger maxInFlight = new AtomicInteger(0);

    Callable<Void> task =
        () ->
            lock.withGameLock(
                gameId,
                () -> {
                  int current = inFlight.incrementAndGet();
                  maxInFlight.updateAndGet(previous -> Math.max(previous, current));
                  try {
                    Thread.sleep(40L);
                  } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                  } finally {
                    inFlight.decrementAndGet();
                  }
                  return null;
                });

    try (var executor = Executors.newFixedThreadPool(2)) {
      Future<Void> f1 = executor.submit(task);
      Future<Void> f2 = executor.submit(task);
      f1.get();
      f2.get();
    }

    assertEquals(1, maxInFlight.get());
  }
}
