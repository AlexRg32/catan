package com.catan.server.game.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.catan.server.game.engine.model.SpecialFlow;
import com.catan.server.game.engine.model.TurnPhase;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RollDiceCommandHandlerTest {

  @Test
  void rollSevenTriggersDiscardFlow() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);
    GameRuntimeFixtures.addResources(state, p1, 2);
    GameRuntimeFixtures.addResources(state, p2, 2);

    DiceRoller diceRoller = Mockito.mock(DiceRoller.class);
    when(diceRoller.roll2d6(state)).thenReturn(7);

    RollDiceCommandHandler handler =
        new RollDiceCommandHandler(
            new PhaseGuard(), diceRoller, new ProductionService(), new DiscardService());

    RollDiceCommandResult result = handler.handle(state, p1);

    assertEquals(7, result.roll());
    assertEquals(SpecialFlow.DISCARD_RESOLUTION, result.specialFlow());
    assertTrue(result.pendingDiscards().containsKey(p1));
    assertTrue(result.pendingDiscards().containsKey(p2));
    assertEquals(TurnPhase.POST_ROLL, state.getPhase());
  }

  @Test
  void normalRollTransitionsToTrading() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);

    DiceRoller diceRoller = Mockito.mock(DiceRoller.class);
    when(diceRoller.roll2d6(state)).thenReturn(8);

    RollDiceCommandHandler handler =
        new RollDiceCommandHandler(
            new PhaseGuard(), diceRoller, new ProductionService(), new DiscardService());

    RollDiceCommandResult result = handler.handle(state, p1);

    assertEquals(8, result.roll());
    assertEquals(SpecialFlow.NONE, result.specialFlow());
    assertEquals(TurnPhase.TRADING, state.getPhase());
    assertEquals(
        1, state.getResourceCount(p1, com.catan.server.game.engine.model.ResourceType.WOOD));
  }
}
