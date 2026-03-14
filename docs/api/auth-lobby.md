# Auth, Lobby, and Game Sync API

## Auth

### POST /api/auth/register
- Body: `{ "email": string, "password": string, "nickname": string }`
- Response: `{ "user": { "id", "email", "nickname" }, "accessToken" }`

### POST /api/auth/login
- Body: `{ "email": string, "password": string }`
- Response: `{ "user": { "id", "email", "nickname" }, "accessToken" }`

### GET /api/auth/me
- Auth: `Authorization: Bearer <token>`
- Response: `{ "id", "email", "nickname" }`

## Lobby

All endpoints require `Authorization: Bearer <token>`.

### POST /api/rooms
- Body (optional): `{ "name": string }`
- Response: room object with seats.

### POST /api/rooms/{roomCode}/join
- Joins room if WAITING and not full.

### POST /api/rooms/{roomCode}/ready
- Body: `{ "ready": boolean }`
- Marks caller readiness.

### POST /api/rooms/{roomCode}/start
- Host only.
- Requires exactly 4 joined players and all ready.
- Response: `{ "gameId": uuid }`

### GET /api/rooms/{roomCode}
- Returns current room projection.

## WebSocket

### Endpoint
- `ws://<host>/ws` (STOMP)

### Auth Handshake Header
- Native header `Authorization: Bearer <token>` must be sent on `CONNECT`.

## Game Command Channel (Initial)

### STOMP destination
- Client sends commands to: `/app/game.command`
- Server ACKs to user queue: `/user/queue/ack`

### CommandEnvelope
```json
{
  "commandId": "uuid-or-client-id",
  "gameId": "uuid",
  "type": "roll_dice",
  "sentAt": "2026-03-14T03:00:00Z",
  "payload": {}
}
```

### ACK payload (initial stub)
```json
{
  "status": "accepted",
  "gameId": "...",
  "commandId": "...",
  "actorUserId": "...",
  "type": "..."
}
```

### Currently wired command types
- `roll_dice`
  - Enforces active player + `PRE_ROLL` phase.
  - Rolls 2d6, applies production unless result is `7`.
  - On `7`, activates discard/robber flow.
- `discard_resources`
  - Valid only while discard flow is active.
  - Payload shape:
    ```json
    {
      "cards": {
        "WOOD": 1,
        "GRAIN": 2
      }
    }
    ```
- `move_robber`
  - Valid only while robber flow is active.
  - Payload shape:
    ```json
    {
      "targetHexIndex": 4,
      "targetPlayerId": "optional-uuid"
    }
    ```
- `play_dev_knight`
  - Active player only.
  - Consumes knight card, increments played knights, starts robber flow.
- `play_dev_progress`
  - Active player only.
  - Dispatches progress effect (`ROAD_BUILDING`, `YEAR_OF_PLENTY`, `MONOPOLY`).
- `propose_trade`
  - Active player only.
  - Creates a targeted domestic offer.
- `answer_trade`
  - Only targeted players can accept/reject an open offer.
- `maritime_trade`
  - Supports 4:1, 3:1 and 2:1 ratios with port entitlement validation.
- `end_turn`
  - Active player only.
  - Allowed during `TRADING`/`BUILDING`, rotates turn, checks win condition.
- `setup_init`
  - Initializes setup model with snake order.
- `setup_place_settlement`
  - Setup-only action; enforces setup settlement legality.
- `setup_place_road`
  - Setup-only action; road must connect to just-placed setup settlement.
- `build_road`
  - Active player only.
  - Allowed during `TRADING`/`BUILDING`.
  - Enforces setup completion, special-flow guard, resources, and connectivity.
  - Payload shape:
    ```json
    {
      "edgeIndex": 12
    }
    ```
- `build_settlement`
  - Active player only.
  - Allowed during `TRADING`/`BUILDING`.
  - Enforces setup completion, special-flow guard, resources, distance rule, and own-road connection.
  - Payload shape:
    ```json
    {
      "nodeIndex": 21
    }
    ```
- `build_city`
  - Active player only.
  - Allowed during `TRADING`/`BUILDING`.
  - Enforces setup completion, special-flow guard, ownership, and city upgrade cost.
  - Payload shape:
    ```json
    {
      "nodeIndex": 21
    }
    ```
- `buy_dev_card`
  - Active player only.
  - Allowed during `TRADING`/`BUILDING`.
  - Enforces setup completion, special-flow guard, deck availability, and dev-card purchase cost.
  - Payload shape:
    ```json
    {}
    ```

### ACK payload (current)
- Base ACK includes previous fields plus `seq` (monotonic game event sequence).
- Command-specific fields are included per command (for example `roll`, `offerId`, `nextActivePlayerId`).

## Game Sync API

All endpoints require `Authorization: Bearer <token>` and caller must belong to the game.

### GET /api/games/{gameId}
- Returns latest player-scoped projected snapshot with sequence cursor.
- Response:
  - `gameId`
  - `lastSequence`
  - `state`:
    - Existing scalar fields (phase, active player, turn, flow, winner, etc.)
    - `players[]` with hidden-info masking for opponents
    - `board`:
      - `hexes[]` (`hexIndex`, `terrain`, `numberToken`, `hasRobber`, `x`, `y`, `z`)
      - `intersections[]` (`nodeIndex`, adjacency, owner/building, `x`, `y`, `z`)
      - `edges[]` (`edgeIndex`, nodes, owner, endpoints `x/y/z`, adjacentHexes)
      - `ports[]` (`portIndex`, `edgeIndex`, `ratio`, `resourceType`)
    - `legalActions[]`:
      - `actionType`, `enabled`, `allowedNodeIndexes[]`, `allowedEdgeIndexes[]`, `allowedHexIndexes[]`

### GET /api/games/{gameId}/events?fromSeq={n}
- Returns events with `seq > fromSeq` for incremental catch-up.
- Response:
  - `gameId`
  - `fromSequence`
  - `lastSequence`
  - `events[]` (seq, type, actorPlayerId, payload, createdAt)

## Persistence and Audit

- Event pipeline persists one `game_events` row per accepted command with monotonic sequence.
- Snapshot checkpoints are persisted in `game_snapshots` for reconnection and replay.
- Command audit trail persists in `game_audit` for accepted and rejected commands, including validation failures.
