# Design: UI 3D del juego con tablero hexagonal drag & drop

## Architecture Overview
La solución encaja como una evolución de la arquitectura actual, manteniendo backend autoritativo y reemplazando la capa visual del cliente por una escena 3D interactiva.

Flujo objetivo:
1. Backend expone snapshot enriquecido con `board` (hexes + intersections + edges + ports + ownership + legal actions por jugador/fase).
2. Frontend sincroniza snapshot/eventos como hoy, pero renderiza una `GameScene3D` con meshes y capas HUD.
3. Las acciones de usuario (drag/drop o click-select) generan comandos WS tipados; el servidor valida y responde ACK; el cliente re-sincroniza.
4. UI usa estados visuales (highlight legal, preview de coste, feedback de error contextual) en lugar de inputs textuales.

## Data Model (if applicable)
| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| BoardHexView | `hexIndex`, `terrain`, `numberToken`, `hasRobber`, `q`, `r`, `z` | 1:N con intersecciones adyacentes |
| BoardIntersectionView | `nodeIndex`, `adjacentHexIndexes`, `adjacentNodeIndexes`, `ownerPlayerId`, `buildingType`, `x`, `y`, `z` | N:N con aristas |
| BoardEdgeView | `edgeIndex`, `nodeA`, `nodeB`, `ownerPlayerId`, `x1,y1,z1,x2,y2,z2` | Une dos intersecciones |
| BoardPortView | `portIndex`, `ratio`, `resourceType?`, `edgeIndex` | 1:1 con borde costero |
| LegalActionView | `actionType`, `allowedNodeIndexes[]`, `allowedEdgeIndexes[]`, `constraints` | Calculada por jugador/fase |
| GameProjection (extendida) | `state` actual + `board` + `legalActions` | Snapshot consumido por escena 3D |

## API Contracts (if applicable)
| Method | Path | Body | Response | Auth |
|--------|------|------|----------|------|
| GET | `/api/games/{gameId}` | - | `GameSnapshotResponse` con `state.board` y `state.legalActions` | Bearer |
| GET | `/api/games/{gameId}/events?fromSeq=n` | - | Eventos incrementales (sin cambio estructural) | Bearer |
| WS SEND | `/app/game.command` type=`build_road` | `{ edgeIndex }` | ACK + `seq` | Bearer en CONNECT |
| WS SEND | `/app/game.command` type=`build_settlement` | `{ nodeIndex }` | ACK + `seq` | Bearer en CONNECT |
| WS SEND | `/app/game.command` type=`build_city` | `{ nodeIndex }` | ACK + `seq` | Bearer en CONNECT |
| WS SEND | `/app/game.command` type=`buy_dev_card` | `{}` | ACK + `seq` + metadatos | Bearer en CONNECT |
| WS SEND | comandos actuales (`roll_dice`, `discard_resources`, `move_robber`, `propose_trade`, `maritime_trade`, `end_turn`, setup) | payload actual | ACK actual | Bearer en CONNECT |

## Component Design (if frontend)
### Component Tree
```text
GamePage3D (container)
|- GameTopHud
|- GameScene3D
|  |- BoardHexLayer
|  |- NumberTokenLayer
|  |- PortLayer
|  |- RoadsLayer3D
|  |- BuildingsLayer3D
|  |- RobberLayer
|  |- PlacementHighlightLayer
|  |- InteractionController
|- HandAndCardsHud
|  |- ResourceHandCarousel
|  |- DevelopmentCardsTray
|- ActionDock
|  |- TurnControls
|  |- BuildDock
|  |- TradeDock
|- FlowModals
   |- DiscardFlowModal
   |- RobberFlowModal
   |- TradeOfferModal
   |- EndGameModal
```

### State Management
- Local state:
  - cámara (zoom/rotación limitada), objeto en drag, hover target, overlays temporales.
  - paneles abiertos/cerrados, feedback visual de comandos.
- Server state:
  - snapshot y eventos desde `GameStore` (se mantiene patrón actual).
  - `legalActions` para colorear hitboxes y bloquear acciones inválidas antes del envío.
- Command state:
  - cola ligera de comandos en vuelo (`pendingCommandIds`) para mostrar loading/rollback visual.

## File Structure
```text
apps/web/src/features/game/
  GamePage3D.tsx
  game3d/
    scene/
      GameScene3D.tsx
      camera.ts
      BoardHexLayer.tsx
      RoadsLayer3D.tsx
      BuildingsLayer3D.tsx
      RobberLayer.tsx
      HighlightLayer.tsx
    interaction/
      useBoardPicking.ts
      useDragPlacement.ts
      useLegalHighlights.ts
    hud/
      GameTopHud.tsx
      HandAndCardsHud.tsx
      ActionDock.tsx
      ResourceCard.tsx
      DevCard.tsx
    adapters/
      projectionToScene.ts
      commandBuilder.ts
    styles/
      game3d.css

apps/web/src/lib/types/
  gameProjection3d.ts (o extensión de index.ts)

apps/server/src/main/java/com/catan/server/game/
  projections/
    BoardHexProjection.java
    BoardIntersectionProjection.java
    BoardEdgeProjection.java
    BoardPortProjection.java
    LegalActionProjection.java
    GameProjection.java (extendido)
  engine/
    BoardGraphFactory.java
    LegalActionService.java
    BuildRoadCommandHandler.java
    BuildSettlementCommandHandler.java
    BuildCityCommandHandler.java
    BuyDevCardCommandHandler.java
  service/
    GameCommandDispatcher.java (nuevos command types)
```

## Dependencies
- Suggested new packages:
  - `three`: motor 3D base.
  - `@react-three/fiber`: integración idiomática React para escena/ciclo de render.
  - `@react-three/drei`: utilidades de cámara, controles, billboards, loaders.
  - `@use-gesture/react`: soporte táctil/ratón para drag y gestos.
- Existing packages to use:
  - `react`, `react-router-dom`, `@stomp/stompjs`, store actual y API client.

## Testing Strategy
- Unit:
  - mapeo `projection -> scene nodes`.
  - construcción de comandos desde interacción gráfica.
  - validación de `legalActions` en capas de highlight.
- Integration:
  - flujo completo snapshot -> interacción -> WS command -> resync.
  - guardas de fase y casos de rechazo (mensaje visual correcto).
- E2E:
  - partida mínima con setup, tirada, construcción y fin de turno en UI gráfica.
  - pruebas desktop y viewport móvil para drag/click alternativo.
- Visual:
  - snapshots de escena para detectar regresiones de layout/piezas.
- Performance:
  - presupuesto de FPS, tiempo de frame y recuento de draw calls por escena.

## Modularity Check
La propuesta separa lógica e interfaz: hooks/controladores (`interaction/*`, `adapters/*`) encapsulan reglas de interacción y serialización de comandos; componentes de escena y HUD son presentacionales y reciben datos ya mapeados.
