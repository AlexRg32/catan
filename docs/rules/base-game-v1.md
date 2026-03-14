# Catan Online Rules Spec (Base Game V1)

## Source of Truth
- Official rules PDF: https://deviramericas.com/wp-content/uploads/2016/12/Catan-Plus-reglas.pdf
- Plan artifacts: `.orchestrator/plans/2026-03-14-02-34-think-catan-online-full/`

## Scope
This document defines the base-game rules for 4-player online Catan V1.

Out of scope in V1:
- 5-6 player expansion
- New York / Mallorca scenarios
- Helpers mini-expansion

## Core Rules

1. Victory
- First player to reach 10 or more victory points on their own turn wins.

2. Turn Flow
- Turn phases: roll -> trade -> build.
- Development card play allowed during own turn, except same-turn purchased card.

3. Resource Production
- Active player rolls 2 dice.
- Matching numbered hexes produce for adjacent settlements/cities.
- Settlement yields 1 resource, city yields 2 resources.
- Hex with robber produces nothing.

4. Building and Costs
- Road: clay + wood.
- Settlement: clay + wood + wool + grain.
- City: 3 ore + 2 grain (upgrade from own settlement only).
- Development card: ore + wool + grain.

5. Placement Constraints
- Settlement distance rule: no adjacent settlements/cities.
- New settlement requires connection to own road.
- Road must connect to own road/settlement/city and respect interruption rules.

6. Trading
- Domestic trade: only active player can trade with other players.
- Maritime trade: 4:1 default.
- Ports: 3:1 generic or 2:1 specific resource.

7. Robber and Dice 7
- On roll 7: no production.
- Any player with more than 7 resources discards half (rounded down).
- Active player moves robber to a different hex.
- Active player may steal 1 random resource from one adjacent opponent.

8. Awards
- Longest Road: awarded at contiguous length >= 5, transfers when surpassed, worth 2 VP.
- Largest Army: first to 3 played knights, transfers when surpassed, worth 2 VP.

9. Development Cards
- Knight: robber movement and steal flow.
- Progress cards: execute printed effect.
- Victory Point cards: remain hidden until revealed for victory.

## Engineering Requirements
- Server-authoritative validation for every command.
- Hidden information must be masked in per-player projections.
- Event log + snapshots required for replay and dispute auditing.

## Visual and IP Policy (V1)
- V1 uses original in-house artwork only.
- Gameplay layout and interaction model follow base-game rules parity.
- Official logos, card art, board art, and branded assets are excluded until explicit license approval.
