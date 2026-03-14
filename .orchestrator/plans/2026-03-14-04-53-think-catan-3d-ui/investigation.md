# Investigation: UI 3D completa para Catan (tablero, cartas y figuras)

## Summary
Se solicita un rediseño total de la experiencia de juego: pasar de la UI actual (mayoritariamente textual/2D) a una UI gráfica 3D centrada en el tablero hexagonal, con interacción drag & drop para piezas, cartas visuales de recursos/desarrollo, y flujos de juego amigables sin interacción textual cruda.

## Current State
- **Tech Stack**: Monorepo con `apps/web` (React 19 + TypeScript + Vite), `apps/server` (Spring Boot Java 17), WebSocket STOMP + REST snapshot/deltas.
- **Relevant Code**:
  - Frontend tablero y paneles: `apps/web/src/features/game/*`, `apps/web/src/features/game/board/*`
  - Estado/sync: `apps/web/src/lib/state/gameStore.ts`, `apps/web/src/lib/ws/*`, `apps/web/src/lib/api/client.ts`
  - Contratos compartidos FE: `apps/web/src/lib/types/index.ts`
  - Proyección backend: `apps/server/src/main/java/com/catan/server/game/projections/*`
  - Dispatcher comandos: `apps/server/src/main/java/com/catan/server/game/service/GameCommandDispatcher.java`
  - Modelo runtime: `apps/server/src/main/java/com/catan/server/game/engine/model/*`
- **Architecture**:
  - Backend autoritativo (valida comandos, persiste eventos y snapshots).
  - Frontend reactivo (resync por snapshot + eventos), pero UI del juego aún no representa geometría real de tablero ni affordances gráficas completas.

## Análisis 5 preguntas
1. **¿Qué se pide?**
- UI 3D integral para jugar: tablero hexagonal 3D, piezas drag & drop, cartas de materiales visibles y controles gráficos para todas las acciones de partida.

2. **¿Por qué se necesita?**
- Mejorar drásticamente UX y legibilidad del estado de partida; eliminar fricción de inputs textuales y acercar la experiencia a un juego de mesa digital premium.

3. **¿Dónde encaja?**
- Principalmente en `apps/web/src/features/game/*` y contratos FE/BE de snapshot/comandos.
- También requiere ampliar proyección backend para exponer geometría/estado de tablero consumible por render 3D.

4. **¿Qué existe reutilizable?**
- Paleta, tipografías y guía visual propia (`docs/ui/style-guide.md`).
- Assets iniciales (terrenos, puertos, piezas, cartas) y loader (`assetLoader.ts`).
- Pipeline de sincronización robusto (REST + WS + store).
- Validadores de reglas en backend (fases, descarte, ladrón, caminos, etc.).

5. **¿Riesgos y edge cases?**
- La proyección actual no incluye geometría de hex/intersecciones/aristas ni ownership detallado en snapshot.
- El inicializador runtime crea `intersections` y `edges` vacíos, lo que bloquea setup/placement real.
- Faltan comandos de construcción normal en dispatcher (`build_road`, `build_settlement`, `build_city`, compra de desarrollo) para una interacción gráfica completa.
- Rendimiento 3D en móvil y accesibilidad keyboard/screen reader requieren diseño explícito.

## Requirements
### Functional
- [ ] Tablero hexagonal 3D fiel a la topología de juego (19 hex + puertos + ladrón + fichas numéricas).
- [ ] Drag & drop visual para colocar carretera, poblado y ciudad sobre aristas/intersecciones válidas.
- [ ] Mano de recursos y cartas de desarrollo en formato carta gráfica, no JSON/texto crudo.
- [ ] Flujos gráficos para comercio, descarte, ladrón, desarrollo y fin de turno.
- [ ] HUD visual de fase, turno, puntos y premios (carretera más larga / ejército más grande).
- [ ] Sustituir interacciones textuales actuales por controles gráficos amigables (desktop + mobile).

### Non-Functional
- Performance: objetivo 60 FPS en desktop y 30+ FPS estable en móvil medio; degradación visual controlada.
- Seguridad: mantener backend autoritativo; UI solo sugiere acciones válidas, nunca decide reglas finales.
- Accesibilidad: WCAG AA en contraste; alternativa por teclado para acciones críticas de drag/drop.
- Observabilidad: trazabilidad de comandos y errores UI/WS sin romper pipeline actual.
- IP/arte: respetar política V1 de arte propio (sin logos/ilustraciones oficiales).

## Scope
### In Scope
- Rediseño integral de pantalla de partida (`/game/:id`) hacia interacción 3D.
- Ampliación de contratos API/proyección para estado de tablero renderizable.
- Integración de acciones de juego actuales y faltantes necesarias para experiencia gráfica completa.
- Sistema visual de cartas, piezas, highlights de legalidad, overlays y feedback animado.

### Out of Scope
- Expansiones (5-6 jugadores, escenarios especiales).
- Cambios de reglas base fuera de `docs/rules/base-game-v1.md`.
- Uso de arte/licencias oficiales de CATAN/Devir (expresamente excluido en V1).

## Risks
| Risk | Impact | Mitigation |
|------|--------|------------|
| Proyección sin geometría de tablero | Alto | Añadir `board` estructurado al snapshot con hexes/intersections/edges/ports + legal moves |
| Runtime con aristas/intersecciones vacías | Alto | Inicializar grafo canónico del tablero en backend antes de UI drag/drop |
| Cobertura incompleta de comandos de build | Alto | Extender dispatcher + handlers para construcción normal y compra de desarrollo |
| Jank en móvil por render 3D | Medio | Instancing, LOD, texturas comprimidas, limitación de sombras/postprocesado |
| UX no accesible por depender solo de drag | Medio | Modo alternativo click-select + teclado para todas las acciones críticas |
| Riesgo IP por usar referencias fotográficas oficiales | Medio | Usar fotos solo como referencia de composición, producir arte/3D propio en-house |

## Recommendation
Implementar en 3 bloques dependientes: (1) contrato/estado backend de tablero + comandos faltantes, (2) motor UI 3D con interacción y HUD gráfico, (3) hardening UX/QA/performance. Sin resolver primero la base de datos de geometría y acciones válidas en snapshot, el drag & drop 3D no puede ser fiable ni consistente con reglas servidor.
