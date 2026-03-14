# Plan: Catan Online 4 Players (React + Spring)

> WARNING: This plan is strictly theoretical. No source code files have been modified.

> Goal: Build an online 4-player Catan experience equivalent to board-game base rules from the provided PDF.
> Architecture: React client + Spring authoritative multiplayer server + Postgres persistence (+ optional Redis).

## Foundation

- [x] **Task 1: Create monorepo skeleton** — `apps/web`, `apps/server`, `infra`, `docs`
  - What: create folder structure and root README with run commands.
  - Verify: `tree -L 2` shows expected folders.

- [x] **Task 2: Add root build scripts** — `package.json` (root) or task runner config
  - What: add scripts for lint/test/dev for both apps.
  - Verify: `npm run -s` lists workspace scripts.

- [x] **Task 3: Initialize React app** — `apps/web/*`
  - What: scaffold React + TypeScript + Vite app.
  - Verify: `npm run dev` serves default page.

- [x] **Task 4: Initialize Spring Boot app** — `apps/server/*`
  - What: scaffold Java 21 Spring Boot project with web/security/websocket/data-jpa.
  - Verify: `./mvnw test` passes empty baseline.

- [x] **Task 5: Add local Docker stack** — `infra/docker/docker-compose.yml`
  - What: add Postgres and Redis services for local development.
  - Verify: `docker compose up -d` starts both healthy.

- [x] **Task 6: Add env templates** — `.env.example`, `apps/web/.env.example`, `apps/server/.env.example`
  - What: define required variables and defaults.
  - Verify: app boots with copied `.env` files.

- [x] **Task 7: Configure backend Flyway** — `apps/server/src/main/resources/db/migration`
  - What: enable migration baseline table.
  - Verify: app startup logs show Flyway migration success.

- [x] **Task 8: Configure backend code quality** — `pom.xml`
  - What: add checkstyle/spotless/surefire/failsafe plugins.
  - Verify: `./mvnw verify` succeeds.

- [x] **Task 9: Configure frontend code quality** — `apps/web/eslint.config.*`, `tsconfig*`
  - What: strict TS + lint + format config.
  - Verify: `npm run lint` passes.

- [x] **Task 10: Add CI baseline pipeline** — `.github/workflows/ci.yml`
  - What: run backend and frontend lint/test on PR.
  - Verify: pipeline green on sample branch.

- [x] **Task 11: Add auth tables migration** — `V1__auth.sql`
  - What: create users and sessions tables.
  - Verify: DB contains expected tables.

- [x] **Task 12: Implement auth domain model** — `apps/server/.../auth/domain/*`
  - What: define user/session entities and repositories.
  - Verify: repository tests persist and fetch records.

- [x] **Task 13: Implement password hashing service** — `auth/service/PasswordService.java`
  - What: use BCrypt with configurable strength.
  - Verify: hash != plaintext and verify() true for valid password.

- [x] **Task 14: Implement JWT token service** — `auth/service/JwtService.java`
  - What: generate/verify access token with expiry and tokenVersion.
  - Verify: secured endpoint accepts valid token and rejects invalid.

- [x] **Task 15: Implement register endpoint** — `POST /api/auth/register`
  - What: create user with unique email and nickname constraints.
  - Verify: returns user DTO and tokens.

- [x] **Task 16: Implement login endpoint** — `POST /api/auth/login`
  - What: authenticate credentials and issue tokens.
  - Verify: valid login returns 200; invalid returns 401.

- [x] **Task 17: Implement websocket auth handshake** — `websocket/AuthChannelInterceptor`
  - What: validate JWT during websocket connect.
  - Verify: unauthenticated connection is rejected.

- [x] **Task 18: Add lobby tables migration** — `V2__lobby.sql`
  - What: create rooms and room_seats tables.
  - Verify: DB schema includes room constraints for max 4.

- [x] **Task 19: Implement room create API** — `POST /api/rooms`
  - What: create room with host seat assignment.
  - Verify: response includes room code and host seat.

- [x] **Task 20: Implement room join API** — `POST /api/rooms/{code}/join`
  - What: assign free seat if room not full and not started.
  - Verify: 5th join attempt returns 409.

