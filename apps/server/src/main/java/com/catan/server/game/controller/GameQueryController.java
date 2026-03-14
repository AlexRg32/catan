package com.catan.server.game.controller;

import com.catan.server.game.dto.GameEventsResponse;
import com.catan.server.game.dto.GameSnapshotResponse;
import com.catan.server.game.service.GameQueryService;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/games")
public class GameQueryController {

  private final GameQueryService gameQueryService;

  public GameQueryController(GameQueryService gameQueryService) {
    this.gameQueryService = gameQueryService;
  }

  @GetMapping("/{gameId}")
  public GameSnapshotResponse getSnapshot(
      Authentication authentication, @PathVariable UUID gameId) {
    return gameQueryService.snapshot(gameId, actorUserId(authentication));
  }

  @GetMapping("/{gameId}/events")
  public GameEventsResponse getEvents(
      Authentication authentication,
      @PathVariable UUID gameId,
      @RequestParam(name = "fromSeq", defaultValue = "0") long fromSeq) {
    return gameQueryService.events(gameId, actorUserId(authentication), fromSeq);
  }

  private UUID actorUserId(Authentication authentication) {
    return UUID.fromString(authentication.getName());
  }
}
