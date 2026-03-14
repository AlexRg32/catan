package com.catan.server.game.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.catan.server.game.cards.DevCardType;
import com.catan.server.game.engine.model.ResourceType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ProjectionServiceTest {

  @Test
  void masksOpponentHandsAndDevCardTypes() {
    UUID p1 = UUID.randomUUID();
    UUID p2 = UUID.randomUUID();
    var state = GameRuntimeFixtures.basicTwoPlayerState(p1, p2);
    state.addResource(p1, ResourceType.WOOD, 2);
    state.addResource(p2, ResourceType.ORE, 3);
    state.addDevCardToPlayer(p1, DevCardType.KNIGHT, 1);
    state.addDevCardToPlayer(p2, DevCardType.VICTORY_POINT, 1);

    LegalActionService legalActionService = Mockito.mock(LegalActionService.class);
    Mockito.when(legalActionService.forPlayer(state, p1)).thenReturn(List.of());
    ProjectionService service = new ProjectionService(legalActionService);
    var projection = service.projectForPlayer(state, p1, 7L);

    assertEquals(2, projection.players().size());
    var self =
        projection.players().stream()
            .filter(player -> player.playerId().equals(p1))
            .findFirst()
            .orElseThrow();
    var opponent =
        projection.players().stream()
            .filter(player -> player.playerId().equals(p2))
            .findFirst()
            .orElseThrow();

    assertNotNull(self.resources());
    assertNotNull(self.devCards());
    assertNull(opponent.resources());
    assertNull(opponent.devCards());
    assertEquals(3, opponent.resourceCount());
    assertEquals(1, opponent.devCardCount());
    assertEquals(7L, projection.lastSequence());
    assertNotNull(projection.board());
  }
}