- [x] **Task 21: Implement room ready toggle** — `POST /api/rooms/{code}/ready`
  - What: set user ready status.
  - Verify: room projection updates ready flags.

- [x] **Task 22: Implement start-game API** — `POST /api/rooms/{code}/start`
  - What: allow host to start when exactly 4 players ready.
  - Verify: creates game record and transitions room status.

- [x] **Task 23: Add game core tables migration** — `V3__game_core.sql`
  - What: create games, game_players, snapshots, events tables.
  - Verify: migration applied and FK constraints valid.

- [x] **Task 24: Add deterministic RNG service** — `game/engine/RandomService`
  - What: seed-based RNG for reproducible game setup/events.
  - Verify: same seed produces same sequence in tests.

- [x] **Task 25: Implement board preset builder** — `game/domain/BoardPreset`
  - What: build base 19-hex board with terrains, numbers, ports, desert.
  - Verify: unit test checks exact counts and robber starts on desert.

- [x] **Task 26: Implement initial game state factory** — `game/engine/GameStateFactory`
  - What: create turn order, decks, empty structures, players.
  - Verify: initial snapshot has 4 players and expected defaults.

- [x] **Task 27: Add command envelope model** — `game/commands/CommandEnvelope`
  - What: include commandId, actorId, gameId, timestamp.
  - Verify: deserialization and validation tests pass.

- [x] **Task 28: Add command idempotency store** — `game/persistence/ProcessedCommand`
  - What: persist processed command IDs per game.
  - Verify: duplicate command returns same result, no double apply.

- [x] **Task 29: Add game projection DTO baseline** — `game/projections/GameView`
  - What: define projected state contract for clients.
  - Verify: serialization includes required public fields only.

- [x] **Task 30: Add websocket game channel** — `websocket/GameWsController`
  - What: route command messages to command dispatcher.
  - Verify: command receives ACK/NACK over websocket.

## Core Rules Engine

- [x] **Task 31: Implement turn-phase guard** — `game/engine/PhaseGuard`
  - What: enforce command eligibility by phase (`PRE_ROLL`, `TRADING`, `BUILDING`).
  - Verify: out-of-phase commands return validation error.

- [x] **Task 32: Implement roll dice command** — `RollDiceCommandHandler`
  - What: only active player can roll in PRE_ROLL.
  - Verify: valid roll transitions to POST_ROLL.

- [x] **Task 33: Implement production resolver** — `ProductionService`
  - What: distribute resources by rolled number to adjacent settlements/cities.
  - Verify: settlement gets 1, city gets 2.

- [x] **Task 34: Implement robber production block** — `ProductionService`
  - What: skip production on robber-occupied tile.
  - Verify: affected adjacent owners get 0 resources.

- [x] **Task 35: Implement roll-7 trigger** — `RollDiceCommandHandler`
  - What: on roll 7, no production, open discard/robber flow.
  - Verify: state enters discard resolution with expected players.

- [x] **Task 36: Implement discard-half rule** — `DiscardResourcesCommandHandler`
  - What: players with >7 resources must discard floor(n/2).
  - Verify: hand size reduction matches rule for odd/even cases.

- [x] **Task 37: Implement robber move command** — `MoveRobberCommandHandler`
  - What: robber must move to different terrain.
  - Verify: selecting same tile is rejected.

- [x] **Task 38: Implement robber steal rule** — `MoveRobberCommandHandler`
  - What: allow steal 1 random resource from one eligible adjacent opponent.
  - Verify: exactly one random card transfer occurs.

- [x] **Task 39: Implement road cost payment** — `CostService`
  - What: require clay + wood and deduct atomically.
  - Verify: command rejected when any resource missing.

- [x] **Task 40: Implement settlement cost payment** — `CostService`
  - What: require clay + wood + wool + grain.
  - Verify: hand deducts exact cards on success.

- [x] **Task 41: Implement city upgrade cost payment** — `CostService`
  - What: require 3 ore + 2 grain.
  - Verify: exact payment and building state update.

- [x] **Task 42: Implement dev card purchase cost** — `CostService`
  - What: require ore + wool + grain.
  - Verify: paid cards removed and deck decremented.

- [x] **Task 43: Implement road placement validation** — `RoadPlacementValidator`
  - What: edge must connect to own road/settlement/city and not violate occupancy constraints.
  - Verify: invalid disconnected edge is rejected.

