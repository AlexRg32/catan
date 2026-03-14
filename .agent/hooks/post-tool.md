# Post-Tool Execution Hooks

**Goal**: Automate logging and state updates after every significant action.

## 1. Plan Tracker (Implementation Phase)

**Note**: The core loop of `forge.md` or `orchestrate.md` workflows natively handles tracking task progression in `plan.md` and `implementation.md`. 
**Do NOT** trigger automatic post-tool updates to `plan.md` outside of the explicit workflow loop to avoid infinite AI recursion loops.

## 2. Continuous Documentation

**Trigger**: After modifying any file that is NOT code (e.g., config, rules, schema).
**Logic**:

- Invoke `@doc-planner` to update `README.md` or `ARCHITECTURE.md` if the change alters system behavior.
