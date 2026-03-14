# Plan: Agent Reusability (Spring Boot + React Stack)

> WARNING: This plan is strictly theoretical. No source code files have been modified.

> Goal: Generalize `.agent` configurations to be project-agnostic but specific to Spring Boot and React.
> Architecture: Scrub domain logic, retain tech stack constraints.

## Foundation

- [ ] **Task 1: Clean Agent Prompts** — `.agent/agents/*.md`
  - What: Edit `backend.md`, `frontend.md`, `architect.md`, and `doc-planner.md`. Remove all mentions of "CreviTapas", voting, restaurants, and stamps. Change specific directory names like `cd crevitapas-api` to a generic `cd backend` or similar.
  - Verify: Agents only contain technical instructions for Spring Boot and React without business rules.

- [ ] **Task 2: Clean the Manual and Workflows** — `.agent/MANUAL.md`, `.agent/workflows/*.md`
  - What: Remove mentions of the CreviTapas project from the `MANUAL.md` and any workflows. Ensure they read as generic instructions for a Spring Boot / React template.
  - Verify: A user reading the manual understands it's a boilerplate for this stack.

## Core

- [ ] **Task 3: Clean Skills** — `.agent/skills/**/*.md`
  - What: Grep for "CreviTapas" or specific domain logic in all skills (especially `stitch-designs`). Remove the business context and replace it with generic placeholders (e.g., "the application's theme").
  - Verify: Skills only teach the AI how to code or design, not what the app is about.

- [ ] **Task 4: Add Context Placeholder** — `.agent/contexts/` or `.agent/skills/`
  - What: Add clear instructions or a dedicated placeholder file (e.g., `.agent/contexts/domain.md` or similar) where the user is instructed to write the *new* project's domain rules.
  - Verify: It's obvious to a new user where to put their business logic so the agents know what to build.