- [x] **Task 44: Implement settlement placement validation** — `SettlementPlacementValidator`
  - What: enforce distance rule and own-road adjacency for new settlements.
  - Verify: adjacent occupied neighbor nodes block placement.

- [x] **Task 45: Implement city upgrade validation** — `CityUpgradeValidator`
  - What: only own settlement may be upgraded to city.
  - Verify: upgrade on empty/opponent node is rejected.

- [x] **Task 46: Implement longest road algorithm** — `LongestRoadService`
  - What: compute longest contiguous path with branch handling and interruptions.
  - Verify: fixtures cover split paths and opponent blocking.

- [x] **Task 47: Implement longest road award transfer** — `AwardService`
  - What: grant at >=5 and transfer when surpassed.
  - Verify: VP updates by +2/-2 on transfer.

- [x] **Task 48: Implement dev deck composition** — `DevDeckFactory`
  - What: initialize 14 knight, 6 progress, 5 VP cards.
  - Verify: deck counts match expected totals.

- [x] **Task 49: Implement dev card ownership state** — `GamePlayerDevCards`
  - What: track hidden hand, played cards, bought turn index.
  - Verify: purchased card marked unplayable this turn.

- [x] **Task 50: Implement play-knight command** — `PlayKnightCommandHandler`
  - What: consume knight, trigger robber move and steal.
  - Verify: knight moved to played pile and robber flow executed.

- [x] **Task 51: Implement largest army award logic** — `AwardService`
  - What: first to 3 played knights gets card; transfer on strictly greater count.
  - Verify: award holder and VP update correctly.

- [x] **Task 52: Implement progress card effects framework** — `ProgressCardService`
  - What: define action dispatch for each progress type.
  - Verify: each progress card invokes correct handler.

- [x] **Task 53: Implement Road Building effect** — `ProgressRoadBuildingHandler`
  - What: place up to 2 free roads following normal placement validity.
  - Verify: invalid road targets rejected without cost deduction.

- [x] **Task 54: Implement Year of Plenty effect** — `ProgressYearOfPlentyHandler`
  - What: grant 2 selected resources to player.
  - Verify: resource hand increments exactly by selected pair.

- [x] **Task 55: Implement Monopoly effect** — `ProgressMonopolyHandler`
  - What: take all selected resource type from opponents.
  - Verify: all opponents transfer matching cards.

- [x] **Task 56: Implement hidden VP dev cards behavior** — `VictoryPointCardService`
  - What: keep VP cards hidden until owner reveals for win.
  - Verify: opponent projection never reveals card count by type.

- [x] **Task 57: Implement domestic trade offer model** — `TradeOffer` domain
  - What: create offer lifecycle (open, accepted, rejected, expired).
  - Verify: status transitions are valid and timestamped.

- [x] **Task 58: Implement domestic trade active-player restriction** — `TradeCommandValidator`
  - What: only active player can create offers.
  - Verify: non-active proposer gets validation error.

- [x] **Task 59: Implement domestic trade response rule** — `AnswerTradeHandler`
  - What: only targeted players may accept/reject.
  - Verify: untargeted player response rejected.

- [x] **Task 60: Implement maritime 4:1 trade** — `MaritimeTradeHandler`
  - What: allow 4 same resource for 1 chosen resource.
  - Verify: exact ratio enforced.

- [x] **Task 61: Implement port ownership resolver** — `PortService`
  - What: detect if player has settlement/city on specific port node.
  - Verify: port entitlement updates after build events.

- [x] **Task 62: Implement maritime 3:1 trade** — `MaritimeTradeHandler`
  - What: allow 3:1 only with generic port entitlement.
  - Verify: trade rejected without entitlement.

- [x] **Task 63: Implement maritime 2:1 specific trade** — `MaritimeTradeHandler`
  - What: allow 2:1 only for matching specific port resource.
  - Verify: wrong resource type rejected.

- [x] **Task 64: Implement end-turn command** — `EndTurnCommandHandler`
  - What: advance active player index and reset phase to PRE_ROLL.
  - Verify: turn cycles among 4 players correctly.

