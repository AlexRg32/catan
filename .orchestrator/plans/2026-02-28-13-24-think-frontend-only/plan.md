# Plan: Frontend-Only Adaptation

> WARNING: This plan is strictly theoretical. No source code files have been modified.

> Goal: Modify the `.agent` configuration to dynamically support pure frontend projects.
> Architecture: Context-driven Tech Stack Flag (MULTI-TIER vs FRONTEND-ONLY).

## Foundation

- [ ] **Task 1: Add Tech Stack Flag** — `.agent/contexts/project-domain.md`
  - What: Add a "Technical Stack Mode" section with a default of MULTI-TIER, explaining to the user how to switch to FRONTEND-ONLY.
  - Verify: File contains the new section.

- [ ] **Task 2: Update Tech Stack Rules** — `.agent/rules/global.md`
  - What: Modify section 4 "Technology Stack". Remove the hard requirement for Spring Boot. Add conditional instructions based on the Tech Stack Mode (MULTI-TIER vs FRONTEND-ONLY).
  - Verify: Rule specifically mentions how to behave when FRONTEND-ONLY mode is active.

## Core

- [ ] **Task 3: Neutralize Backend Agent** — `.agent/agents/backend.md`
  - What: Add an instruction explicitly putting `@backend` on standby if the context indicates `FRONTEND-ONLY`.
  - Verify: `@backend` knows not to write Java if there is no backend.

- [ ] **Task 4: Adapt Architect Agent** — `.agent/agents/architect.md`
  - What: Update the architect's instructions to focus strictly on frontend architecture (React routing, components, external API consumption) when in `FRONTEND-ONLY` mode, avoiding Database and REST API design.
  - Verify: Architect instructions handle the conditional logic for `FRONTEND-ONLY`.

## Integration & Polish

- [ ] **Task 5: Update the Manual** — `.agent/MANUAL.md`
  - What: Add a small note to the manual explaining that the agents support both Full-Stack and Pure Frontend landing pages via the `project-domain.md` flag.
  - Verify: Manual reflects the new capability.
