---
description: Read-only Forge. Plan solutions, analyze architecture, and answer questions using the codebase, documentation, and skills, without editing or implementing any code files.
---
// turbo-all

# Think Workflow

**Goal**: Provide a deep analysis of the codebase, answer architectural questions, and plan solutions based strictly on existing documentation and code. This workflow is **READ-ONLY** regarding the project's codebase. It will NOT edit, create, or implement any source code files. It has the exact same analytical power and structure as `/forge`, but stops precisely before implementation.

---

## Phase 0: Setup Analysis Workspace

Analyze Intent (`question`, `design`, `refactor_plan`) + Slug.

1. **Initialize Workspace Directory**:

   ```bash
   export PLAN_PATH=".orchestrator/plans/$(date +'%Y-%m-%d-%H-%M')-think-<SLUG>" && \
   mkdir -p "$PLAN_PATH" && \
   echo "Analysis Workspace Ready: $PLAN_PATH"
   ```

2. **Create Status Tracker** — Create `$PLAN_PATH/status.md`:

   ```markdown
   # Status: <SLUG>
   | Phase          | Status  |
   |----------------|---------|
   | Investigation  | pending |
   | Design         | pending |
   | Plan           | pending |
   | Implementation | pending |

   Created: <ISO_TIMESTAMP>
   ```

---

## Phase 1: Investigation (Read-Only)

> Uses: `software-architecture` skill

### 1A — Codebase Scan (MANDATORY)

Before analyzing the request, **understand what already exists**:

1. **CRITICAL: Analyze Documentation**: Read ALL files in the `docs/` folder FIRST, starting with `00-general-docs.md`. This is the source of truth for the project context. Do not skip.
2. List top-level files/dirs → detect tech stack (`package.json`, `pom.xml`, `Dockerfile`, etc.).
3. Map folder structure → identify architecture pattern (monorepo, feature-based, layered).
4. Read key configs → `tsconfig.json`, `application.yml`, `.env.example`, etc.
5. Check existing conventions → linters, formatters, test patterns, naming.

### 1B — Deep Analysis

Answer these five questions:

1. **What** is being requested? (clear problem statement or question)
2. **Why** is it needed? (business value / user impact)
3. **Where** does it fit? (which modules/layers are affected)
4. **What exists** that can be reused or extended?
5. **What are the risks** and edge cases?

### 1C — Generate `$PLAN_PATH/investigation.md`

```markdown
# Investigation: [Name]

## Summary
> One-paragraph description of the analysis goal.

## Current State
- **Tech Stack**: [detected]
- **Relevant Code**: [files/modules that will be affected/analyzed]
- **Architecture**: [current pattern]

## Requirements
### Functional
- [ ] Requirement 1
- [ ] Requirement 2

### Non-Functional
- Performance: [constraints if any]
- Security: [considerations if any]

## Scope
### In Scope
- ...

### Out of Scope
- ...

## Risks
| Risk | Impact | Mitigation |
|------|--------|------------|
| ...  | ...    | ...        |

## Recommendation
> Recommended approach and reasoning based on codebase and skills.
```

### 1D — Checkpoint

Update `$PLAN_PATH/status.md` → Investigation: `done`.

---

## Phase 2: Design (Read-Only)

> Uses: `software-architecture`, `api-patterns`, `database-design`, `frontend-dev-guidelines`, `backend-dev-guidelines`, `stitch-designs`, `landing-page-anatomy`, `premium-animations`, `advanced-tailwind-styling` skills

Based on Phase 1, design the conceptual solution or formulate the architectural response without touching the source code.

### 2A — Read `$PLAN_PATH/investigation.md`

### 2B — Generate `$PLAN_PATH/design.md`

