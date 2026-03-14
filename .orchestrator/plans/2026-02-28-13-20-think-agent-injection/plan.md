# Plan: Agent Injection Strategies

> WARNING: This plan is strictly theoretical. No source code files have been modified.

> Goal: Document and provide tools for easily injecting the `.agent` folder into new and existing projects.
> Architecture: Using GitHub Templates (new projects) and Bash scripts (existing projects).

## Foundation

- [ ] **Task 1: Document Injection Methods** — `.agent/MANUAL.md`
  - What: Add a new section to the manual explaining how to replicate the AI Agent setup into other repositories. Detail both the "GitHub Template" approach and the "Copy/Bash Script" approach.
  - Verify: A user reading the manual understands how to propagate the agents.

## Core

- [ ] **Task 2: Create Injection Script** — `inject-agent.sh` (or provide the code in `MANUAL.md`)
  - What: Provide a ready-to-use bash script or alias instruction inside `MANUAL.md` that a user can copy to their `~/.zshrc` or `~/.bashrc`. This script will safely copy the `.agent` folder from a centralized location (this repository) to whatever directory they are currently in.
  - Verify: The bash code is syntactically correct and includes a safety check to ensure it doesn't overwrite an existing customized `project-domain.md`.

## Integration & Polish

- [ ] **Task 3: GitHub Template Setup instructions** — `.agent/MANUAL.md`
  - What: Provide instructions on how to set up the current repository as a GitHub Template for one-click cloning.
  - Verify: The instructions are accurate for GitHub's UI.
