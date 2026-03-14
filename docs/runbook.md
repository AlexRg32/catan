# Incident Runbook

## Incident: Game Desync Between Clients

### Symptoms
- Players see different active turn/phase.
- Commands accepted for one player but not reflected for others.

### Immediate Actions
1. Freeze impacted room by preventing new commands at gateway level.
2. Capture latest `game_events`, `game_snapshots`, and `game_audit` rows for the game.
3. Collect logs filtered by `corr`, `game`, and `cmd` fields.

### Diagnosis
1. Compare latest snapshot sequence with latest event sequence.
2. Replay events from the latest snapshot checkpoint and compare resulting state.
3. Inspect rejected commands in `game_audit` for authorization/validation mismatch.

### Recovery
1. If replay is consistent: force all clients to fetch `/api/games/{id}` and deltas from returned sequence.
2. If replay diverges: mark game as maintenance-locked and restore from last known good snapshot/event checkpoint.
3. Notify players and create replacement room if state repair is not safe.

### UI 3D-Specific Safeguards
1. If a rendering issue is isolated to web clients while server state is healthy, disable 3D shell by setting `VITE_ENABLE_3D_BOARD=false` and redeploy web.
2. Validate that `/api/games/{id}` still returns `state.board` and `state.legalActions`; if missing, treat as backend contract regression.
3. Confirm command payloads for `build_road`, `build_settlement`, `build_city`, `buy_dev_card` are being accepted/rejected with expected reasons in `game_audit`.
4. If browser shows repeated `WebSocket connection to ws://.../ws failed`, verify handshake endpoint security:
   - `/ws` and `/ws/**` must be permitted at HTTP filter level.
   - STOMP CONNECT authentication is still enforced by `AuthChannelInterceptor` with `Authorization: Bearer <token>`.

### Rollback
1. Roll back to previous server image tag.
2. Run smoke checks on auth, lobby, and command dispatch.
3. Re-enable room traffic progressively.

### Postmortem Data
- Correlation IDs for affected command window.
- Snapshot/event diff artifact.
- Time to detect, mitigate, and recover.
