---
root: .components.layouts.MarkdownLayout
title: Helpers Utilities
nav-title: Helpers Utilities
description: Overview of helper-focused utilities in Kore — renderers, math, raycasts, areas, state delegates, and particle helpers.
keywords: minecraft, datapack, kore, helpers, ansi, markdown, minimessage, raycast, math, area, state, vfx, particles, text
date-created: 2026-03-31
date-modified: 2026-03-31
routeOverride: /docs/helpers/utilities
position: 0
---

# Helpers Utilities

The `helpers` module contains utility-style features that build on top of Kore and, when needed, the OOP abstractions.
These helpers are not centered around long-lived object models, so they now live outside the `oop` module.

## All Features

- **[ANSI Renderer](/docs/helpers/ansi-renderer)** — Convert ANSI SGR escape sequences into Minecraft text components.
- **[Area](/docs/helpers/area)** — Axis-aligned 3D bounding box with geometric operations and containment checks.
- **[Markdown Renderer](/docs/helpers/markdown-renderer)** — Convert Markdown-formatted text into Minecraft text
  components.
- **[MiniMessage Renderer](/docs/helpers/minimessage-renderer)** — Parse Adventure MiniMessage format strings into
  Minecraft text components.
- **[Raycasts](/docs/helpers/raycasts)** — Recursive step-based raycasting with callbacks.
- **[Scoreboard Math](/docs/helpers/scoreboard-math)** — Trigonometry, square root, distance, and parabolic trajectory
  using scoreboard operations.
- **[State Delegates](/docs/helpers/state-delegates)** — Kotlin property delegates that map scoreboards or NBT storage
  to `var` properties.
- **[VFX Particles](/docs/helpers/vfx-particles)** — Geometric particle shapes (circles, spheres, spirals, helixes,
  lines).

If you need entities, teams, scoreboards, items, timers, or other object-oriented gameplay abstractions, see the
[OOP Utilities](/docs/oop/utilities) page.