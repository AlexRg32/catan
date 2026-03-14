# Investigation: Catan Online 4 Players (React + Spring)

## Summary

Goal: deliver a complete implementation plan for an online 4-player Catan game that behaves like the board game, with authoritative server-side rules, real-time multiplayer, reconnection support, anti-cheat guarantees, and production-ready operations.

The rules baseline is extracted from the official PDF provided by the user:
- Source: https://deviramericas.com/wp-content/uploads/2016/12/Catan-Plus-reglas.pdf
- Local extraction: `.orchestrator/tmp/catan-plus-reglas.txt`

## Current State

- **Tech Stack**: No app code exists yet in this repository.
- **Relevant Code**: Only orchestration assets (`.agent`, `.orchestrator`) are present.
- **Architecture**: Greenfield project.

## What Is Requested

Build a complete online implementation blueprint for Catan with exactly 4 connected players in one match, board-game-equivalent gameplay, React frontend, and Spring backend when needed.

## Why It Is Needed

- Competitive and fair multiplayer needs an authoritative backend.
- Rules parity with the physical game needs strict validations and deterministic state transitions.
- The project needs a full roadmap, not only technical fragments.

## Where It Fits

Affected areas (to be created):
- Frontend: React game client, lobby, board rendering, player actions, websocket sync.
- Backend: Spring Boot API + websocket command gateway + authoritative game engine.
- Data: PostgreSQL persistence, optional Redis for ephemeral room state/performance.
- Infra: CI/CD, observability, deployment, runtime security.

## What Can Be Reused

- Existing `.agent` and `.orchestrator` workflows can drive implementation (`/forge execute`) once this plan is approved.
- No reusable game/application code currently exists.

## Rules Baseline Captured From PDF

The following rules are explicitly extracted and must be implemented as requirements:

1. Base board and goal
- 19 terrain hexes and 1 desert.
- First player to 10 victory points wins.

2. Resource production
- Active player rolls 2 dice at start of turn.
- Matching terrain numbers produce for all adjacent owners.
- Settlement yields 1 resource, city yields 2 resources.
- Terrain blocked by robber produces nothing.

3. Turn order and phases
- Canonical turn flow: roll -> trade -> build.
- Development cards can be played at any point in own turn (including before roll), except cards bought that same turn.

4. Trading
- Domestic trade only between active player and other players.
- Maritime trade with bank allowed at 4:1.
- Ports allow 3:1 generic and 2:1 specific-resource trades.

5. Building costs and constraints
- Road: clay + wood.
- Settlement: clay + wood + wool + grain.
- City: 3 ore + 2 grain (upgrade from existing settlement only).
- Development card: ore + wool + grain.
- Settlement distance rule: no adjacent settlements/cities on neighboring intersections.
- New settlement needs connection to own road.
- Roads must connect from own road/settlement/city and cannot pass through opponent occupied interruption points.

6. Longest Road
- Award at length >= 5 contiguous roads.
- Branches do not stack as one path.
- Opponent settlement/city can break continuity.
- Card worth 2 victory points; transfers immediately when surpassed.

7. Robber and roll of 7
- On roll 7: no production this turn.
- Players with >7 resource cards discard half (rounded down for odd hand sizes).
- Active player must move robber to another terrain.
- Active player may steal 1 random resource from one adjacent opponent.

8. Development cards and Largest Army
- Knight card moves robber (same steal rules).
- Largest Army starts at 3 played knights.
- Largest Army worth 2 victory points; transfers when another player has more played knights.
- Victory Point development cards remain hidden until needed to reach win threshold.

9. End game condition
- Game ends when player has 10+ points on own turn (either already at turn start or reached during turn).

## Scope

### In Scope (V1)

- 4-player online base game parity with rules above.
- Real-time multiplayer with reconnect and anti-cheat guarantees.
- Ranked-ready architecture (rating can be disabled initially).
- Production-grade observability and release process.
- Own visual asset pack with board-game-equivalent layout (no official CATAN/Devir artwork).

### Out of Scope (V1)

- Expansions/scenarios from Catan Plus beyond base game rules (5-6 players, New York, Mallorca, helpers).
- Native mobile apps (web responsive only).
- AI bots as mandatory feature (optional future enhancement).
- Use of official licensed board/card/piece artwork and logos.

## Non-Functional Requirements

- **Fairness**: server-authoritative commands, no client-trusted game state.
- **Latency**: p95 action-to-ack under 250ms in EU region target.
- **Reliability**: reconnect in-progress match within 30 seconds without desync.
- **Security**: authenticated websocket sessions, command authorization by role and turn.
- **Auditability**: event log to replay and debug disputes.
- **Scalability**: support many concurrent rooms (horizontal backend pods).
- **Accessibility**: keyboard-navigable core actions and readable contrast.

## Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| Ambiguity in edge-case interpretations | High | Encode rule matrix from PDF and add golden tests per rule |
| Client-side cheating attempts | High | Server-authoritative validations for every command |
| Longest road algorithm bugs | High | Graph-based path tests with many branching fixtures |
| Reconnect race conditions | High | Versioned snapshots + event sequence replay |
| Trade flow deadlocks | Medium | Explicit offer lifecycle (open/accept/reject/expire) |
| Hidden information leaks | High | Per-player state projection on outbound websocket payloads |
| Rule changes later for expansions | Medium | Modular rule engine with pluggable scenario packs |
| Visual mismatch vs tabletop expectations | Medium | Add board-geometry golden fixtures and own-art style guide with fixed ratios |

## Recommendation

Use:
- **Frontend**: React + TypeScript + Vite + state store + websocket client.
- **Backend**: Spring Boot (Java 21), websocket messaging, PostgreSQL, Redis optional.
- **Engine**: deterministic authoritative rules engine with command handlers and event sourcing semantics.

Spring is necessary for this product scope because fairness, anti-cheat, and multi-user consistency require a trusted server runtime.
