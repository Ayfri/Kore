---
root: .components.layouts.MarkdownLayout
title: "Helpers Module: Utilities for Minecraft Datapacks"
nav-title: Helpers Module
description: Overview of helper-focused utilities in Kore - renderers, displays, interactions, inventories, mannequins, math, NBT paths, raycasts, scheduling, and state.
keywords: minecraft, datapack, kore, helpers, block id, ansi, markdown, minimessage, raycast, math, vector, area, state, nbt, vfx, particles, text, display, interaction, inventory, mannequin, scheduler, menu, dialog, sidebar
date-created: 2026-03-31
date-modified: 2026-11-11
routeOverride: /docs/helpers/utilities
---

# Helpers Module

The `helpers` module contains utility-style features that build on top of Kore and, when needed, the OOP abstractions.
These helpers are not centered around long-lived object models, so they live outside the `oop` module.

## Why use `helpers`

Use `helpers` when you want higher-level building blocks without introducing gameplay objects such as teams, timers, or
entity wrappers.

- **Rendering helpers** convert human-friendly text formats into Minecraft text components.
- **Display and entity helpers** cover display entities, interactions, mannequins, and other world-facing utility
  objects.
- **Math, scheduling, and geometry helpers** pre-generate command logic for raycasts, particle shapes, spatial zones,
  delayed execution, or fixed-point math.
- **State helpers** let you write concise Kotlin that still compiles to vanilla-friendly scoreboard and storage
  commands.

The module is designed to stay composable: you can use it with plain Kore functions or mix it with the
[`oop` module](/docs/oop/oop-utilities) when a system grows beyond a few stateless helpers.

## Install this module

Add the `helpers` artifact when you want renderer, display, interaction, inventory, mannequin, math, raycast, scheduler,
delegate, or particle utilities on top of Kore.

```kotlin
dependencies {
  implementation("io.github.ayfri.kore:helpers:{{kore.release}}")
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

- **[Areas](/docs/helpers/area)** - Work with axis-aligned 3D regions for containment checks, intersections, unions,
  and coordinate transforms.
- **[Block IDs](/docs/helpers/block-ids)** - Read the block at any position as an integer score with one
  `compute block` command and a generated context int provider.
- **[Conversations](/docs/helpers/conversations)** - Script per-player NPC dialogue with timed lines, click pauses, and
  dialog or chat choices.
- **[Display Entities](/docs/helpers/display-entities)** - Configure block, item, and text display entities with shared
  transformations, billboards, brightness, and interpolation settings.
- **[Interactions & NPCs](/docs/helpers/interactions)** - Make clickable hitboxes and clickable mannequin NPCs with right and
  left click callbacks, generated as advancements and functions with no tick function.
- **[Inventory Manager](/docs/helpers/inventory-manager)** - Register slot listeners and container policies for player,
  entity, or block inventories.
- **[Mannequins](/docs/helpers/mannequins)** - Build mannequin entities with profiles, hidden layers, poses, and hand
  selection.
- **[Menus](/docs/helpers/menus)** - Build dialog-based GUIs whose buttons run functions for any player, with
  sub-pages, links, and pause screen or Smithed menu entries.
- **[NBT Paths](/docs/helpers/nbt-paths)** - Build typed NBT paths and resolve trees that mix literal values with
  runtime scores.
- **[Raycasts](/docs/helpers/raycasts)** - Generate recursive step-based raycasts with callbacks for hits, misses, and
  per-step side effects.
- **[Scheduler](/docs/helpers/scheduler)** - Schedule delayed or repeating functions with load-time registration and
  cancelation helpers.
- **[Score Vectors](/docs/helpers/score-vectors)** - Runtime 3D vectors for look directions, dashes, knockback,
  exact lengths, teleports, and motion.
- **[Scoreboard Math](/docs/helpers/scoreboard-math)** - Fixed-point sine, cosine, atan2, and exact integer
  square root.
- **[Sidebars](/docs/helpers/sidebars)** - Show up to 15 custom lines on the sidebar, with right-aligned values, live
  scores, and conditional lines.
- **[State Delegates](/docs/helpers/state-delegates)** - Map scoreboard objectives or NBT storage paths to Kotlin
  properties for terser command-generation code.
- **[Text Renderers](/docs/helpers/text-renderers)** - Turn
  [MiniMessage](/docs/helpers/text-renderers#minimessage) tags, [Markdown](/docs/helpers/text-renderers#markdown)
  snippets, or [ANSI](/docs/helpers/text-renderers#ansi) escape sequences into rich Minecraft text for chat, titles,
  signs, or boss bars, while keeping authoring ergonomic.
- **[Particle Shapes](/docs/helpers/vfx-particles)** - Generate circles, spheres, spirals, helixes, and lines as
  reusable particle functions.

## Helpers vs OOP

Choose the `helpers` module when you mainly need:

- data conversion and rendering,
- world-facing utility objects such as displays, interactions, mannequins, and inventory controllers,
- geometry or math utilities,
- scheduling helpers,
- lightweight compile-time sugar over vanilla commands.

Choose the [`oop` module](/docs/oop/oop-utilities) when you need named gameplay objects such as players, teams,
scoreboards, timers, spawners, or state machines.

If you need entities, teams, scoreboards, items, timers, or other object-oriented gameplay abstractions, see the
[OOP Module](/docs/oop/oop-utilities) page.
