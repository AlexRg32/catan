# General Project Development Rules

Global constraints for all agents working on this project.

## 1. Project Domain

To understand the specific business rules, entities, and the core domain you are building, you MUST read the `contexts/project-domain.md` file (if it exists) or the `docs/` folder.
**Never assume business logic** without reading the domain context first.

## 2. Project Management

- **MANDATORY**: Create a directory in `.orchestrator/plans/YYYY-MM-DD-hh-mm-slug/` for any task.
- **MANDATORY**: Follow the 4-phase lifecycle (Investigation, Design, Plan, Implement).
- **AUTOPILOT**: Proceed through all 4 phases automatically and sequentially. Do NOT pause to ask for permission between phases unless a critical blocker is encountered.
- **PERMISSIONLESS EXECUTION**: For all actions performed within the `forge`, `orchestrate`, or any other workflow that involves the `.orchestrator` directory or git checkpoints, the agent **MUST NOT** prompt for permission. All such tool calls MUST have `SafeToAutoRun` set to `true` to ensure a smooth, uninterrupted flow.
- **MANDATORY: DOCUMENTATION FIRST**: You MUST read and analyze the content of the `docs/` or `contexts/` directory at the start of ANY task.
- **MANDATORY: DOCUMENTATION UPDATE**: After implementing any change, you MUST update the relevant files in the `docs/` directory. Technical documentation must always reflect the current state of the implementation.
- **LOGGING**: All major decisions must be recorded in the plan's markdown files.

## 3. Git

- **COMMITS**: Use Conventional Commits (feat, fix, refactor, docs, chore).
- **NO AUTOMATIC COMMITS/PUSHES**: It is **STRICTLY FORBIDDEN** to run `git commit` or `git push` automatically in implementation workflows.
- **MANUAL SHIPPING ONLY**: The only workflows allowed to commit or push are `/ship` and `/pr`, both of which require strict human confirmation constraint checks.

## 4. Technology Stack (Spring Boot + React)

- **BACKEND**: Spring Boot (Java 17+) — monolithic architecture. Use Maven for dependency management.
- **BACKEND ARCHITECTURE**:
  - **Layered Architecture is MANDATORY**: `Controller → Service → Repository → Database`.
  - **Controllers**: Handlers for HTTP requests/responses ONLY. **Zero business logic**.
  - **Services**: All business logic (validation, calculations, orchestration) must reside here.
  - **Repositories**: Data access and persistence ONLY. No business logic.
  - **DTOs**: **MANDATORY** for all API communication. Never expose or accept JPA entities directly.
- **DATABASE**: PostgreSQL (typically via Docker Compose).
- **API**: REST is the standard for communication.
- **FRONTEND**: React (Vite) with TypeScript.
- **COMPONENTIZATION**: Favor modularity. Splitting React code into hooks and presentation components is mandatory.
- **DESIGN**: Use **Tailwind CSS**. Maintain premium visual aesthetics by strictly following the project's design system tokens (colors, spacing).

## 5. Security & Quality

- No hardcoded secrets. Use env variables.
- Write tests (TDD) before implementation when possible.
- Defense in depth for all API endpoints.

## 6. User Experience (UX)

- **DESTRUCTIVE ACTIONS**: Always show a confirmation modal before deleting any resource to prevent accidental data loss. This is mandatory for all frontend implementations.
- **SEARCHABLE SELECTS**: For any input field that involves selecting from a potentially large list, **MANDATORY** to use a searchable select component.
- **MOBILE FIRST**: Always design the UI to be responsive and mobile-optimized first, unless instructed otherwise for desktop-only admin panels.
