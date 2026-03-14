# Investigation: Catan Landing Page

## Summary
> Analizar y proponer cómo crear exclusivamente una Landing Page de Catan que sea independiente del desarrollo del agente que construye el online-game stack principal.

## Current State
- **Tech Stack**: Spring Boot (Backend), React/Vite (Frontend en `apps/web`), Tailwind CSS (Styling).
- **Relevant Code**: Ninguno del stack actual. La idea es crearlo fuera de la app `web` (ej. una app Vite separada en `apps/landing`) para evitar cualquier colisión.
- **Architecture**: Monorepo.

## Requirements
### Functional
- [x] Crear un landing page standalone sobre Catan.
- [x] Explicar de qué va el juego de mesa Catan.
- [x] Usar imágenes/fotos atractivas (generadas previamente por IA).

### Non-Functional
- Performance: Carga rápida.
- Zero-Interference: Debe crearse como un workspace autónomo (`apps/landing`) que no modifique nada de `apps/web` ni `apps/server`.

## Scope
### In Scope
- Setup inicial de Vite + React + Tailwind en `apps/landing`.
- Maquetar landing page usando los mockups generados.
- Textos de copywriting explicando la mecánica e historia del juego.

### Out of Scope
- Modificar el router de `apps/web`.
- Lógica de juego, online rooms, o backend dev.

## Risks
| Risk | Impact | Mitigation |
|------|--------|------------|
| Colisión con dependencias de `apps/web` | High | Inicializar como workspace Vite independiente |

## Recommendation
> Recomiendo construir la landing page en una nueva carpeta `apps/landing` inicializada con Vite y TailwindCSS. Esto aísla el 100% del trabajo del otro agente y permite un focus exclusivo en diseño.
