# Investigation: Frontend-Only Adaptation

## Summary

The user wants to use the current `.agent` setup to build pure frontend websites and landing pages (e.g., using React/Tailwind) without a backend. Currently, the `.agent` folder is hardcoded to force a Spring Boot + React monolithic architecture, which creates friction for purely frontend projects.

## Current State

- **Tech Stack**: Spring Boot (Backend) + React/Tailwind (Frontend).
- **Relevant Code**:
  - `.agent/rules/global.md`: Currently states that "Spring Boot (Java 17) — monolithic architecture" and "Layered Architecture is MANDATORY".
  - `.agent/agents/backend.md`: An agent dedicated to Spring Boot.
  - `.agent/agents/architect.md`: Assumes backend architecture, databases, and REST APIs.
- **Architecture**: Enforced Full-Stack.

## Requirements

### Functional

- Allow the AI to operate dynamically on a "Frontend-only" mode where it doesn't attempt to build or expect a Spring Boot backend.
- Maintain the current high standards for React and Tailwind CSS.

### Non-Functional

- Flexibility: The `.agent` folder should be reusable across both full-stack apps and pure landing pages without having to maintain two separate repositories of agents.

## Scope

### In Scope

- Analyzing how to make `rules/global.md` and agents adaptable to "Frontend-Only" constraints.
- Providing project context flags to inform the agents of the stack.

### Out of Scope

- Actually changing the files in this analysis (Read-Only workflow phase).

## Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| Agent Confusion | High | Agents might hallucinate a backend if it is not explicitly told that the project is frontend-only. |
| Over-engineering | Medium | `architect` might try to design databases for a simple landing page. |

## Recommendation

We must adapt the `.agent` folder to be **Stack-Aware** rather than rigidly Full-Stack. Instead of forcing Spring Boot every time, `global.md` and the agents should conditionally apply rules based on a configuration file (like a `project-stack.md` or as part of `project-domain.md`). If the project is declared as "Frontend-Only", the `@backend` agent becomes inactive, and `@architect` focuses solely on frontend architecture and routing.
