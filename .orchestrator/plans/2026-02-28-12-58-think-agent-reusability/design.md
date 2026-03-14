# Design: Agent Reusability (Spring Boot + React Stack)

## Architecture Overview

The `.agent` configuration will be transformed into a universal boilerplate for the **Spring Boot + React** stack. We will remove all business domain logic ("CreviTapas", voting, restaurants) while preserving the high architectural standards (layered architecture, DTOs, feature-based React, Suspense, Tailwind).

## Data Model (if applicable)

N/A

## API Contracts (if applicable)

N/A

## Component Design (if frontend)

N/A

## File Structure Update Strategy

To achieve reusability for this specific stack, the files must be cleaned up as follows:

- **`MANUAL.md`**: Remove mentions of "CreviTapas". Make it a universal manual for the Spring/React agent team.
- **`agents/backend.md`**:
  - Remove Domain Knowledge (Restaurants, Tapas, Votes).
  - Remove Key Business Rules (3 stamps, scoring).
  - Modify Directory references (e.g., `crevitapas-api` -> `backend` or generic `API` folder).
- **`agents/frontend.md`**:
  - Remove Domain references (Tapas app, voting UX).
  - Keep the Tailwind, React, TypeScript, and styling guidelines intact.
- **`skills/`**:
  - Check skills like `stitch-designs.md` (mentions "gastronomic theme" and "CreviTapas"). Scrub them to be generic UX guidelines.
  - Create a new skill template `project-context/SKILL.md` (or similar) where the domain rules for the *next* project will live.

## Dependencies

- Standard `.agent` markdown files.

## Testing Strategy

- Review the cleaned files to ensure no "CreviTapas" or "Tapas" references remain.
- Ensure the Spring Boot and React rules are still strictly enforced.
