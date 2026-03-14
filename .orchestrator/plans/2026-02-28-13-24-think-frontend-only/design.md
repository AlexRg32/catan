# Design: Frontend-Only Adaptation

## Architecture Overview

To support pure frontend projects without throwing errors or hallucinating backends, the `.agent` configuration needs an explicit "Tech Stack Flag" mechanism. Based on the presence of this flag in `contexts/project-domain.md`, the agents will adapt their behavior.

## Strategy: The "Stack Context" Approach

We will modify `.agent/contexts/project-domain.md` to include a clear, explicit section about the `Tech Stack`.

### 1. Update `contexts/project-domain.md`

Add a section like this:

```markdown
## Technical Stack Mode
[Specify the mode of the application. Choose between: MULTI-TIER (Spring Boot + React) or FRONTEND-ONLY (React + Tailwind)]
- MODE: FRONTEND-ONLY
```

### 2. Update `rules/global.md`

Remove the hardcoded enforcement that "Spring Boot is MANDATORY". Instead, make it conditional:

- "If the Stack Mode is MULTI-TIER, then Spring Boot Architecture rules apply."
- "If the Stack Mode is FRONTEND-ONLY, the frontend is the only tier. Do not attempt to create or design backend services. Mock APIs or use BAAS (like Supabase/Firebase) if databases are needed."

### 3. Update `@backend` (backend.md)

Add a kill-switch instruction:
"If the project context specifies `FRONTEND-ONLY`, acknowledge that you are on standby and do not attempt to write or design any backend code unless explicitly forced."

### 4. Update `@architect` (architect.md)

Add conditional logic:
"If `FRONTEND-ONLY`, focus architecture on React routing, state management (Redux/Zustand), service workers, components, and API client layers (fetching from third parties rather than building the local API)."

## File Structure Changes

No files will be deleted. The following files will be edited to support conditionals:

- `.agent/rules/global.md`
- `.agent/contexts/project-domain.md`
- `.agent/agents/architect.md`
- `.agent/agents/backend.md`

## Why this is the best option?

- **Unified Repository**: You don't need a entirely separate repository/branch just for frontend projects. The same `.agent` factory works for everything.
- **Explicit Constraints**: The LLM clearly knows what mode it is in. No guessing games.
