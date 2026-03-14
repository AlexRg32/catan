package com.catan.server.game.engine;

import com.catan.server.game.engine.model.GameRuntimeState;
import org.springframework.stereotype.Component;

@Component
public class DiceRoller {

  public int roll2d6(GameRuntimeState state) {
    return state.rollDie() + state.rollDie();
  }
}