- [x] **Task 65: Implement end-game detector** — `WinConditionService`
  - What: declare victory when player reaches 10+ VP on own turn.
  - Verify: game status switches to FINISHED and actions lock.

- [x] **Task 66: Implement initial setup phase model** — `SetupPhaseService`
  - What: define initial placement flow and resource granting policy.
  - Verify: setup completes before normal turn phases start.

- [x] **Task 67: Implement setup placement validation** — `SetupPlacementValidator`
  - What: enforce legal first settlements/roads by setup rules.
  - Verify: illegal setup placement rejected.

- [x] **Task 68: Implement setup turn order (snake order)** — `SetupTurnOrderService`
  - What: ensure placement order follows first round then reverse round.
  - Verify: integration test checks exact actor sequence.

- [x] **Task 69: Implement setup initial resources grant** — `SetupResourceGrantService`
  - What: grant resources from second settlement adjacency.
  - Verify: expected starting hand by terrain adjacency.

- [x] **Task 70: Implement player-scoped projection filter** — `ProjectionService`
  - What: hide hidden hands/dev cards of opponents.
  - Verify: projection tests assert masked sensitive fields.

- [x] **Task 71: Implement event persistence pipeline** — `EventStoreService`
  - What: append event with monotonic seq and persist snapshot checkpoints.
  - Verify: replay from events reconstructs same state.

- [x] **Task 72: Implement room lock/concurrency guard** — `RoomExecutionLock`
  - What: serialize command processing per game room.
  - Verify: race test shows no double apply corruption.

- [x] **Task 73: Implement reconnect snapshot endpoint** — `GET /api/games/{id}`
  - What: return latest projected state + last sequence.
  - Verify: disconnected client can restore in single request.

- [x] **Task 74: Implement delta events endpoint** — `GET /api/games/{id}/events`
  - What: return events from sequence for incremental catch-up.
  - Verify: catch-up applies only missing events.

- [x] **Task 75: Implement command audit trail** — `game_audit` records
  - What: store actor, command type, validation outcome, timestamp.
  - Verify: failed command leaves audit record.

## Frontend and Real-Time UX

- [x] **Task 76: Build app shell and routing** — `apps/web/src/app/router.tsx`
  - What: define auth, lobby, and game routes with guards.
  - Verify: unauthorized users redirected to login.

- [x] **Task 77: Implement auth pages** — `features/auth/*`
  - What: login/register forms with validation and API calls.
  - Verify: successful login stores session and routes to lobby.

- [x] **Task 78: Implement lobby room list page** — `features/lobby/RoomListPage.tsx`
  - What: show create/join actions.
  - Verify: user can create room and see room code.

- [x] **Task 79: Implement room page with seats** — `features/lobby/RoomPage.tsx`
  - What: show 4 seats, readiness state, start button for host.
  - Verify: start button enabled only when all 4 ready.

- [x] **Task 80: Implement websocket client core** — `lib/ws/client.ts`
  - What: connect, reconnect backoff, event dispatch.
  - Verify: reconnect auto-resubscribes after forced disconnect.

- [x] **Task 81: Implement game store** — `lib/state/gameStore.ts`
  - What: maintain projected game state and sequence cursor.
  - Verify: snapshot + delta apply in correct order.

- [x] **Task 82: Implement board renderer** — `features/game/board/*`
  - What: render 19 hexes, number tokens, ports, robber token.
  - Verify: board matches server tile coordinates.

- [x] **Task 83: Implement road/building layers** — `RoadsLayer`, `BuildingsLayer`
  - What: render ownership by player color and type.
  - Verify: placements update immediately on server events.

- [x] **Task 84: Implement action panel** — `BuildPanel`, `TurnPhasePanel`
  - What: expose valid actions for active player and phase.
  - Verify: invalid actions disabled.

- [x] **Task 85: Implement hand/resource panel** — `PlayerHandPanel`
  - What: show own resource counts and dev card hand.
  - Verify: hidden data of opponents not displayed.

- [x] **Task 86: Implement trade UI flow** — `TradePanel`
  - What: create domestic offers and respond to incoming offers.
  - Verify: non-targeted players cannot accept offer in UI.

- [x] **Task 87: Implement maritime trade modal** — `MaritimeTradeModal`
  - What: show allowed ratios from owned ports.
  - Verify: UI ratios match server validation results.

