# Plan: Implementación UI 3D completa del juego

> WARNING: Este plan es estrictamente teórico. No se ha modificado ningún archivo de código fuente.

> Goal: entregar una experiencia de juego 100% gráfica en 3D (tablero, piezas, cartas y acciones), reemplazando la interacción textual en la pantalla de partida.
> Architecture: backend autoritativo con snapshot enriquecido + frontend React 3D por capas (scene/hud/interaction/adapters).

## Foundation

- [x] **Task 1: Definir criterios de aceptación UX 3D** — `docs/ui/style-guide.md`, `.orchestrator/plans/2026-03-14-04-53-think-catan-3d-ui/`
  - What: documentar checklist de “sin texto crudo”, drag/drop, legibilidad y paridad de reglas.
  - Verify: checklist firmada y referenciada por tareas de desarrollo.

- [x] **Task 2: Añadir feature flag de activación 3D** — `apps/web/.env.example`, `apps/web/src/features/game/GamePage.tsx`
  - What: crear `VITE_ENABLE_3D_BOARD` para activar UI nueva sin romper flujo actual.
  - Verify: con flag `off` sigue UI actual; con flag `on` carga contenedor 3D.

- [x] **Task 3: Crear geometría canónica del tablero** — `apps/server/src/main/java/com/catan/server/game/engine/BoardGraphFactory.java`
  - What: definir hexes, nodos e aristas con IDs estables + coordenadas para proyección.
  - Verify: tests confirman conteos esperados (19 hex, nodos/aristas válidos, conectividad).

- [x] **Task 4: Inicializar runtime con nodos/aristas reales** — `GameRuntimeInitializer.java`
  - What: sustituir inicialización vacía de `intersections`/`edges` por grafo canónico.
  - Verify: snapshot inicial ya contiene nodos y aristas no vacíos.

- [x] **Task 5: Modelar proyección de tablero (DTO backend)** — `apps/server/src/main/java/com/catan/server/game/projections/*`
  - What: crear records de `BoardHexProjection`, `BoardIntersectionProjection`, `BoardEdgeProjection`, `BoardPortProjection`.
  - Verify: serialización JSON incluye estructura de tablero completa.

- [x] **Task 6: Extender `GameProjection` con `board`** — `GameProjection.java`, `ProjectionService.java`
  - What: añadir campo `board` y mapear ownership/estado actual (robber, edificios, carreteras).
  - Verify: `GET /api/games/{id}` retorna `board` con estado consistente.

- [x] **Task 7: Exponer acciones legales por jugador/fase** — `LegalActionService.java`, `ProjectionService.java`
  - What: calcular `allowedNodeIndexes`/`allowedEdgeIndexes` y constraints según fase/flow.
  - Verify: jugador activo recibe acciones permitidas; no activos reciben listas vacías/bloqueadas.

- [x] **Task 8: Añadir comandos de construcción faltantes** — `BuildRoadCommandHandler.java`, `BuildSettlementCommandHandler.java`, `BuildCityCommandHandler.java`, `BuyDevCardCommandHandler.java`
  - What: implementar handlers con validación de coste, fase y reglas de colocación.
  - Verify: tests de aceptación/rechazo por cada comando.

- [x] **Task 9: Registrar nuevos command types en dispatcher** — `GameCommandDispatcher.java`
  - What: wire de `build_road`, `build_settlement`, `build_city`, `buy_dev_card` con ACK consistente.
  - Verify: comando válido genera `seq`; inválido queda auditado como `REJECTED`.

- [x] **Task 10: Actualizar tipos frontend para snapshot 3D** — `apps/web/src/lib/types/index.ts`
  - What: tipar `board`, `legalActions`, ownership de nodos/aristas/hex.
  - Verify: `tsc -b` sin `any` ni castings peligrosos.

- [x] **Task 11: Añadir adaptadores snapshot->escena** — `apps/web/src/features/game/game3d/adapters/projectionToScene.ts`
  - What: transformar DTO en estructuras listas para render/picking.
  - Verify: unit test con fixtures confirma mapping determinista.

- [x] **Task 12: Pruebas backend de proyección y comandos nuevos** — `apps/server/src/test/java/com/catan/server/game/*`
  - What: cubrir snapshot con board + comandos build.
  - Verify: `./mvnw -q test` verde para suite de juego.

## Core

- [x] **Task 13: Crear contenedor `GamePage3D`** — `apps/web/src/features/game/GamePage3D.tsx`
  - What: componer escena, HUD y modales en sustitución de paneles textuales.
  - Verify: ruta `/game/:id` renderiza shell 3D sin errores.

