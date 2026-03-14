---
name: stitch-designs
description: Expert guidance on using the Stitch MCP server to generate and maintain professional, premium frontend designs. Integrates project context, extracts design tokens, and generates high-quality React/Tailwind code.
---

# Stitch Design Expert

This skill provides expert guidance for crafting effective prompts in **Google Stitch**, integrated via the **StitchMCP** server. It ensures that every UI generation is professional, high-fidelity, and perfectly aligned with the project's specific domain and theme.

## 1. Core Principles

* **Design-Led Development**: Use Stitch to generate UI screens and components before implementation.
* **Aesthetics First (WOW Factor)**: Interfaces must feel premium and "alive". Use vibrant colors, smooth transitions, and modern aesthetics (glassmorphism, soft shadows, micro-animations).
* **Context-Aware Mobile-First Focus**: Validate the primary platform for the application (e.g., if users are on the go, designs must be optimized for smartphones first).
* **Brand Alignment**: Always align the generated UI colors, typography, and mood with the project's identity.

## 2. StitchMCP Tooling Reference

When executing the design phase, leverage these tools:

* `mcp_StitchMCP_generate_screen_from_text`: Create a new screen. **Always provide detailed, sensory prompts.**
* `mcp_StitchMCP_edit_screens`: Refine existing screens using natural language annotations.
* `mcp_StitchMCP_generate_variants`: Explore different visual directions for the same requirement.
* `mcp_StitchMCP_get_screen`: Retrieve the underlying code (HTML/CSS) to translate into React/Tailwind components.

## 3. Expert Prompting Principles

### A. Be Specific and Context-Rich

Generic prompts like "create a login" will fail to produce premium results. Describe the specific project context.

**Effective Prompt Example:**
> "Mobile-first SMS verification screen for a modern fintech app. Includes a name field, phone number field with country selector, and a 'Verify' button. Style: Trustworthy, secure, with a subtle dark mode gradient background. Use a glassmorphic card for the form."

### B. Define Visual Style and Theme

Explicitly specify the design tokens to the Stitch MCP based on the project's brand:
* **Palette**: Define Primary, Secondary, and Neutral colors.
* **Aesthetic**: E.g., Premium, Modern, Energetic, Minimalist, Corporate.
* **Typography**: Clean, readable sans-serif (e.g., Inter, Roboto, or Outfit) with elegant headings.

### C. Structure Functional Requirements

List the interactive logic to help Stitch generate the right components.

**Example for a Detail Page:**
> "Detail screen for a marketplace item. Features: High-res image gallery header, seller info, rich text description, review stars, and a prominent 'Buy Now' sticky footer button. Include a 'Saved' state toggle."

## 4. Prompt Structure Template

Use this template for comprehensive UI generations via Stitch:

```text
[Screen/Component Type] for [Project Name] [User/Context]

Key Features:
- [Feature 1: e.g., QR scanner overlay]
- [Feature 2: e.g., List of items with specific badges]
- [Feature 3: e.g., Floating action button]

Visual Style:
- Palette: [Primary/Accent colors]
- Mood: [e.g., Vibrant, professional, trustworthy]
- Layout: [e.g., Mobile-first, card-based, data-grid]

Platform: [Mobile Web / Desktop Dashboard]
```

## 5. Iteration Strategies

### Refine via Edit/Annotation

Use `edit_screens` to make precise adjustments without a full regenerate:
* "Make the primary action button larger and use a pulsing animation effect."
* "Change the background color to a warmer cream #FDF5E6."
* "Adjust the spacing between the cards to be more compact."

### Progressive Refinement

1. **Initial**: Generate the broad layout (e.g., "Main list of items").
2. **Follow-up**: Add specific details (e.g., "Add search bar and filter by category").
3. **Polish**: Add the brand identity (e.g., "Apply the project's official theme and add hover micro-interactions to the cards").

## 6. Integration with Forge Workflow

In the **Design Phase (Phase 2)**:

1. **Context Extraction**: Investigate the business rules to understand what elements are needed.
2. **Stitch Generation**: Use `generate_screen_from_text` for new features defined in the `Investigation Phase`.
3. **Variant Selection**: Use `generate_variants` to present the user with 3 options if the design direction is ambiguous.
4. **Handoff**: Once a design is approved, extract the code with `get_screen`. **CRITICAL**: Refactor the generated HTML/CSS into React components following the `frontend-dev-guidelines` (Tailwind, Logic/Presentation split via hooks).

## 7. Anti-Patterns to Avoid

* ❌ **No Context**: "Create a menu." (Yields a generic restaurant menu, even if it's a software settings menu).
* ❌ **Default Styling**: Forgetting to mention "Premium", "Modern", or specific brand traits (Yields boring browser-default looks).
* ❌ **Missing States**: Forgetting to prompt for "Empty states", "Loading states", or "Error states".

## When to Use

This skill is applicable whenever the Forge process enters the **Design Phase** or when a user requests UI improvements to the frontend.
