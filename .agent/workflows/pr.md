---
description: pr — commits, pushes the current branch, and provides a PR link for manual merging.
---

# Pull Request Workflow

**Goal**: Push code from the current feature branch to `origin` and provide a **Pull Request link** for manual merging on GitHub.

> Uses: `git-commit-formatter` skill (Conventional Commits)

---

## 🛑 SAFETY PROTOCOL
- This workflow is allowed to `git push` **ONLY** to feature branches.
- **NEVER** push to `main` using this workflow.
- This workflow must be triggered **MANUALLY** by a human programmer.

---

## Step 0: Collect Commit Message

If the user invoked `/pr "some message"`, use that message as-is.

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

- If `CURRENT_BRANCH` is `main` → **ABORT**. Tell the user: _"You are already on `main`. The `pr` workflow is meant to push a feature branch and create a PR. Please switch to a feature branch first."_

### 1B — Working Tree Check

```bash
git status --porcelain
```

- If output is empty → **WARN** the user: _"No changes detected. Are you sure you want to push?"_ Wait for confirmation.
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
  mvn compile -q 2>&1 || echo "⚠️ BUILD_FAILED"
fi
```

- If build fails → **STOP** and report the errors to the user. Do NOT proceed with a broken build.
- If no build system detected → Skip and proceed.

---

## Step 2: Commit Changes

// turbo

```bash
git add . && \
git commit -m "$COMMIT_MSG" || echo "Nothing to commit, working tree clean."
```

---

## Step 3: Push to Remote

// turbo

```bash
git push origin "$CURRENT_BRANCH"
```

- If push fails (e.g., remote rejected) → Report the error. Suggest `git pull --rebase origin "$CURRENT_BRANCH"` and retry.

---

## Step 4: Generate Pull Request Link

// turbo

Identify the remote repository details and build the GitHub comparison link.

```bash
REMOTE_URL=$(git config --get remote.origin.url | sed 's/\.git$//' | sed 's/git@github.com:/https:\/\/github.com\//') && \
echo "PR_LINK: $REMOTE_URL/compare/main...$CURRENT_BRANCH?expand=1"
```

---

## Step 5: Summary

Present a clear summary to the user:

```text
🚀 Changes Pushed to Remote!
─────────────────────────────
  Branch:  <CURRENT_BRANCH>
  Commit:  <COMMIT_MSG>
  Push:    origin/<CURRENT_BRANCH> ✓
─────────────────────────────

🔗 Create Pull Request:
[Click here to create the PR on GitHub](<PR_LINK>)

Manual next steps:
1. Open the link above.
2. Review the changes on GitHub.
3. Merge the PR manually when ready.
```

---

## Rules

1. **Never invoke this workflow automatically.** It must be triggered by the user.
2. **Never skip Step 0.** The commit message must be confirmed before any git operations.
3. **Never force-push.** If `git push` fails, report and let the user decide.
4. **Always use Conventional Commits format** for the commit message (see `git-commit-formatter` skill).
