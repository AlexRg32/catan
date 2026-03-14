# Rules Reference: Catan Plus PDF -> Online Spec

## Source
- PDF: https://deviramericas.com/wp-content/uploads/2016/12/Catan-Plus-reglas.pdf
- Extracted text: `.orchestrator/tmp/catan-plus-reglas.txt`

## V1 Rules Scope (Implemented)

This V1 scope targets base Catan for 4 online players using the official rules baseline:

1. Win condition
- 10+ victory points on own turn.

2. Production
- Roll 2 dice each turn.
- Numbered tiles produce resources for adjacent settlements/cities.
- Settlement = 1 resource, city = 2 resources.
- Robber blocks tile production.

3. Turn flow
- Roll -> trade -> build.
- Dev card can be played in own turn, but not on same turn it was bought.

4. Build costs
- Road: clay + wood.
- Settlement: clay + wood + wool + grain.
- City: 3 ore + 2 grain.
- Dev card: ore + wool + grain.

5. Placement constraints
- Settlement distance rule (no adjacent settlements/cities).
- New settlement requires own-road connection.
- Road connectivity restrictions apply.

6. Trading
- Domestic: only active player trades with others.
- Maritime: 4:1 default.
- Ports: 3:1 generic, 2:1 specific resource.

7. Robber / roll 7
- No production on roll 7.
- Players with >7 cards discard half (rounded down).
- Active player moves robber and steals one random card from eligible adjacent opponent.

8. Awards
- Longest Road: min 5 contiguous roads, transfers when surpassed, worth 2 VP.
- Largest Army: first at 3 played knights, transfers on strictly greater count, worth 2 VP.

9. Dev cards
- Knight: robber action.
- Progress cards: execute specific effect.
- VP cards: hidden until needed to win.

## Out of Scope in V1 (Future Rule Packs)

- 5-6 player expansion rules.
- Scenario-specific rules (New York, Mallorca).
- Helper mini-expansion.

## Compliance Matrix

| Rule area | Backend module | Frontend behavior | Test suite |
|---|---|---|---|
| Turn order and phases | `PhaseGuard`, `TurnService` | Action gating | Phase integration tests |
| Production | `ProductionService` | Resource gain animations | Production unit tests |
| Robber | `RobberService`, `DiscardService` | Discard + robber modals | Robber integration tests |
| Building | Placement validators + `CostService` | Board highlights | Placement/cost tests |
| Trading | `TradeService`, `PortService` | Trade panel/modal | Trade integration tests |
| Dev cards | `DevCardService` | Dev card panel | Dev-card rule tests |
| Awards | `AwardService` | Awards panel | Award transfer tests |
| Hidden info | `ProjectionService` | Masked opponents | Privacy projection tests |

## Implementation Notes

- Server must remain fully authoritative.
- Every command must be validated by actor, phase, resources, and board state.
- Event log and snapshots are mandatory for replay and dispute resolution.

## Visual Asset Policy (V1)
- Use custom, non-branded artwork for board, cards, tokens, and pieces.
- Preserve gameplay geometry and interaction parity with base rules.
- Keep an asset provenance list for legal traceability.