- [x] **Task 14: Montar escena base con cámara controlada** — `game3d/scene/GameScene3D.tsx`, `game3d/scene/camera.ts`
  - What: configurar cámara isométrica/semilibre, luces y límites de zoom/rotación.
  - Verify: interacción fluida de cámara en desktop y táctil.

- [x] **Task 15: Renderizar capa de hexágonos 3D** — `game3d/scene/BoardHexLayer.tsx`
  - What: crear mallas hexagonales con material por terreno y relieve ligero.
  - Verify: 19 hexes visibles y ordenados según layout oficial base.

- [x] **Task 16: Renderizar fichas numéricas y desierto** — `NumberTokenLayer.tsx`
  - What: colocar chips/tokens sobre hexes correctos, omitiendo token en desierto.
  - Verify: números coinciden con preset backend.

- [x] **Task 17: Renderizar ladrón sobre hex activo** — `RobberLayer.tsx`
  - What: mostrar pieza de ladrón en `hex.hasRobber` con highlight contextual.
  - Verify: mover ladrón actualiza posición tras ACK + resync.

- [x] **Task 18: Renderizar puertos y ratios** — `PortLayer.tsx`
  - What: dibujar puertos 3:1/2:1 orientados al borde costero.
  - Verify: puertos visibles y legibles sin solapar piezas.

- [x] **Task 19: Renderizar carreteras por ownership** — `RoadsLayer3D.tsx`
  - What: instanciar carreteras por color de jugador sobre aristas ocupadas.
  - Verify: cada `edge.ownerPlayerId` tiene representación única.

- [x] **Task 20: Renderizar poblados/ciudades por ownership** — `BuildingsLayer3D.tsx`
  - What: instanciar settlement/city por tipo y jugador en nodos ocupados.
  - Verify: upgrade a ciudad reemplaza visual de settlement.

- [x] **Task 21: Implementar picking de nodos/aristas/hex** — `game3d/interaction/useBoardPicking.ts`
  - What: raycasting y resolución de objetivo con prioridad y debounce.
  - Verify: hover y selección precisos en distintos zooms.

- [x] **Task 22: Drag & drop de carretera con preview** — `useDragPlacement.ts`, `HighlightLayer.tsx`
  - What: drag desde dock/pieza a arista válida con snap visual.
  - Verify: drop en arista legal envía `build_road`; ilegal muestra feedback.

- [x] **Task 23: Drag & drop de settlement con preview** — `useDragPlacement.ts`, `HighlightLayer.tsx`
  - What: drag a nodo legal respetando distancia y conexión.
  - Verify: drop legal dispara `build_settlement` y actualiza escena.

- [x] **Task 24: Upgrade visual a ciudad por selección** — `InteractionController` + `commandBuilder.ts`
  - What: seleccionar settlement propio elegible y confirmar upgrade.
  - Verify: comando `build_city` solo habilitado en nodos válidos.

- [x] **Task 25: Setup guiado (settlement->road) en tablero** — `InteractionController`, `GameTopHud.tsx`
  - What: forzar secuencia setup con highlights del jugador activo.
  - Verify: secuencia inválida bloqueada y explicada en UI.

- [ ] **Task 26: Mano de recursos como cartas visuales** — `hud/ResourceHandCarousel.tsx`, `hud/ResourceCard.tsx`
  - What: reemplazar JSON por cartas con contador y feedback al gastar/ganar.
  - Verify: mano refleja snapshot del jugador en tiempo real.

- [ ] **Task 27: Bandeja de desarrollo gráfica** — `hud/DevelopmentCardsTray.tsx`, `hud/DevCard.tsx`
  - What: render de cartas de desarrollo y acciones permitidas por fase.
  - Verify: jugar caballero/progreso emite comando correcto.

- [x] **Task 28: Dock de acciones (tirar, construir, finalizar turno)** — `hud/ActionDock.tsx`
  - What: botones icónicos con estado habilitado/inhabilitado por fase.
  - Verify: en `PRE_ROLL` solo tirada; en `TRADING/BUILDING` acciones correspondientes.

- [x] **Task 29: Comercio doméstico visual por fichas** — `hud/TradeDock.tsx`
  - What: selector gráfico de “ofrezco/pido” con chips de recursos.
  - Verify: payload `propose_trade` coincide con selección visual.

- [x] **Task 30: Comercio marítimo visual por puerto/ratio** — `MaritimeTradeModal.tsx` (3D style)
  - What: reemplazar botones hardcoded por selector gráfico de ratio y recursos.
  - Verify: `maritime_trade` enviado con ratio y tipos correctos.

