---
name: premium-animations
description: Expert guidelines for implementing high-end, silky smooth animations in web applications using CSS, Tailwind, and Framer Motion.
---

# Premium Web Animations Expert

This skill dictates how to bring a web interface to life. Premium landing pages do not feel static; they react to the user and guide the eye dynamically. However, bad animations look cheap and hurt performance. We aim for **"Apple-level"** or **"Stripe-level"** smoothness.

## 1. The Core Philosophy of Premium Animation

1. **Purposeful**: Animations should serve a purpose (drawing attention to a CTA, indicating state change, showing hierarchy), not just "wiggling for fun".
2. **Subtle & Fast**: Premium animations are quick. Transitions should generally be between `150ms` and `300ms`. Elements fading in on scroll should not make the user wait.
3. **Hardware Accelerated**: Only animate properties that don't trigger CSS repaints: `transform` (translating, scaling) and `opacity`. Never animate `width`, `height`, `margin`, or `top`/`left`.
4. **Easing is Everything**: Linear animations look robotic. Use custom cubic-bezier curves for organic, springy, or snappy feels.

## 2. Tailwind CSS Micro-Interactions (Hover/Focus)

Use these pure Tailwind utility combinations for instantaneous premium feel on interactable elements (buttons, cards).

### The "Floating Card" Effect

```html
<div class="transition-all duration-300 ease-out hover:-translate-y-2 hover:shadow-[0_20px_40px_-15px_rgba(0,0,0,0.1)]">
  Content
</div>
```

### The "Slight Press" Button

```html
<button class="transition-transform duration-150 active:scale-95 hover:scale-105">
  Click Me
</button>
```

### The "Glow on Hover" effect

Use relative positioning and a blurred pseudo-element or absolute div behind the main element.

```html
<div class="relative group cursor-pointer">
  <div class="absolute -inset-0.5 bg-gradient-to-r from-pink-600 to-purple-600 rounded-lg blur opacity-20 group-hover:opacity-60 transition duration-500"></div>
  <div class="relative bg-white dark:bg-black rounded-lg p-6">Content</div>
</div>
```

## 3. Reveal on Scroll (Using Intersection Observer or Framer Motion)

High-end landing pages reveal content elegantly as the user scrolls down.

### Using Framer Motion (React)

If the project uses React, **Framer Motion is highly recommended** for complex spatial and scroll animations.

**The Standard Staggered Fade-Up:**
Instead of slamming all content on the screen, fade it up slightly organically.

```jsx
import { motion } from 'framer-motion';

const containerVariants = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: { staggerChildren: 0.1 }
  }
};

const itemVariants = {
  hidden: { opacity: 0, y: 20 },
  visible: { opacity: 1, y: 0, transition: { type: 'spring', stiffness: 100, damping: 20 } }
};

// Usage: Viewport triggered
<motion.div variants={containerVariants} initial="hidden" whileInView="visible" viewport={{ once: true, margin: "-100px" }}>
  <motion.div variants={itemVariants}>Item 1</motion.div>
  <motion.div variants={itemVariants}>Item 2</motion.div>
</motion.div>
```

## 4. Advanced Effects

### Infinite Marquee

For logo clouds or text tickers. Use pure CSS keyframes.
Extend `tailwind.config.js`:

```javascript
theme: {
  extend: {
    animation: {
      'marquee': 'marquee 25s linear infinite',
    },
    keyframes: {
      marquee: {
        '0%': { transform: 'translateX(0%)' },
        '100%': { transform: 'translateX(-100%)' },
      }
    }
  }
}
```

### Parallax

Keep it subtle. Only move background elements or decorative blobs slightly slower/faster than scroll speed. Deep parallax can cause motion sickness and severe performance drops on mobile. Use Framer Motion's `useScroll` and `useTransform` hooks.

## 5. Mobile vs Desktop Considerations

- What looks good on desktop might be chaotic on mobile.
- **Rule of Thumb**: Reduce animation complexity and travel distance (translate pixels) on mobile screens.
- Respect `prefers-reduced-motion`. Tailwind has a modifier variant for this: `motion-reduce:transition-none`. Always keep accessibility in mind.