- [x] **Task 88: Implement robber/discard modals** — `DiscardModal`, `RobberModal`
  - What: enforce discard before continuing and then robber target selection.
  - Verify: modal flow blocks other actions until resolved.

- [x] **Task 89: Implement dev card UI** — `DevCardsPanel`
  - What: buy/play actions and same-turn restriction hint.
  - Verify: bought-this-turn card disabled with tooltip.

- [x] **Task 90: Implement awards panel** — `AwardsPanel`
  - What: show Longest Road and Largest Army holders.
  - Verify: panel updates instantly on award transfer events.

- [x] **Task 91: Implement action log panel** — `ActionLogPanel`
  - What: timeline of major events (rolls, builds, steals, awards).
  - Verify: last N events visible and ordered by seq.

- [x] **Task 92: Implement game end modal** — `EndGameModal`
  - What: show winner and final scores at victory event.
  - Verify: no further action commands can be sent.

- [x] **Task 93: Implement reconnect resync strategy** — `lib/ws/resync.ts`
  - What: fetch snapshot + missed events after reconnect.
  - Verify: desync test recovers to exact server state.

- [x] **Task 94: Implement API error UX** — `lib/ui/toast.ts`
  - What: map backend validation errors to human-readable messages.
  - Verify: invalid action shows specific rule reason.

- [x] **Task 95: Add responsive layout behavior** — `GameLayout.css`
  - What: support desktop and tablet without breaking board interactions.
  - Verify: viewport tests for 1280 and 768 widths pass.

- [x] **Task 121: Define own-art visual style guide** — `docs/ui/style-guide.md`
  - What: define color system, icon language, board ratios, and card visual rules without official branding.
  - Verify: guide includes palette, typography, and component references.

- [x] **Task 122: Create board terrain sprite set (original art)** — `apps/web/src/assets/board/*`
  - What: produce custom terrain textures for all base terrain types + desert.
  - Verify: each terrain type renders with unique texture in board preview.

- [x] **Task 123: Create number token and harbor icon assets** — `apps/web/src/assets/board/*`
  - What: create original number tokens and maritime trade markers.
  - Verify: all required token numbers and port types are present.

- [x] **Task 124: Create player piece sprite set (original art)** — `apps/web/src/assets/pieces/*`
  - What: create road, settlement, and city pieces for all 4 player colors.
  - Verify: board renders all piece variants with strong contrast.

- [x] **Task 125: Create development and resource card frames (original art)** — `apps/web/src/assets/cards/*`
  - What: create non-branded card visuals for resource and development cards.
  - Verify: card panels render all card types with consistent sizing.

- [x] **Task 126: Implement frontend asset atlas loader** — `apps/web/src/features/game/board/assetLoader.ts`
  - What: load and cache board/piece/card sprites through a single atlas manifest.
  - Verify: initial game render completes with one manifest load flow.

- [x] **Task 127: Add board geometry overlay mode** — `apps/web/src/features/game/board/debugOverlay.ts`
  - What: add optional overlay to show node/edge/hex IDs and hitboxes for parity validation.
  - Verify: debug toggle displays IDs matching server graph references.

- [x] **Task 128: Add asset provenance manifest** — `docs/ui/asset-provenance.md`
  - What: track that all visual assets are original/in-house and list creation sources.
  - Verify: manifest covers each asset folder and author/source notes.

## Security, Quality, and Testing

- [x] **Task 96: Add backend unit tests for cost rules** — `CostServiceTest`
  - What: cover all build/dev costs and insufficient resources.
  - Verify: all cost cases tested.

- [x] **Task 97: Add backend unit tests for placement rules** — `PlacementValidatorTest`
  - What: road connectivity, settlement distance, city upgrade ownership.
  - Verify: invalid fixtures fail as expected.

- [x] **Task 98: Add backend unit tests for robber rules** — `RobberFlowTest`
  - What: roll 7 discard, robber move, steal behavior.
  - Verify: all branches covered.

- [x] **Task 99: Add backend unit tests for dev card timing** — `DevCardTimingTest`
  - What: ensure bought card cannot be played in same turn.
  - Verify: same-turn play is rejected.

