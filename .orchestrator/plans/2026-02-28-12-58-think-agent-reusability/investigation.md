# Investigation: Agent Reusability (Spring Boot + React Stack)

## Summary

The objective is to analyze the `.agent` configuration folder and make it a reusable boilerplate strictly for projects using the **Spring Boot (Backend) and React (Frontend)** stack. It must be completely agnostic of the current business domain ("CreviTapas").

## Current State

- **Tech Stack Detected**: Java, Spring Boot 3+, Maven, PostgreSQL, React, TypeScript, Tailwind CSS.
- **Relevant Code**: Everything inside `.agent/` (`agents/`, `skills/`, `workflows/`, `MANUAL.md`).
- **Domain Lock-in**: The files heavily reference "CreviTapas", restaurants, tapas, user votes, stamps, and specific UI requirements for that business.

## Requirements

### Functional

- Extract all domain-specific logic (CreviTapas) from the `.agent` configuration.
- Retain all strict rules, guidelines, and commands for Spring Boot and React development.
- Provide a clear place for the user to define the domain of their new projects.

### Non-Functional

- Reusability: Must be usable as a plug-and-play `.agent` folder for any new Spring Boot + React project.

## Scope

### In Scope

- Analysis of all files inside the `.agent/` folder to identify domain-specific terminology.

### Out of Scope

- Making it agnostic to other languages (e.g., Python, Node.js).
- Actually modifying the files (this is the read-only `think` workflow).

## Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| Domain lock-in | High | The AI will assume the business rules of "CreviTapas" exist in new projects. Must scrub all agents and skills. |
| Hardcoded paths | Medium | Existing workflows might use specific folder names (e.g., `crevitapas-api`). Need to use generic names like `backend` or `frontend`. |

## Recommendation

The `.agent` folder should be refactored to remove all mentions of "CreviTapas" and its business logic. The `agents/backend.md` and `agents/frontend.md` should focus entirely on being expert Spring Boot and React engineers, respectively, without knowing anything about Tapas. A new placeholder for "Project Context" should be introduced so users know where to add their new app's business rules.
