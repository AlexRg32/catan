# Catan Online

Monorepo base for an online 4-player Catan implementation.

## Stack

- Frontend: React + TypeScript + Vite (`apps/web`)
- Backend: Spring Boot 3 + Java 21 (`apps/server`)
- Infra local: PostgreSQL + Redis (`infra/docker`)

## Quick Start

1. Copy env files:
   - `cp .env.example .env`
   - `cp apps/web/.env.example apps/web/.env`
   - `cp apps/server/.env.example apps/server/.env`
2. Install frontend dependencies:
   - `npm install`
3. Start local infrastructure:
   - `docker compose -f infra/docker/docker-compose.yml up -d`
4. Run backend:
   - `npm run dev:server`
5. Run frontend:
   - `npm run dev:web`

## Local Dev Notes

- `npm run dev:server` now skips Checkstyle/Spotless checks so startup logs are cleaner in local development.
- Backend CORS allows `http://localhost:5173` by default (override with `APP_WEB_ORIGINS`, comma-separated).
- Tracing export is disabled by default in local (`MANAGEMENT_TRACING_ENABLED=false`) to avoid OTLP `localhost:4318` connection errors.

## One-Command Docker Run

If you prefer running everything in Docker (web + server + postgres + redis):

- `docker compose -f infra/docker/docker-compose.full.yml up -d --build`

Then open:

- `http://localhost:5173` (web)
- `http://localhost:8080/actuator/health` (backend health)

## Scripts

- `npm run dev:web` - Start web app.
- `npm run dev:server` - Start Spring Boot app.
- `npm run build` - Build web and server.
- `npm run lint` - Lint web and server.
- `npm run test` - Test web and server.
