---
name: Xoai Aura
colors:
  surface: '#fbf9f8'
  surface-dim: '#dbd9d9'
  surface-bright: '#fbf9f8'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f5f3f3'
  surface-container: '#efeded'
  surface-container-high: '#eae8e7'
  surface-container-highest: '#e4e2e2'
  on-surface: '#1b1c1c'
  on-surface-variant: '#42493e'
  inverse-surface: '#303030'
  inverse-on-surface: '#f2f0f0'
  outline: '#72796e'
  outline-variant: '#c2c9bb'
  surface-tint: '#3b6934'
  primary: '#154212'
  on-primary: '#ffffff'
  primary-container: '#2d5a27'
  on-primary-container: '#9dd090'
  inverse-primary: '#a1d494'
  secondary: '#665d4e'
  on-secondary: '#ffffff'
  secondary-container: '#ebdecb'
  on-secondary-container: '#6b6152'
  tertiary: '#393a29'
  on-tertiary: '#ffffff'
  tertiary-container: '#50513f'
  on-tertiary-container: '#c3c4ac'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#bcf0ae'
  primary-fixed-dim: '#a1d494'
  on-primary-fixed: '#002201'
  on-primary-fixed-variant: '#23501e'
  secondary-fixed: '#eee1ce'
  secondary-fixed-dim: '#d1c5b3'
  on-secondary-fixed: '#211b0f'
  on-secondary-fixed-variant: '#4e4638'
  tertiary-fixed: '#e4e4cc'
  tertiary-fixed-dim: '#c8c8b0'
  on-tertiary-fixed: '#1b1d0e'
  on-tertiary-fixed-variant: '#474836'
  background: '#fbf9f8'
  on-background: '#1b1c1c'
  surface-variant: '#e4e2e2'
typography:
  display-lg:
    fontFamily: Libre Caslon Text
    fontSize: 64px
    fontWeight: '400'
    lineHeight: '1.1'
    letterSpacing: -0.02em
  display-lg-mobile:
    fontFamily: Libre Caslon Text
    fontSize: 40px
    fontWeight: '400'
    lineHeight: '1.2'
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Libre Caslon Text
    fontSize: 32px
    fontWeight: '400'
    lineHeight: '1.3'
  headline-sm:
    fontFamily: Libre Caslon Text
    fontSize: 24px
    fontWeight: '400'
    lineHeight: '1.4'
  body-lg:
    fontFamily: Manrope
    fontSize: 18px
    fontWeight: '400'
    lineHeight: '1.6'
  body-md:
    fontFamily: Manrope
    fontSize: 16px
    fontWeight: '400'
    lineHeight: '1.6'
  label-caps:
    fontFamily: Manrope
    fontSize: 12px
    fontWeight: '600'
    lineHeight: '1.2'
    letterSpacing: 0.1em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  unit: 8px
  container-max: 1200px
  gutter: 24px
  margin-mobile: 20px
  margin-desktop: 64px
  section-gap: 120px
---

## Brand & Style

The brand personality is centered on "Healing & Renewal," positioning itself as a sanctuary of serenity and high-end wellness. The target audience includes discerning individuals seeking respite from high-stress environments through holistic luxury and environmental connection.

The design style is **Organic Minimalism**. It prioritizes expansive, airy whitespace to mimic the feeling of deep breaths and physical space. The aesthetic blends the precision of modern luxury with the soft, imperfect curves found in nature. By utilizing a "High-End Spa" visual language, the UI evokes an immediate sense of lowered heart rate and mental clarity. Every interaction should feel intentional, quiet, and effortless, avoiding visual noise or aggressive calls to action.

## Colors

The palette is derived from a forest floor at dawn, utilizing grounding earth tones and restorative greens. 

- **Primary (Forest Green):** Used for key brand moments, primary buttons, and deep backgrounds. It represents the strength and quietude of the forest.
- **Secondary (Earth Beige):** A warm, tactile neutral used for surfaces and containers to provide a softer alternative to pure white.
- **Tertiary (Sand):** A light, airy tone used for large background areas to maintain high-key lighting.
- **Neutral:** A muted charcoal for typography to ensure readability without the harshness of pure black.

Functional colors (Success, Error) should be desaturated to fit the palette—think sage green for success and a dusty terracotta for errors.

## Typography

This design system utilizes a high-contrast typographic pairing to balance heritage and modernity.

**Headlines (Libre Caslon Text):** An elegant, classic serif that carries the weight of authority and timelessness. It should be used with generous leading and occasional italicization for emphasis on "human" or "healing" keywords.

**Body & UI (Manrope):** A clean, balanced sans-serif chosen for its technical clarity and professional warmth. Its geometric influence ensures that functional text feels modern and unobtrusive. 

For mobile, large display sizes scale down aggressively to maintain a single-column focal point without overwhelming the viewport.

## Layout & Spacing

The layout philosophy follows a **Fixed Grid** with an emphasis on "negative space as content." 

- **Desktop:** A 12-column grid with wide margins (64px) to ensure content feels centered and calm.
- **Mobile:** A 4-column grid with 20px margins.
- **Rhythm:** Use a strict 8px baseline grid. Section vertical spacing should be significantly larger than standard web apps (up to 120px) to allow the eye to rest between content blocks.

Elements should often be offset from the grid slightly or use asymmetrical padding to mimic organic, non-linear growth patterns.

## Elevation & Depth

Depth is conveyed through **Tonal Layers** and **Ambient Shadows** rather than stark borders.

- **Surfaces:** Use the Earth Beige (#EADDCA) to create subtle separation from the Sand (#F5F5DC) background.
- **Shadows:** Shadows should be extremely soft, using a deep green or warm brown tint instead of gray. (e.g., `box-shadow: 0 10px 40px rgba(45, 90, 39, 0.05)`).
- **Glassmorphism:** Use sparingly for navigation overlays. A light blur (10px) with high transparency ensures the "airy" feel is maintained even when UI elements overlap imagery.

## Shapes

The shape language avoids harsh 90-degree angles. A "Rounded" setting (0.5rem base) is applied to most UI components to maintain a soft, approachable feel.

For imagery and high-level containers, use **asymmetrical radii** (e.g., top-left: 100px, bottom-right: 20px) to create "pebble" or "leaf" silhouettes. These organic forms differentiate the brand from standard corporate structures.

## Components

- **Buttons:** Primary buttons use the Forest Green background with white text. They should have ample horizontal padding and use a subtle "grow" transition on hover rather than a color change.
- **Inputs:** Fields should be "ghost style" with a bottom border only, or a very light Earth Beige fill with no border. Focus states use a soft Forest Green glow.
- **Cards:** Use large corner radii and ambient shadows. Images within cards should always have a slight zoom-on-hover effect to feel "alive."
- **Chips/Labels:** Use the `label-caps` typography style. Backgrounds should be low-contrast (e.g., Earth Beige on Sand).
- **Navigation:** A minimal top bar with high transparency. Menu items should have wide spacing to avoid a "cluttered" header.
- **Additional Components:**
    - **Date Pickers:** Should emphasize "Stay Duration" with soft range highlighting in Forest Green.
    - **Quote Blocks:** Use large serif italics for testimonials, flanked by ample whitespace.
