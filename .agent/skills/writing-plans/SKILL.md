---
name: writing-plans
description: Use when you have a spec or requirements for a task, BEFORE touching code. This is the tactical blueprint for all implementations.
---

# Writing Implementation Plans

## Rules

1. **Granularity**: Tasks MUST be 5-15 mins.
2. **Standard Loop**:
   - Write failing test (RED).
   - Minimal code (GREEN).
   - Verify (PASS).
   - Mark task as done.

## Plan Template

```markdown
# [Feature]
> Goal: [value]
> Architecture: [pattern]

### Task N: [File]
1. Failing test in `XyzTests.java`.
2. Implement in `Xyz.java`.
3. `mvn test -Dtest=XyzTests`
4. Update `docs/` if necessary.
5. Mark task as `[x]` in `plan.md`.
```
