# Plan: Catan Landing Page

> WARNING: This plan is strictly theoretical. No source code files have been modified.
> Goal: Crear la Landing sin interferir con el otro agente.
> Architecture: Vite Standalone SPA independiente.

## Foundation

- [x] **Task 1: Setup Workspace y Vite** — `apps/landing`
  - What: Crear nueva carpeta con `npm create vite@latest apps/landing -- --template react-ts`, e inicializar TailwindCSS. Actualizar `package.json` global para añadirlo a los "workspaces".
  - Verify: Comprobar que arranca un dev server vía `npm run dev --workspace=apps/landing`.

## Core

- [x] **Task 2: Assets & Copiar Imágenes IA** — `apps/landing/src/assets`
  - What: Tomar los 3 archivos generados por IA (catan_board_game, catan_landscape, catan_pieces) y copiarlos al directorio assets/ de la landing.
  - Verify: Verificar que Vite las puede servir correctamente y resuelven las rutas.

## Integration & Polish

- [x] **Task 3: Construir Secciones y UI Premium** — `apps/landing/src/App.tsx`
  - What: 
    1. Base: Configurar la app para usar un theme premium moderno (cristalmorphism, colores cálidos, fondo inmersivo).
    2. Hero: Usar imagen envolvente `catan_landscape`. Título atractivo: "Conquista la Isla de Catan".
    3. About: Texto describiendo recursos, el ladrón y las tiradas de dados. 
    4. Gallery/Features: Usar `catan_board_game` y `catan_pieces` para visualizar el juego real.
  - Verify: Revisar visualmente a pantalla completa el resultado del Landing y asegurarse de que cumple sobradamente en diseño.
