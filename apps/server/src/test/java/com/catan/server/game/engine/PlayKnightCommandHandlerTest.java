package com.catan.server.game.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.catan.server.game.cards.DevCardType;
import com.catan.server.game.engine.model.SpecialFlow;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class PlayKnightCommandHandlerTest {

  @Test
  void playsKnightAndActivatesRobberFlow() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);

    state.addDevCardToPlayer(p1, DevCardType.KNIGHT, 1);
    state.setTurnNumber(2);

    PlayKnightCommandHandler handler = new PlayKnightCommandHandler(new PhaseGuard());
    int playedKnights = handler.handle(state, p1);

    assertEquals(1, playedKnights);
    assertEquals(1, state.playedKnightCount(p1));
    assertEquals(SpecialFlow.ROBBER_RESOLUTION, state.getSpecialFlow());
  }

  @Test
  void rejectsSameTurnBoughtKnight() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);

    state.addDevCardToPlayer(p1, DevCardType.KNIGHT, 2);
    state.setTurnNumber(2);

    PlayKnightCommandHandler handler = new PlayKnightCommandHandler(new PhaseGuard());

    assertThrows(ResponseStatusException.class, () -> handler.handle(state, p1));
  }
}