- [x] **Task 100: Add longest road algorithm fixtures** — `LongestRoadServiceTest`
  - What: include branches, loops, interruption by opponent settlement.
  - Verify: expected length for each fixture.

- [x] **Task 101: Add largest army tests** — `AwardServiceTest`
  - What: threshold and transfer rules.
  - Verify: award moves only on strictly greater played knights.

- [x] **Task 102: Add projection privacy tests** — `ProjectionServiceTest`
  - What: assert opponent hidden cards are not exposed.
  - Verify: projection payload omits sensitive fields.

- [x] **Task 103: Add websocket integration test harness** — `GameWsIntegrationTest`
  - What: simulate 4 authenticated players sending commands.
  - Verify: synchronized event sequence across all clients.

- [x] **Task 104: Add full game integration scenario** — `GameFlowIntegrationTest`
  - What: run deterministic mini-game until win condition.
  - Verify: winner reaches >=10 VP on own turn.

- [x] **Task 105: Add frontend component tests** — `apps/web/src/**/*.test.tsx`
  - What: validate critical panels (trade, discard, end game).
  - Verify: tests pass in CI.

- [x] **Task 106: Add frontend websocket store tests** — `gameStore.test.ts`
  - What: snapshot and delta application order.
  - Verify: state remains consistent after sequence gaps.

- [x] **Task 107: Add E2E flow test** — `apps/web/e2e/game.spec.ts`
  - What: user login -> room -> game start -> basic actions.
  - Verify: E2E run passes in headless CI.

- [x] **Task 108: Add load test script** — `infra/load/k6-room-actions.js`
  - What: benchmark concurrent rooms and action throughput.
  - Verify: p95 latency and error rates within thresholds.

- [x] **Task 109: Add security tests for authz** — `SecurityIntegrationTest`
  - What: forbid non-player commands in room/game.
  - Verify: unauthorized command returns forbidden.

- [x] **Task 110: Add replay consistency test** — `EventReplayTest`
  - What: rebuild state from event log equals latest snapshot.
  - Verify: deep-equality assertion passes.

## Operations and Delivery

- [x] **Task 111: Add structured logging config** — `logback-spring.xml`
  - What: include correlation IDs and player/game identifiers.
  - Verify: logs contain traceable fields.

- [x] **Task 112: Add Prometheus metrics** — `MetricsConfig`
  - What: instrument command latency and validation failures.
  - Verify: `/actuator/prometheus` exposes custom metrics.

- [x] **Task 113: Add tracing instrumentation** — OpenTelemetry config
  - What: trace command lifecycle across layers.
  - Verify: trace appears in local collector.

- [x] **Task 114: Add dashboard templates** — `infra/monitoring/grafana/*`
  - What: dashboards for active rooms, WS connections, latency.
  - Verify: dashboards render populated series.

- [x] **Task 115: Add alert rules** — `infra/monitoring/alerts/*`
  - What: alert on high error rates and websocket disconnect spikes.
  - Verify: synthetic alert test triggers notification.

- [x] **Task 116: Add Dockerfiles for apps** — `apps/web/Dockerfile`, `apps/server/Dockerfile`
  - What: produce production-ready images.
  - Verify: images build and run locally.

- [x] **Task 117: Add deployment manifests** — `infra/k8s/*`
  - What: define deployments/services/ingress for web and server.
  - Verify: manifests validate with `kubectl apply --dry-run=client`.

- [x] **Task 118: Add release checklist doc** — `docs/release-checklist.md`
  - What: define pre-release verification steps.
  - Verify: checklist includes gameplay, infra, rollback checks.

- [x] **Task 119: Add incident runbook** — `docs/runbook.md`
  - What: steps for desync incident, rollback, data repair.
  - Verify: runbook reviewed against simulated incident.

- [x] **Task 120: Add versioned rules spec doc** — `docs/rules/base-game-v1.md`
  - What: codify PDF-derived rules and engine mapping with examples.
  - Verify: each rule references corresponding validator/test.

## Validation Before Forge

- [x] Every task has What + Verify.
- [x] Tasks are in dependency order.
- [x] Tasks are atomic and suitable for short implementation cycles.
- [x] Official PDF rules are represented in engine, UX, and tests.
