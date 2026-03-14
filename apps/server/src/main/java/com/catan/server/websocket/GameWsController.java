package com.catan.server.websocket;

import com.catan.server.game.commands.CommandEnvelope;
import com.catan.server.game.service.GameCommandDispatcher;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Map;
import java.util.UUID;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GameWsController {

  private final GameCommandDispatcher gameCommandDispatcher;
  private final SimpMessagingTemplate messagingTemplate;

  public GameWsController(
      GameCommandDispatcher gameCommandDispatcher, SimpMessagingTemplate messagingTemplate) {
    this.gameCommandDispatcher = gameCommandDispatcher;
    this.messagingTemplate = messagingTemplate;
  }

  @MessageMapping("/game.command")
  public void handleCommand(@Valid CommandEnvelope command, Principal principal) {
    UUID actorUserId = UUID.fromString(principal.getName());
    Map<String, Object> ack = gameCommandDispatcher.dispatch(actorUserId, command);

    messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/ack", ack);
  }
}
