---
description: ship — commits and pushes with pre-flight checks, interactive commit message, and safety nets.
---

# Ship Workflow

**Goal**: Ship code from the current feature branch to `main` with **pre-flight validation**, **interactive commit message**, **build verification**, and **safe merge**.

> Uses: `git-commit-formatter` skill (Conventional Commits)

---

## 🛑 SAFETY PROTOCOL
**THIS IS THE ONLY WORKFLOW ALLOWED TO MERGE AND PUSH TO MAIN.**
- No other automation (Forge, Checkpoint, etc.) is permitted to run `git push` or `git commit` on the `main` branch.
- This workflow must be triggered **MANUALLY** by a human programmer.

---

## Step 0: Collect Commit Message

If the user invoked `/ship "some message"`, use that message as-is.

If **no commit message** was provided in the invocation:

1. **Ask the user** for the commit message before proceeding.
   - Suggest a message based on the staged/unstaged changes using the `git-commit-formatter` skill (Conventional Commits format: `<type>[scope]: <description>`).
   - Example prompt:
     > I detected the following changes: `[summary of changes]`.
     > Suggested commit message: `feat(auth): implement google login`
     > Would you like to use this message, or provide your own?
2. **Wait for user confirmation** — do NOT proceed until a commit message is confirmed.

Store the final message as `$COMMIT_MSG`.

---

## Step 1: Pre-Flight Checks

// turbo-all

Run these validations **before** doing anything destructive:

### 1A — Branch Check

```bash
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD) && \
echo "Current branch: $CURRENT_BRANCH"
```

- If `CURRENT_BRANCH` is `main` → **ABORT**. Tell the user: _"You are already on `main`. Ship is meant to merge a feature branch into `main`. Please switch to a feature branch first."_

### 1B — Working Tree Check

```bash
git status --porcelain
```

- If output is empty → **WARN** the user: _"No changes detected. Are you sure you want to ship?"_ Wait for confirmation.
- If output is non-empty → Proceed (changes will be staged and committed).

### 1C — Build Verification (Optional but Recommended)

Detect the project type and run the appropriate build command:

```bash
# Node.js projects
if [ -f "package.json" ]; then
  npm run build 2>&1 || echo "⚠️ BUILD_FAILED"
fi

# Java/Spring Boot projects
if [ -f "pom.xml" ]; then
  mvn compile test -q 2>&1 || echo "⚠️ BUILD_FAILED"
fi
```

- If build fails → **STOP** and report the errors to the user. Do NOT proceed with a broken build.
- If no build system detected → Skip and proceed.

---

## Step 2: Commit Changes

// turbo

```bash
git add . && \
git commit -m "$COMMIT_MSG"
```

- If nothing to commit (all changes already committed via checkpoints) → Skip to Step 3.

---

## Step 3: Merge into Main

// turbo

```bash
git checkout main && \
git pull origin main
```

### 3A — Squash Merge & Commit

```bash
git merge --squash "$CURRENT_BRANCH" && \
git commit -m "$COMMIT_MSG"
```

- If merge **succeeds** → Proceed to Step 4.
- If merge **conflicts** → **STOP**. Report conflicted files to the user and provide guidance:
  > ⚠️ Merge conflicts detected in: `[file list]`
  > Please resolve the conflicts, then run `/ship` again.
  > To abort the merge: `git merge --abort`

---

## Step 4: Push to Remote

// turbo

```bash
git push origin main
```

- If push fails (e.g., remote rejected) → Report the error. Suggest `git pull --rebase origin main` and retry.

---

## Step 5: Cleanup

// turbo

```bash
git branch -D "$CURRENT_BRANCH"
```

- Force delete the branch since squash merge changes commit hashes, making -d fail.

---

## Step 6: Summary

Present a clear summary to the user:

```text
✅ Ship Complete (Squash Merge)!
─────────────────────────────
  Branch:  <CURRENT_BRANCH> → main (collapsing checkpoints)
  Commit:  <COMMIT_MSG>
  Push:    origin/main ✓
  Cleanup: branch <CURRENT_BRANCH> force-deleted ✓
─────────────────────────────
```

### Rollback Instructions

Always include rollback guidance after shipping:

> If you need to undo this ship:
>
> ```bash
> git revert HEAD    # creates a new commit undoing the merge
> git push origin main
> ```

---

## Rules

1. **Never invoke this workflow automatically.** It must be triggered by the user.
2. **Never skip Step 0.** The commit message must be confirmed before any git operations.
3. **Never force-push.** If `git push` fails, report and let the user decide.
4. **Cleanup after shipping.** Use `git branch -D` to ensure the feature branch is removed after its contents are committed to `main`.
5. **Always use Conventional Commits format** for the commit message (see `git-commit-formatter` skill).
