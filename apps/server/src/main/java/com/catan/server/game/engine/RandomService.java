package com.catan.server.game.engine;

import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class RandomService {

  public Random create(long seed) {
    return new Random(seed);
  }

  public long nextSeed() {
    return new Random().nextLong();
  }
}
