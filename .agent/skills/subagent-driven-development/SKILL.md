---
name: subagent-driven-development
description: Use when executing implementation plans. Dispatches fresh subagents per task with a two-stage review process (spec compliance and code quality).
---

# Subagent-Driven Development (SDD)

## SDD Workflow

1. **Dispatch**: Spawn fresh subagent per task from `plan.md`.
2. **Review 1 (Spec)**: `@architect` validates requirements.
3. **Review 2 (Quality)**: `@backend`/`@doc-planner` validates code/docs.
4. **Documentation**: Update relevant files in `docs/` to reflect implemented changes.
5. **Cleanup**: Mark task complete in `plan.md` + `implementation.md`.

## Laws

- **Strict Isolation**: One subagent = one task. No context carryover.
- **Copy-Paste**: Provide the subagent with the **task text**, not a file path to the plan.
- **Review Loop**: Fixes MUST be re-reviewed by the same agent persona.
- **Error Check**: Reviews MUST verify that user interactions have clear, personalized error messages.
