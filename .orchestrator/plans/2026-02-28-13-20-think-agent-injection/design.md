# Design: Agent Injection Strategies

## Architecture Overview

The `.agent` directory is essentially a set of Markdown files that power your AI IDE/orchestrator. We need to define standard operating procedures (SOPs) for propagating this folder to new or existing projects.

## Options Evaluated

### Option A: The "GitHub Template Repository" Approach (Recommended for New Projects)

- **How it works:** You create a repository named `spring-react-agent-template` and mark it as a "Template" in GitHub settings. This repository contains only the `.agent` folder and maybe empty `frontend/` and `backend/` directories.
- **Workflow:** When starting a new project, you click "Use this template" on GitHub. It clones the entire structure instantly.
- **Pros:** Zero setup required for new projects.
- **Cons:** If you update the `.agent` rules in the template, old projects don't automatically get the updates.

### Option B: The "Global CLI Setup Script" Approach (Recommended for Existing Projects)

- **How it works:** You keep your `.agent` folder in a central repository (e.g., `~/Desktop/SpringReactAgent`). You create a simple bash alias or script on your Mac, like `agent-inject`, which syncs the folder to your current `$PWD`.
- **Workflow:** You initialize a new Spring Boot app. You `cd` into it. You run `agent-inject`. Boom, the `.agent` folder is copied.
- **Pros:** Fast, works on any existing project, doesn't require GitHub UI.
- **Cons:** Same as Option A, it runs once. Updates require re-running the script.

### Option C: The "Git Submodule" Approach (Not Recommended)

- **How it works:** The `.agent` folder is a Git Submodule linked to a master repository.
- **Pros:** One source of truth. Updating the master repository updates all projects.
- **Cons:** Git Submodules are notoriously annoying to work with. IDEs often struggle with indexing them, and committing changes from child repositories is complex.

## File Structure (Option B Script Proposal)

If leaning towards Option B, we would create a script like this that you can source in your `~/.zshrc`:

```bash
# ~/.zshrc equivalent
function inject_agent() {
    local source_dir="/Users/alex32/Desktop/agent/.agent"
    local target_dir="${PWD}/.agent"

    if [ ! -d "$source_dir" ]; then
        echo "Error: Source template not found at $source_dir"
        return 1
    fi

    echo "Injecting AI Agent templates into $PWD..."
    cp -r "$source_dir" "$target_dir"
    echo "Done! Don't forget to update .agent/contexts/project-domain.md"
}
```

## Recommendation

- For **completely new projects**: Use **Option A** (GitHub Template).
- For **injecting into projects you already started**: Use a simple bash alias like **Option B**.
We will document BOTH approaches directly in your `MANUAL.md` so you always remember how to do it.
