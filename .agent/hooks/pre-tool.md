# Pre-Tool Execution Hooks

**Guardrails preventing unauthorized or unplanned code modifications.**

## 1. The Immutable Law of Planning (Content Validation)

**BEFORE** using any tool that modifies project source code:

1. **Check for Active Plan Directory**: Look in `.orchestrator/plans/`.
2. **Validate Investigation Phase**:
    * Read `investigation.md`.
    * **FAIL IF**: File is empty or only contains the template header.
    * **PASS IF**: Contains detailed analysis sections (Problem, Goals, Risks).
3. **Validate Design Phase**:
    * Read `design.md`.
    * **FAIL IF**: File is empty or generic.
    * **PASS IF**: Contains specific Schema definitions, Interface contracts, or Root Cause Analysis.
4. **Validate Plan Phase**:
    * Read `plan.md`.
    * **FAIL IF**: No checkboxes (`- [ ]`) or task list found.
    * **PASS IF**: Detailed step-by-step tasks are present.

## 2. Enforcement Behavior

If any validation fails, the Agent **MUST ABORT** the code modification and explicitly state:
> "Compliance Check Failed: Phase [X] is incomplete. Please fulfill the requirements in [file.md] before implementing."

## 3. Protected Paths

- **Never Edit**: `.git/`, `.agent/rules/` (unless explicitly authorized).
