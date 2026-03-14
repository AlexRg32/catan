// turbo-all
---
description: Proactively scans the project to find technical debt, vulnerabilities, and architectural improvements.
---

# Audit Workflow

**Goal**: Act as a proactive auditor scanning the codebase for technical debt, security vulnerabilities, and departures from established guidelines. This workflow provides a prioritized health report and actionable refactoring suggestions. It is **READ-ONLY** and will not modify source code.

---

## Phase 0: Setup Audit Workspace

1. **Initialize Audit Directory**:

   ```bash
   export AUDIT_PATH=".orchestrator/audits/$(date +'%Y-%m-%d-%H-%M')-audit" && \
   mkdir -p "$AUDIT_PATH" && \
   echo "Audit Workspace Ready: $AUDIT_PATH"
   ```

2. **Create Status Tracker** — Create `$AUDIT_PATH/status.md`:

   ```markdown
   # Status: Audit
   | Phase                | Status  |
   |----------------------|---------|
   | 1. Guidelines Check  | pending |
   | 2. Security Check    | pending |
   | 3. Architecture Scan | pending |
   | 4. Final Report      | pending |

   Created: <ISO_TIMESTAMP>
   ```

---

## Phase 1: Guidelines Compliance Check (Read-Only)

> Uses: `frontend-dev-guidelines`, `backend-dev-guidelines`, `clean-code` skills

### 1A — Tech Stack Detection
Identify the core technologies of the project (e.g., React, Spring Boot, etc.) by reading `package.json`, `pom.xml`, etc. 

### 1B — Targeted Sampling
Based on the detected tech stack, sample key files (e.g., a few React components, a Spring Boot controller, a service, a repository).

### 1C — Verify Compliance
Check the sampled files against the loaded guidelines:
- Are frontend components using the specified architecture and patterns?
- Are backend services following layered architecture and exception handling rules?
- Does the code adhere to the clean-code principles (concise, pragmatic)?

### 1D — Document Findings
Create `$AUDIT_PATH/1-guidelines-findings.md` listing the departures from the guidelines.

### 1E — Checkpoint
Update `$AUDIT_PATH/status.md` → 1. Guidelines Check: `done`.

---

## Phase 2: Security & Vulnerability Check (Read-Only)

> Uses: `api-security-best-practices` skill

### 2A — Security Scan
Analyze the application's configuration files (e.g., `.env.example`, `application.yml`, security configs) and key integration points (e.g., authentication filters, auth controllers).

### 2B — Identify Vulnerabilities
Look for common issues:
- Hardcoded secrets or poor secret management.
- Missing rate limiting or excessive payload sizes.
- Improper authorization checks.

### 2C — Document Findings
Create `$AUDIT_PATH/2-security-findings.md` detailing the discovered vulnerabilities and security gaps.

### 2D — Checkpoint
Update `$AUDIT_PATH/status.md` → 2. Security Check: `done`.

---

## Phase 3: Architecture & Performance Scan (Read-Only)

> Uses: `software-architecture`, `api-patterns`, `database-design` skills

### 3A — Structural Analysis
Review the interaction between layers, API contracts, and database models.
- Are there cyclic dependencies or tightly coupled modules?
- Are endpoints well-designed (REST/GraphQL/tRPC)?
- Are database schemas optimized?

### 3B — Document Findings
Create `$AUDIT_PATH/3-architecture-findings.md` outlining architectural bottlenecks, bad abstractions, or performance risks.

### 3C — Checkpoint
Update `$AUDIT_PATH/status.md` → 3. Architecture Scan: `done`.

---

## Phase 4: Final Deliverable - The Audit Report

### 4A — Synthesize Findings
Read the reports from Phases 1, 2, and 3.

### 4B — Generate Final Report
Create `$AUDIT_PATH/audit-report.md`:

```markdown
# Comprehensive Codebase Audit Report

## Executive Summary
> High-level overview of the project's health.

## Prioritized Action Items
Group the findings from all phases by severity:

### 🔴 Critical (Fix Immediately)
- [Issue description] (Source: Security/Architecture) - Recommendation

### 🟡 Moderate (Refactor Soon)
- [Issue description] - Recommendation

### 🔵 Minor / Technical Debt (When Time Permits)
- [Issue description] - Recommendation

## Detailed Findings
### 1. Guidelines & Clean Code
> Summary from Phase 1

### 2. Security
> Summary from Phase 2

### 3. Architecture & Patterns
> Summary from Phase 3
```

### 4C — Checkpoint
Update `$AUDIT_PATH/status.md` → 4. Final Report: `done`.

---

## Phase 5: Presentation

Present the `audit-report.md` to the user in the chat interface. Recommend the user pick a specific finding and use `/think` or `/forge` to plan and execute the refactoring.

---

## Global Rules

- **STRICTLY READ-ONLY**: You are **FORBIDDEN** from editing, deleting, or writing into any source code file, configuration, or project documentation. You may ONLY create tracking files inside `.orchestrator/audits/`.
- **NO AUTOMATIC COMMITS/PUSHES**: Do not touch or alter the Git repository in any way.
- **AUTOPILOT**: Proceed through ALL phases (0 through 4) sequentially and automatically. Do NOT stop to ask for permission.
- **SAFE TO AUTO-RUN**: All commands in this workflow (mkdir, checkpoints creation, etc.) are explicitly safe. Use `SafeToAutoRun: true` for all tool calls to avoid interrupting the user.
- **Constructive Tone**: All findings must be objective and actionable, pointing directly to the relevant files and line numbers where possible.
