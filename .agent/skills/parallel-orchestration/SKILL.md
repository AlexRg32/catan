---
name: parallel-orchestration
description: Multi-agent coordination pattern. Use when a task requires multiple domain experts (Security + Architecture + Implementation) or when analyzing a complex system.
---

# Parallel Orchestration

## Overview

This skill coordinates multiple specialized agents natively through Claude Code's agent system.

## Domain Teams

| Team | Agents involved | Trigger |
| :--- | :--- | :--- |
| **New Feature** | `@architect` → `@doc-planner` → `@backend` | New business requirement |
| **Security Audit** | `@architect` → `security-scanner` | New API or sensitive change |
| **Doc Polish** | `@doc-planner` → `@architect` | Refactoring or public release |

## Sequential Chain Pattern

1. **Explorer**: Map the current state.
2. **Domain Experts**: Each analyzes from their perspective (Architecture, Security, Performance).
3. **Synthesis**: Combine all outputs into a single Decision Record (ADR) or Implementation Plan.

## Rules

- **Context Passing**: Each agent must share its findings with the next one.
- **Unified Output**: The final response must be a synthesis of all agent findings.
- **Handoffs**: Clearly define what each agent is responsible for.
