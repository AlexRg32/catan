package com.catan.server.game.engine;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.catan.server.game.engine.model.GameRuntimeState;
import com.catan.server.game.engine.model.SpecialFlow;
import com.catan.server.game.engine.model.TurnPhase;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class PhaseGuardTest {

  private final PhaseGuard phaseGuard = new PhaseGuard();

  @Test
  void validatesActivePlayerAndPhase() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    GameRuntimeState state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);

    assertDoesNotThrow(() -> phaseGuard.ensureActivePlayer(state, p1));
    assertThrows(ResponseStatusException.class, () -> phaseGuard.ensureActivePlayer(state, p2));

    assertDoesNotThrow(() -> phaseGuard.ensurePhase(state, TurnPhase.PRE_ROLL));
    assertThrows(
        ResponseStatusException.class, () -> phaseGuard.ensurePhase(state, TurnPhase.TRADING));

    state.setSpecialFlow(SpecialFlow.ROBBER_RESOLUTION);
    assertDoesNotThrow(() -> phaseGuard.ensureSpecialFlow(state, SpecialFlow.ROBBER_RESOLUTION));
    assertThrows(
        ResponseStatusException.class,
        () -> phaseGuard.ensureSpecialFlow(state, SpecialFlow.DISCARD_RESOLUTION));
  }
}
