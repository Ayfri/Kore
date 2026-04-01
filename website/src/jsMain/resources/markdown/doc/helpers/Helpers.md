---
root: .components.layouts.MarkdownLayout
title: Helpers Utilities
nav-title: Helpers Utilities
description: Overview of helper-focused utilities in Kore - renderers, display entities, inventories, mannequins, math, raycasts, scheduling, state delegates, and particle helpers.
keywords: minecraft, datapack, kore, helpers, ansi, markdown, minimessage, raycast, math, area, state, vfx, particles, text, display, inventory, mannequin, scheduler
date-created: 2026-03-31
date-modified: 2026-04-01
routeOverride: /docs/helpers/utilities
position: 0
---

# Helpers Utilities

The `helpers` module contains utility-style features that build on top of Kore and, when needed, the OOP abstractions.
These helpers are not centered around long-lived object models, so they now live outside the `oop` module.

## Why use `helpers`

Use `helpers` when you want higher-level building blocks without introducing gameplay objects such as teams, timers, or
entity wrappers.

- **Rendering helpers** convert human-friendly text formats into Minecraft text components.
- **Display and entity helpers** cover display entities, mannequins, and other world-facing utility objects.
- **Math, scheduling, and geometry helpers** pre-generate command logic for raycasts, particle shapes, spatial zones,
  delayed execution, or fixed-point math.
- **State helpers** let you write concise Kotlin that still compiles to vanilla-friendly scoreboard and storage
  commands.

The module is designed to stay composable: you can use it with plain Kore functions or mix it with the
[`oop` module](/docs/oop/utilities) when a system grows beyond a few stateless helpers.

## Install this module

Add the `helpers` artifact when you want renderer, display, inventory, mannequin, math, raycast, scheduler, delegate, or
particle utilities on top of Kore.

```kotlin
dependencies {
  implementation("io.github.ayfri.kore:helpers:VERSION")
}
```

## Typical workflow

Most helper APIs follow the same pattern:

1. **Register or build** a helper once (`registerMath()`, `raycast { ... }`, `drawCircle(...)`, etc.).
2. **Call it from functions** wherever you need the generated commands.
3. **Combine it with core Kore or OOP** if you need selectors, scoreboards, teams, timers, or reusable gameplay
   objects.

That makes helpers a good fit for packs that start simple and progressively adopt more structure only where needed.

## All Features

- **[ANSI Renderer](/docs/helpers/ansi-renderer)** - Convert ANSI SGR escape sequences into Minecraft text components
  when your source text already contains terminal-style formatting.
- **[Area](/docs/helpers/area)** - Work with axis-aligned 3D regions for containment checks, intersections, unions,
  and coordinate transforms.
- **[Display Entities](/docs/helpers/display-entities)** - Configure block, item, and text display entities with shared
  transformations, billboards, brightness, and interpolation settings.
- **[Inventory Manager](/docs/helpers/inventory-manager)** - Register slot listeners and container policies for player,
  entity, or block inventories.
- **[Mannequins](/docs/helpers/mannequins)** - Build mannequin entities with profiles, hidden layers, poses, and hand
  selection.
- **[Markdown Renderer](/docs/helpers/markdown-renderer)** - Turn Markdown snippets into rich Minecraft text for chat,
  titles, signs, or boss bars.
- **[MiniMessage Renderer](/docs/helpers/minimessage-renderer)** - Parse Adventure MiniMessage tags into Minecraft
  text components while keeping authoring ergonomic.
- **[Raycasts](/docs/helpers/raycasts)** - Generate recursive step-based raycasts with callbacks for hits, misses, and
  per-step side effects.
- **[Scheduler](/docs/helpers/scheduler)** - Schedule delayed or repeating functions with load-time registration and
  cancelation helpers.
- **[Scoreboard Math](/docs/helpers/scoreboard-math)** - Reuse fixed-point math routines such as trigonometry,
  square-root, distance, and projectile formulas.
- **[State Delegates](/docs/helpers/state-delegates)** - Map scoreboard objectives or NBT storage paths to Kotlin
  properties for terser command-generation code.
- **[VFX Particles](/docs/helpers/vfx-particles)** - Generate circles, spheres, spirals, helixes, and lines as
  reusable particle functions.

## Helpers vs OOP

Choose the `helpers` module when you mainly need:

- data conversion and rendering,
- world-facing utility objects such as displays, mannequins, and inventory controllers,
- geometry or math utilities,
- scheduling helpers,
- lightweight compile-time sugar over vanilla commands.

Choose the [`oop` module](/docs/oop/utilities) when you need named gameplay objects such as players, teams,
scoreboards, timers, spawners, or state machines.

If you need entities, teams, scoreboards, items, timers, or other object-oriented gameplay abstractions, see the
[OOP Utilities](/docs/oop/utilities) page.