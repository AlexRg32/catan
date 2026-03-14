package com.catan.server.game.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class SetupTurnOrderService {

  public List<UUID> snakeOrder(List<UUID> playerOrder) {
    if (playerOrder == null || playerOrder.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Setup requires players");
    }

    List<UUID> order = new ArrayList<>(playerOrder);
    List<UUID> reverse = new ArrayList<>(playerOrder);
    Collections.reverse(reverse);
    order.addAll(reverse);
    return order;
  }
}
