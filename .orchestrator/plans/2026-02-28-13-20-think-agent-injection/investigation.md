# Investigation: Agent Injection Strategies

## Summary

The goal is to determine the best mechanisms for easily "injecting" or distributing the newly generalized `.agent` folder (the Spring Boot + React factory) into any new project the user creates in the future.

## Current State

- **Tech Stack**: The `.agent` folder is a self-contained directory containing markdown files (agents, contexts, hooks, rules, skills, workflows).
- **Relevant Code**: The `.agent` directory itself.
- **Architecture**: A static set of instructions designed to be read by the AI IDE (like Cursor or Windsurf) or the CLI agent orchestrator.

## Requirements

### Functional

- Must allow the user to easily copy or clone the `.agent` folder into a new repository.
- Should minimize manual folder copying where possible.
- Should allow for centralized updates (if the user improves a skill, all projects could ideally benefit, or at least new projects get the latest).

### Non-Functional

- Usability: Must be fast and frictionless.

## Scope

### In Scope

- Identifying Git-based, script-based, and IDE-based injection methods.

### Out of Scope

- Actually implementing the injection in other repositories right now.

## Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| Outdated Agents | Medium | If `.agent` is simply copy-pasted, older projects won't get updates to the `.agent` rules. |
| Overwriting | Low | Script might overwrite project-specific context accidentally. |

## Recommendation

We need to explore three main options:

1. **GitHub Template Repository** (Best for completely new projects).
2. **Setup Script (Bash/Make)** (Best for injecting into existing initialized projects).
3. **Git Submodules / Git Subtrees** (Best for maintaining a single source of truth across multiple repositories).
