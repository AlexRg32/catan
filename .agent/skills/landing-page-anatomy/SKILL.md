---
name: landing-page-anatomy
description: Expert guidelines for designing high-converting, "WOW" factor landing pages. Covers psychological structure (AIDA), component order, and Conversion Rate Optimization (CRO).
---

# Landing Page Anatomy Expert

> ⚠️ **IMPORTANT SCRIPTING SCOPE**: This skill is **STRICTLY** for public-facing, marketing-oriented Landing Pages.
> Do **NOT** apply these structural rules (like gigantic Hero sections or pricing tiers) when building internal SaaS dashboards, user settings panels, or functional app screens. For functional SaaS UIs, defer to `frontend-dev-guidelines`.

This skill provides the architectural and psychological blueprint for building top-tier, high-converting landing pages. A premium landing page is not just beautiful; it is engineered to guide the user towards a specific action (Conversion).

## 1. The AIDA Framework (Attention, Interest, Desire, Action)

Every landing page must follow a psychological flow. Do not just throw components on a screen.

1. **Attention (The Hero Section)**: The top 100vh. Must grab the user in under 3 seconds.
2. **Interest (The Problem/Solution)**: Empathize with the user's pain points and introduce your product as the answer.
3. **Desire (Social Proof & Benefits)**: Show why others love it (testimonials, logos) and the deep benefits (not just features).
4. **Action (The Final CTA)**: A clear, frictionless path to purchase, sign up, or contact.

## 2. Mandatory Structural Components (Top to Bottom)

When designing a full landing page, follow this hierarchical structure:

### A. The Navbar (Glassmorphic & Sticky)

- Must be sticky or become sticky on scroll.
- Use a slight glassmorphic effect (`backdrop-blur-md bg-white/70`, adjust for dark mode).
- Must have a clear primary CTA on the right side.

### B. The Hero Section (The "WOW" Factor)

- **Headline**: Large, bold, readable typography (e.g., `text-5xl md:text-7xl font-extrabold tracking-tight`).
- **Subheadline**: 1-2 sentences explaining exactly what it is and for whom.
- **Primary CTA**: High contrast button with an action-oriented verb (e.g., "Start Building Free").
- **Visual**: A stunning 3D graphic, abstract animated background, or a high-fidelity product mockup.
- *Micro-interaction*: The hero elements should fade up smoothly on load.

### C. Social Proof Logo Cloud (Trust Banner)

- Immediately below the Hero. A horizontal, infinitely scrolling marquee of client logos or "As seen on" badges. Rendered in monochrome/grayscale to not distract from the main CTA.

### D. Features / "How it Works" (Bento Box Layout)

- Do not use boring 3-column lists. Use modern **Bento Box Grids** (asymmetrical grids that pack different sized feature cards tightly).
- Give each card a subtle hover effect (`hover:-translate-y-1 hover:shadow-xl transition-all duration-300`).

### E. Deep Dive / Alternating Features

- Standard layout: Text on left, Image on right. Next section: Image on left, Text on right.
- Provide visual rhythm. Add curved SVG dividers between sections if appropriate for the brand.

### F. Social Proof (Testimonial Grid or Carousel)

- Must include photos of reviewers, names, roles, and realistic quotes.
- Use subtle star ratings (⭐⭐⭐⭐⭐).

### G. Pricing (The Offer)

- If applicable. Use 3 tiers. Highlight the "Pro" or "Popular" tier by making it physically larger, giving it a primary border color, and adding a "Most Popular" badge.

### H. The Final CTA (The Closer)

- A standalone, centered section near the bottom.
- High contrast background (often the primary brand color).
- Headline reminding them of the value proposition. Large button. No distractions.

### I. The Footer

- Clean, organized links. Social icons. Copyright.

## 3. CRO (Conversion Rate Optimization) Rules

- **One Clear Goal**: The page should have a single primary action (e.g., "Sign Up"). Do not confuse the user with 5 different types of buttons.
- **Contrast**: CTA buttons must use a color that stands out sharply against the background.
- **Readability**: Line lengths for text should not exceed 75 characters (`max-w-prose`). Use sufficient line-height (`leading-relaxed`).

## 4. Integration with other skills

- Combine this with `premium-animations` to bring the components to life on scroll.
- Combine with `advanced-tailwind-styling` to execute the Bento boxes and glassmorphism.
