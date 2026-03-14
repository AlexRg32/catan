# Design: Catan Landing Page

## Architecture Overview
> Standalone React (Vite) Single Page Application que servirá exclusivamente de escaparate comercial y promocional del juego.

## Component Design
### Component Tree
- App
  - HeroSection (Título, CTA principal y background inmersivo)
  - AboutSection (Explicación del juego y lore)
  - GallerySection (Imágenes de los componentes generadas)
  - Footer

### State Management
- Local state: Minimal UI states (mobile menu open/close).
- Server state: None.

## File Structure
apps/landing/
├── index.html
├── src/
│   ├── App.tsx
│   ├── main.tsx
│   ├── index.css
│   ├── components/
│   │   ├── Hero.tsx
│   │   ├── About.tsx
│   │   └── Gallery.tsx
│   └── assets/
│       ├── catan_board_game.jpg
│       ├── catan_landscape.jpg
│       └── catan_pieces.jpg

## Dependencies
- Suggested new packages: `lucide-react` para iconos rápidos. `framer-motion` para animaciones premium.

## Testing Strategy
- Unit: None required for static components.
- Integration: Visual QA verification across devices.