- [x] **Task 31: Modal de descarte gráfico con validación** — `DiscardModal.tsx`
  - What: elegir cartas a descartar con contador obligatorio exacto.
  - Verify: botón confirmar solo habilitado cuando suma == requerido.

- [x] **Task 32: Flujo ladrón gráfico (hex + víctima)** — `RobberModal.tsx` o in-scene overlay
  - What: selección de hex objetivo y jugador adyacente en UI visual.
  - Verify: comando `move_robber` correcto; error de adyacencia visible.

- [x] **Task 33: HUD superior de estado de partida** — `hud/GameTopHud.tsx`
  - What: fase, turno activo, puntuaciones visibles y premios en formato gráfico.
  - Verify: cambios de turno/fase se reflejan tras cada ACK.

- [x] **Task 34: Timeline de eventos visual** — `ActionLogPanel.tsx`
  - What: convertir log textual en timeline con iconos y copy breve.
  - Verify: muestra últimos eventos sin ruido técnico.

- [x] **Task 35: Integrar adaptador de comandos central** — `adapters/commandBuilder.ts`
  - What: centralizar payloads para evitar inconsistencias entre UI y WS.
  - Verify: tests unitarios por tipo de comando.

## Integration & Polish

- [ ] **Task 36: Añadir fallback click-select para no-drag** — `interaction/*`
  - What: alternativa accesible: seleccionar pieza y luego destino por click/teclado.
  - Verify: se puede jugar sin arrastrar en desktop/móvil.

- [ ] **Task 37: Accesibilidad y ARIA en HUD/modales** — `hud/*`, `*Modal.tsx`
  - What: foco gestionado, etiquetas ARIA y atajos para acciones críticas.
  - Verify: navegación completa con teclado y lector básico.

- [ ] **Task 38: Animaciones significativas de juego** — `scene/*`, `styles/game3d.css`
  - What: animar colocación de piezas, producción, highlights y transiciones de fase.
  - Verify: animaciones < 180ms en UI, sin afectar legibilidad.

- [ ] **Task 39: Optimización de rendimiento 3D** — `scene/*`
  - What: instancing, memoización, reducción draw calls y fallback de calidad en móvil.
  - Verify: FPS objetivo en profiling de partida media.

- [ ] **Task 40: Manejo de errores contextual en escena** — `lib/ui/toastStore.ts`, `game3d/*`
  - What: mostrar errores cerca del contexto (arista/nodo/comando) además de toast global.
  - Verify: rechazo de comando explica causa y acción correctiva.

- [ ] **Task 41: Test unitario de geometría y mapeo** — `apps/web/src/features/game/game3d/**/*.test.ts`
  - What: validar cálculo de coordenadas, picking IDs y mapping de ownership.
  - Verify: suite Vitest verde para helpers críticos.

- [ ] **Task 42: Test integración frontend con comandos WS** — `apps/web/src/features/game/**/*.test.tsx`
  - What: simular user flow gráfico -> commandBuilder -> ws send -> resync store.
  - Verify: asserts por payload y estado final renderizado.

- [ ] **Task 43: E2E de flujo principal en UI 3D** — `apps/web/e2e/game-3d.spec.ts`
  - What: login, entrar a partida, colocar pieza en setup, tirar dado, finalizar turno.
  - Verify: Playwright pasa en viewport desktop y móvil.

- [x] **Task 44: Actualizar documentación técnica** — `docs/api/auth-lobby.md`, `docs/ui/style-guide.md`, `docs/runbook.md`
  - What: reflejar nuevos campos de snapshot/comandos y operación de UI 3D.
  - Verify: docs alineadas con contratos y sin huecos de implementación.

- [x] **Task 45: Ejecutar validación final de calidad** — `npm run lint`, `npm run test`, `npm run build`, `cd apps/server && ./mvnw -q test`
  - What: correr pipeline completo y registrar resultados en artefacto de implementación.
  - Verify: todo green sin regresiones en login/lobby/juego.

- [x] **Task 46: Checklist de release para rollout gradual** — `docs/release-checklist.md`
  - What: añadir puntos de verificación específicos de 3D (FPS, accesibilidad, reconexión visual).
  - Verify: checklist completable y usada en pre-release.

## Validation before proceeding
- [x] Every task has What + Verify.
- [x] Tasks are in dependency order.
- [x] No task exceeds ~15 min (divididas en unidades atómicas).
