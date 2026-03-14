# Catan Online V1 - Own Art Style Guide

## Direction
- Visual goal: warm tabletop mood, handcrafted but clean.
- Rule parity first: all visual decisions must preserve readability of official gameplay states.
- IP policy: no official Catan logos, card illustrations, or board textures.

## Color System
- Background sand: `#f2e7cf`
- Panel cream: `#fbf6e9`
- Ink dark: `#2a241a`
- Accent clay: `#ba5f1d`
- Accent deep: `#974611`
- Border straw: `#d8c8a7`

### Terrain Palette
- Wood: `#4f8c4b`
- Wool/Pasture: `#8fbe64`
- Grain/Fields: `#d8b24c`
- Clay/Hills: `#b56b45`
- Ore/Mountains: `#7d7f91`
- Desert: `#d6c396`

## Typography
- Display/labels: `Bree Serif`
- UI/body: `Alegreya Sans`
- Debug/coordinates: system monospace

## Components
- Hex cells: rounded-rect pseudo-hex with high-contrast border.
- Pieces: flat-color silhouettes with dark contour.
- Cards: neutral frame + color-coded strip, no artwork in V1.
- Tokens: circular chips, center number with color cues (red emphasis for 6/8 optional future).

## 3D Board UX (Beta)
- Feature flag: `VITE_ENABLE_3D_BOARD=true` enables the 3D game shell.
- The 3D board must keep rule readability first: interaction affordances must reflect server-side legality (`legalActions` projection).
- Board topology source of truth is server projection (`state.board`) with stable IDs for hexes/nodes/edges.
- Primary interaction in beta: click-select on valid targets (roads, settlements, cities, robber hex).
- Drag & drop from action dock to board is enabled with legal preview and invalid-target feedback.

### Interaction Patterns (Current Beta)
- Setup flow is guided in HUD (`poblado -> carretera`) and non-valid setup actions are blocked with explicit message.
- Domestic and maritime trade use visual chips (resource and ratio selectors), avoiding raw text payload inputs.
- Robber modal uses visual hex selectors + per-hex adjacent victims list.
- Timeline uses semantic tags (`DICE`, `BUILD`, `TRADE`, etc.) and short readable action labels.

### Acceptance Checklist (UI 3D)
- [ ] No raw JSON shown for core gameplay actions.
- [x] Build actions are contextual (only legal targets highlighted/enabled).
- [x] Robber flow can be completed using board interaction only.
- [ ] Camera controls are bounded (no disorienting free-fly behavior).
- [ ] Critical actions remain readable on mobile and desktop.
- [x] Color is not the only indicator for legal/illegal interaction.

## Motion
- Keep motion purposeful: 120-180ms transitions for hover/focus.
- No continuous idle animation in board core state.

## Accessibility
- Contrast target: WCAG AA for text and critical symbols.
- Never encode game-critical state by color only; include labels/icons.
