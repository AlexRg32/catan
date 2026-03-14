# Design: Catan Online 4 Players (Rules-Accurate)

## Architecture Overview

We will implement a server-authoritative multiplayer architecture:

- React client renders UI and sends user intent commands.
- Spring backend validates every command against game rules and current state.
- Backend emits canonical events/state snapshots to all players in room.
- Client never computes authoritative outcomes (only local UX hints).

This model is required to enforce official rules from the provided PDF and prevent cheating/desync.

## Logical Architecture

1. `web-client` (React)
- Lobby UI, board UI, action UI, hand UI.
- WebSocket session with auth token.
- Receives player-projected state (hidden info masked).

2. `game-api` (Spring Boot)
- Auth and user profile APIs.
- Lobby and room APIs.
- WebSocket command gateway.

3. `game-engine` (Spring module)
- Deterministic state machine.
- Command handlers with rule validation.
- Event emitter and projection layer.

4. `game-store`
- PostgreSQL for users, matches, events, snapshots, rankings.
- Redis (optional) for room cache, locks, ephemeral presence.

5. `ops`
- Metrics, logs, tracing, dashboards, alerting, release pipelines.

## Domain Model

| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| User | id, email, nickname, passwordHash, createdAt | 1:N sessions, 1:N matchParticipation |
| Session | id, userId, tokenVersion, expiresAt | N:1 user |
| Room | id, code, status, maxPlayers=4, hostUserId | 1:N roomSeats, 0..1 activeGame |
| RoomSeat | roomId, seatIndex, userId, ready | N:1 room, N:1 user |
| Game | id, roomId, status, seed, currentTurnPlayerId, phase, startedAt, endedAt | 1:N gamePlayers, 1:N gameEvents |
| GamePlayer | id, gameId, userId, color, victoryPoints, playedKnights, resourcesSummary | N:1 game, N:1 user |
| HexTile | gameId, hexIndex, terrainType, diceNumber, hasRobber | N:1 game |
| Intersection | gameId, nodeId, buildingType, ownerPlayerId | N:1 game |
| Edge | gameId, edgeId, hasRoad, ownerPlayerId | N:1 game |
| DevCardDeck | gameId, remainingOrderedCards | N:1 game |
| TradeOffer | id, gameId, fromPlayerId, toPlayerIds, give, want, status, expiresAt | N:1 game |
| GameEvent | id, gameId, seq, type, actorPlayerId, payload, createdAt | N:1 game |
| GameSnapshot | id, gameId, seq, stateJson, createdAt | N:1 game |

## State Machine

### Top-Level
- `ROOM_WAITING`
- `GAME_SETUP`
- `GAME_IN_TURN`
- `GAME_FINISHED`

### Turn Phases (per active player)
- `PRE_ROLL`
- `POST_ROLL`
- `TRADING`
- `BUILDING`
- `TURN_END`

### Special Sub-Flows
- `ROBBER_RESOLUTION` (triggered by roll 7 or knight)
- `DISCARD_RESOLUTION` (players >7 cards)
- `TRADE_OFFER_RESOLUTION`

## Rules Matrix (From PDF -> Technical Enforcement)

| Rule (PDF baseline) | Engine Enforcement | Client UX Enforcement | Tests |
|---|---|---|---|
| Roll then trade then build | Turn-phase gate by command type | Disable buttons not valid in phase | Phase transition integration tests |
| Dev card cannot be played same turn bought | Store purchase turn index, reject play | Show "available next turn" label | Unit tests for timing rule |
| Settlement distance rule | Neighbor node occupancy validation | Highlight invalid intersections | Graph validation tests |
| Road continuity / interruption | Edge placement with ownership and interruption checks | Show blocked edges | Pathing scenario tests |
| Longest road >=5, transferred when surpassed | Compute longest contiguous simple path per player | Real-time badge updates | Golden fixtures for branching cases |
| City upgrade only from own settlement | Validate building exists and ownership | Upgrade action only on eligible node | Unit tests |
| Roll 7 discard and robber move | Global discard queue + robber move command | Discard modal + robber picker | Integration tests with multi-player queue |
| Robber blocks production on tile | Production engine checks robber tile | Tile overlay blocked icon | Production tests |
| Domestic trade only with active player | Offer creator must be active player | Non-active player cannot open offer | Command auth tests |
| Maritime 4:1, 3:1, 2:1 | Port ownership + ratio validation | Dynamic trade ratio UI | Unit tests per port type |
| Largest army from 3 knights | Award logic with transfer conditions | Badge + VP update | Knight progression tests |
| Win at 10+ on own turn | End-game evaluation at valid checkpoints | End modal + freeze actions | End-condition tests |

## API Contracts

### REST APIs

| Method | Path | Body | Response | Auth |
|--------|------|------|----------|------|
| POST | /api/auth/register | email, password, nickname | user, tokens | public |
| POST | /api/auth/login | email, password | user, tokens | public |
| POST | /api/rooms | optional roomName | roomCode, room | user |
| POST | /api/rooms/{roomCode}/join | - | room, seat | user |
| POST | /api/rooms/{roomCode}/ready | ready=true/false | room | user |
| POST | /api/rooms/{roomCode}/start | - | gameId | host |
| GET | /api/games/{gameId} | - | projected game state | player in game |
| GET | /api/games/{gameId}/events?fromSeq=n | - | events[] | player in game |
| GET | /api/games/{gameId}/history | - | summary | player in game |

### WebSocket Commands (client -> server)

