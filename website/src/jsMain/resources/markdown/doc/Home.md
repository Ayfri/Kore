---
root: .components.layouts.MarkdownLayout
title: Home
nav-title: Home
description: Welcome to the Kore wiki!
keywords: minecraft, datapack, kore, guide
date-created: 2024-04-06
date-modified: 2026-03-31
routeOverride: /docs/home
position: 0
---

# Kore

**Welcome to the Kore wiki!**

Kore is a Kotlin library for building Minecraft datapacks with a concise, type-safe Kotlin DSL. It focuses on readable builders, stable
generation of datapack JSON, and tight integration with vanilla concepts (functions, loot tables, predicates, worldgen, ...).

## Quick start

- **Getting started**: Check out the [Getting Started](/docs/getting-started) guide for a step-by-step introduction to creating your first datapack.
- **Prerequisites**: Java 21+ and a Kotlin-capable build environment.
- **Starter template**: use the `Kore Template` for a ready-to-run project: [
  `Kore Template`](https://github.com/Kore-Minecraft/Kore-Template).
- **Create & generate**: see [Creating A Datapack](/docs/guides/creating-a-datapack) for lifecycle and output options (`.generate()`,
  `.generateZip()`, `.generateJar()`).

## Installable modules

Kore is split into installable modules. Start with `kore`, then add the others depending on the abstractions or tooling
you need.

### `kore` - Core DSL

- Build datapacks with the main Kore DSL.
- Artifact: `io.github.ayfri.kore:kore:VERSION`
- Snapshot builds from each commit on `master`: add `https://central.sonatype.com/repository/maven-snapshots/` and use
  `VERSION-SNAPSHOT`
- Start here: [Getting Started](/docs/getting-started)

### `oop` - Object-oriented gameplay utilities

- Add higher-level abstractions for entities, teams, scoreboards, timers, and gameplay state.
- Artifact: `io.github.ayfri.kore:oop:VERSION`
- Explore: [OOP Utilities](/docs/oop/utilities)

### `helpers` - Utility-focused helpers

- Add renderers, raycasts, scoreboard math, state delegates, particle helpers, and related utilities.
- Artifact: `io.github.ayfri.kore:helpers:VERSION`
- Explore: [Helpers Utilities](/docs/helpers/utilities)

### `bindings` - Datapack importer

- Import existing datapacks and generate type-safe Kotlin bindings for their functions, resources, and tags.
- Artifact: `io.github.ayfri.kore:bindings:VERSION`
- Explore: [Bindings](/docs/advanced/bindings)

{{{ .components.doc.FeatureGrid }}}

### Minimal example

```kotlin
fun main() {
    dataPack("example") {
        function("display_text") { tellraw(allPlayers(), textComponent("Hello World!")) }
    }.generateZip()
}
```

## Essential reading

- **[Getting Started](/docs/getting-started)**: step-by-step guide to create your first datapack.
- **[Creating A Datapack](/docs/guides/creating-a-datapack)**: lifecycle, output paths, and generation options.
- **[Commands](/docs/commands/commands)**: comprehensive guide to all Minecraft commands with examples.
- **[Functions](/docs/commands/functions)**: building functions, tags, and command helpers.

## Full documentation index

### Core Guides

- [Configuration](/docs/guides/configuration) - JSON formatting and generation options.

### Commands

- [Macros](/docs/commands/macros) - dynamic command arguments for reusable functions.
### Concepts

- [Components](/docs/concepts/components) - item/component builders and custom components.
- [Chat Components](/docs/concepts/chat-components) - formatted messages and text components.
- [Colors](/docs/concepts/colors) - chat colors and formatting options.
- [Scoreboards](/docs/concepts/scoreboards) - objectives, teams, and scoreboard displays.

### Data-Driven

- [Predicates](/docs/data-driven/predicates) - reusable conditions used by loot tables, advancements and item modifiers.
- [Loot Tables](/docs/data-driven/loot-tables) & [Item Modifiers](/docs/data-driven/item-modifiers) - tables, pools and
  `/item modify` helpers.
- [Recipes](/docs/data-driven/recipes) & [Advancements](/docs/data-driven/advancements) - crafting, rewards and integration.
- [Enchantments](/docs/data-driven/enchantments) - custom enchantment definitions.
- [Dialogs](/docs/data-driven/dialogs) - NPC dialog systems.
- [Worldgen](/docs/data-driven/worldgen) - biomes, features and dimension examples.
- [Tags](/docs/data-driven/tags) - custom tag definitions for grouping items, blocks, entities, etc.

### Helpers

- [Helpers Utilities](/docs/helpers/utilities) - overview of helper-focused utilities extracted from the OOP module.
- [Display Entities](/docs/helpers/display-entities) - text, block, and item displays.
- [Inventory Manager](/docs/helpers/inventory-manager) - inventory manipulation helpers.
- [Mannequins](/docs/helpers/mannequins) - armor stand helpers.
- [ANSI Renderer](/docs/helpers/ansi-renderer) - ANSI escape codes to text components.
- [Area](/docs/helpers/area) - 3D bounding box geometry.
- [Markdown Renderer](/docs/helpers/markdown-renderer) - Markdown to text components.
- [MiniMessage Renderer](/docs/helpers/minimessage-renderer) - Adventure MiniMessage to text components.
- [Raycasts](/docs/helpers/raycasts) - recursive step-based raycasting.
- [Scheduler](/docs/helpers/scheduler) - delayed function execution patterns.
- [Scoreboard Math](/docs/helpers/scoreboard-math) - trigonometry and algebra via scoreboards.
- [State Delegates](/docs/helpers/state-delegates) - Kotlin property delegates for scoreboards/storage.
- [VFX Particles](/docs/helpers/vfx-particles) - geometric particle shapes.

### OOP

- [OOP Utilities](/docs/oop/utilities) - overview of all OOP module features.
- [Entities & Players](/docs/oop/entities-and-players) - entity/player management, commands, and effects.
- [Teams](/docs/oop/teams) - object-oriented team management.
- [Scoreboards](/docs/oop/scoreboards) - objective and score operations.
- [Items](/docs/oop/items) - item creation and spawning.
- [Events](/docs/oop/events) - advancement-based event system.
- [Cooldowns](/docs/oop/cooldowns) - scoreboard-based cooldowns.
- [Boss Bars](/docs/oop/boss-bars) - boss bar management.
- [Timers](/docs/oop/timers) - scoreboard-based timers with optional boss bar.
- [Spawners](/docs/oop/spawners) - reusable entity spawner handles.
- [Game State Machine](/docs/oop/game-state-machine) - scoreboard-based state machine.

### Advanced

- [Bindings](/docs/advanced/bindings) - import existing datapacks and generate Kotlin bindings (experimental).
- [GitHub Actions Publishing](/docs/advanced/github-actions-publishing) - automate datapack publishing.
- [Test Features (GameTest)](/docs/advanced/test-features) - testing datapacks with GameTest.
- [Known Issues](/docs/advanced/known-issues) - workarounds and limitations.

## Short tips

- Keep builders small and reusable; prefer extracting predicates and modifiers.
- Enable `prettyPrint` in [`Configuration`](/docs/guides/configuration) during development for readable JSON.
- Use [`Components`](/docs/concepts/components) + [`Predicates`](/docs/data-driven/predicates) together for robust item checks and inventory
  management.

## Known issues

Check out the [Known Issues](/docs/advanced/known-issues) page for a list of known issues and workarounds.

## Community & source

- **Repository**: [Kore](https://github.com/Ayfri/Kore)
- **Starter template**: [Kore Template](https://github.com/Kore-Minecraft/Kore-Template)
- **LLM-friendly documentation**: [llms.txt](https://kore.ayfri.com/llms.txt) | [llms-full.txt](https://kore.ayfri.com/llms-full.txt)

For hands-on examples, follow the doc pages above - most pages include runnable snippets and links to test cases in the repository.