```markdown
# Design: [Name]

## Architecture Overview
> How this feature or solution fits into the existing architecture.

## Data Model (if applicable)
| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| ...    | ...       | ...           |

## API Contracts (if applicable)
| Method | Path | Body | Response | Auth |
|--------|------|------|----------|------|
| ...    | ...  | ...  | ...      | ...  |

## Component Design (if frontend)
### Component Tree
[ASCII tree of components with logic/presentation split]

### State Management
- Local state: [what/where]
- Server state: [cache keys]

## File Structure
[Tree showing suggested new/modified files and where they should go]

## Dependencies
- Suggested new packages: [with justification]
- Existing packages to use: [list]

## Testing Strategy
- Unit: [what]
- Integration: [what]
```

**MODULARITY CHECK**: If React, verify the design splits logic (hooks) and presentation.

### 2C — Checkpoint

Update `$PLAN_PATH/status.md` → Design: `done`.

---

## Phase 3: Actionable Plan (Read-Only)

> Uses: `writing-plans` skill

Provide the final and actionable steps. These will NOT be implemented by this workflow, just prepared as a blueprint.

### 3A — Read `$PLAN_PATH/design.md` (and reference `investigation.md`)

### 3B — Generate `$PLAN_PATH/plan.md`

Each task MUST be:

- **Atomic**: One concern per task (single file or tightly coupled pair).
- **Time-boxed**: 5-15 minutes (estimation).
- **Ordered**: Dependency order — foundations first, features second, integrations last.
- **Verifiable**: Every task has a way to confirm it worked.

```markdown
# Plan: [Name]

> WARNING: This plan is strictly theoretical. No source code files have been modified.

> Goal: [one-line from investigation]
> Architecture: [from design]

## Foundation

- [ ] **Task 1: [Name]** — `[target file(s)]`
  - What: [specific action]
  - Verify: [how to confirm]

## Core

- [ ] **Task 2: [Name]** — `[target file(s)]`
  - What: [specific action]
  - Verify: [how to confirm]

## Integration & Polish

- [ ] **Task N: [Name]** — `[target file(s)]`
  - What: [specific action]
  - Verify: [how to confirm]
```

**Validation before proceeding**:

- [ ] Every task has What + Verify.
- [ ] Tasks are in dependency order.
- [ ] No task exceeds ~15 min.

### 3C — Checkpoint

Update `$PLAN_PATH/status.md` → Plan: `done`.

---

## Phase 4: Final Presentation

After all analytical phases complete, present the results to the user:

1. **Provide a markdown synthesis**: Summarize the findings, the design decision, and the suggested plan.
2. **Explicitly clarify**: The codebase **has NOT been modified** and no git branch was created.
3. **Next step**: Suggest the user run `/forge execute` to automatically implement the generated plan.

---

## Recovery & Resume

If orchestration is interrupted:

1. Read `$PLAN_PATH/status.md`.
2. Find the first phase with status `pending`.
3. Resume from that phase — **do NOT re-run completed phases**.

---

## Global Rules

- **STRICTLY READ-ONLY**: You are **FORBIDDEN** from editing, deleting, or writing into any source code file, configuration, or project documentation. You may ONLY create tracking files inside `.orchestrator/plans/`.
- **NO AUTOMATIC COMMITS/PUSHES**: Do not touch or alter the Git repository in any way. No branch creation (Phase 0 only creates a local tracking directory).
- **DOCS & SKILLS FIRST**: Phase 1 MUST scan the existing codebase and `docs/` folder before making any decisions. Phase 2 must strictly align with our `.agent/skills/`.
- **AUTOPILOT**: Proceed through ALL phases (0 through 3) sequentially and automatically. Do NOT stop to ask for permission for anything (including analyzing code or creating/writing files in the `.orchestrator` folder).
- **NO PERMISSIONS**: Never ask "Is it okay if I create this file?" or "Should I start the analysis?". Just do it.
- **PATH Injection**: Never invoke a sub-workflow without `PATH="$PLAN_PATH"`.
- **SAFE TO AUTO-RUN**: All commands in this workflow (mkdir, checkpoints creation, etc.) are explicitly safe. Use `SafeToAutoRun: true` for all tool calls to avoid interrupting the user.
- **Codebase-First**: Evaluate the actual architecture of what already exists by reading the files; do not invent contexts.