| Command | Payload | Validation |
|---------|---------|------------|
| roll_dice | gameId | active player + PRE_ROLL |
| place_road | edgeId | ownership, resources, phase |
| place_settlement | nodeId | distance rule, connection, resources |
| upgrade_city | nodeId | own settlement, resources |
| buy_dev_card | - | resources, deck not empty |
| play_dev_knight | targetHex, targetPlayer? | card available + timing + robber rules |
| play_dev_road_building | edgeA, edgeB | card available + placement valid |
| play_dev_year_of_plenty | resourceA, resourceB | card available |
| play_dev_monopoly | resourceType | card available |
| propose_trade | toPlayers, give, want | active player only |
| answer_trade | offerId, accept/reject | addressed player only |
| maritime_trade | giveType, giveCount, getType | ratio validation |
| move_robber | targetHex, targetPlayer? | robber flow active |
| discard_resources | selectedCards | required player in discard queue |
| end_turn | - | active player, phase allows |

### WebSocket Events (server -> client)

| Event | Purpose |
|-------|---------|
| game_state_snapshot | canonical projected state |
| game_event_applied | incremental event feed |
| turn_changed | active player updates |
| dice_rolled | roll result |
| resources_produced | production summary |
| robber_triggered | start robber flow |
| discard_required | player must discard |
| trade_offer_created | offer broadcast |
| trade_offer_updated | accepted/rejected/expired |
| longest_road_updated | award transfer updates |
| largest_army_updated | award transfer updates |
| victory_declared | game end |
| player_reconnected | presence signal |

## Component Design (Frontend)

### Component Tree

App
|- AuthLayout
|  |- LoginPage
|  |- RegisterPage
|- LobbyLayout
|  |- RoomListPage
|  |- RoomPage
|  |  |- SeatGrid
|  |  |- ReadyPanel
|  |  |- ChatPanel
|- GameLayout
|  |- GameHeader
|  |- TurnPhasePanel
|  |- BoardCanvas
|  |  |- HexGrid
|  |  |- NumberTokens
|  |  |- RobberToken
|  |  |- RoadsLayer
|  |  |- BuildingsLayer
|  |  |- PortsLayer
|  |- PlayerHandPanel
|  |- BuildPanel
|  |- TradePanel
|  |- DevCardsPanel
|  |- AwardsPanel
|  |- ActionLogPanel
|  |- DiscardModal
|  |- RobberModal
|  |- EndGameModal

### State Management

- Local UI state:
  - Modal visibility, hover targets, selection intents.
- Server state:
  - Room state, game projected state, event sequence, connection status.
- Recommended store:
  - Zustand (or Redux Toolkit) + React Query for REST bootstrap.

## Visual Fidelity Strategy (Own Art)

V1 will use original in-house artwork while preserving tabletop-equivalent geometry and UX behavior.

- Board geometry parity:
  - same hex-grid topology, intersection graph, edge graph, and port placement semantics as rules baseline.
  - deterministic pixel coordinate system derived from board graph IDs.
- Asset policy:
  - no official logos, icons, card frames, or artwork from CATAN/Devir.
  - all textures, icons, and card layouts created from scratch.
- Interaction parity:
  - placement affordances, token readability, and piece visibility tuned to match board-game usability.
- Calibration:
  - add overlay debug mode that draws graph IDs and hitboxes to verify click precision.
- Accessibility:
  - color + shape encoding for player ownership (not color-only distinctions).

## File Structure

```text
catan/
  apps/
    web/
      src/
        app/
        pages/
        components/
        features/
          auth/
          lobby/
          game/
            board/
            hand/
            trade/
            devcards/
        assets/
          board/
          pieces/
          cards/
          icons/
        lib/
          api/
          ws/
          state/
        test/
    server/
      src/main/java/com/catan/
        auth/
        lobby/
        game/
          domain/
          engine/
          commands/
          events/
          projections/
          validations/
        websocket/
        persistence/
      src/main/resources/
        db/migration/
      src/test/java/com/catan/
  infra/
    docker/
    k8s/
    monitoring/
  docs/
    rules/
    adr/
    api/
```

## Dependencies

- Backend:
  - `spring-boot-starter-web`
  - `spring-boot-starter-security`
  - `spring-boot-starter-validation`
  - `spring-boot-starter-websocket`
  - `spring-boot-starter-data-jpa`
  - `postgresql`
  - `flyway-core`
  - `jjwt` (or spring oauth resource server if external IdP)
  - `micrometer-registry-prometheus`

- Frontend:
  - `react`, `react-dom`, `typescript`, `vite`
  - `zustand` (or `@reduxjs/toolkit`)
  - `@tanstack/react-query`
  - `zod`
  - `pixi.js` for board rendering and sprite batching
  - `vitest`, `testing-library`, `playwright`

## Security and Anti-Cheat

- Validate command actor against authenticated player identity.
- Validate command against game phase and turn ownership.
- Idempotency key for client retries.
- Hide opponent hands and unrevealed dev cards in all projections.
- Persist append-only event log for dispute resolution.

## Observability and Operations

- Structured logs with request/game correlation IDs.
- Metrics:
  - command validation failures by type
  - websocket connected sessions
  - action processing latency
  - room counts and in-progress games
- Tracing:
  - command received -> validated -> event stored -> broadcast.

## Testing Strategy

- Unit:
  - every rule validator (distance, costs, robber, dev timing, awards).
- Property/algorithmic:
  - longest road computation on generated graph variants.
- Integration:
  - websocket command flow with 4 players.
- E2E:
  - full game path from lobby to win condition.
- Regression:
  - rule matrix golden tests sourced from PDF baseline.

## Expansion Readiness (Post-V1)

Although V1 is base game for 4 players, design must keep extension points for:
- 5-6 player expansion,
- scenario boards (New York, Mallorca),
- optional helper modules.

This is implemented by pluggable `RuleSet` and `BoardPreset` abstractions.
