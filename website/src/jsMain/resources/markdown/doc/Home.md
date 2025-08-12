---
root: .components.layouts.MarkdownLayout
title: Home
nav-title: Home
description: Welcome to the Kore wiki!
keywords: minecraft, datapack, kore, guide
date-created: 2024-04-06
date-modified: 2025-08-12
routeOverride: /docs/home
position: 0
---

# Kore

**Welcome to the Kore wiki!**

Kore is a Kotlin library for building Minecraft datapacks with a concise, type-safe Kotlin DSL. It focuses on readable builders, stable
generation of datapack JSON, and tight integration with vanilla concepts (functions, loot tables, predicates, worldgen, ...).

## Quick start

- **Prerequisites**: Java 21+ and a Kotlin-capable build environment.
- **Starter template**: use the `Kore Template` for a ready-to-run project: [`Kore Template`](https://github.com/Ayfri/Kore-Template).
- **Create & generate**: see [Creating A Datapack](./creating-a-datapack) for lifecycle and output options (`.generate()`, `.generateZip()`, `.generateJar()`).



{{{ .components.doc.FeatureGrid }}}

### Minimal example

```kotlin
fun main() {
    dataPack("example") {
        function("display_text") { tellraw(allPlayers(), textComponent("Hello World!")) }
    }.generateZip()
}
```

## Recommended reading (next steps)

- **[Creating A Datapack](./creating-a-datapack)**: lifecycle, output paths, merging and mod‑loader jars.
- **[Functions](./functions)**: building functions, tags, generated scopes and command helpers.
- **[Configuration](./configuration)**: JSON formatting and generation options.
- **[Components](./components)**: item/component builders and custom components.
- **[Predicates](./predicates)**: reusable conditions used by loot tables, advancements and item modifiers.
- **[Worldgen](./worldgen)**: biomes, features and dimension examples.
- **[Loot Tables](./loot-tables)** & **[Item Modifiers](./item-modifiers)**: tables, pools and `/item modify` helpers.
- **[Recipes](./recipes)** & **[Advancements](./advancements)**: crafting, rewards and integration.
- **[Chat Components](./chat-components)** & **[Colors](./colors)**: formatted messages, chat colors and text components.

## Short tips

- Keep builders small and reusable; prefer extracting predicates and modifiers.
- Enable `prettyPrint` in [`Configuration`](./configuration) during development for readable JSON.
- Use [`Components`](./components) + [`Predicates`](./predicates) together for robust item checks and inventory management.

## Community & source

- **Repository**: [Kore](https://github.com/Ayfri/Kore)
- **Starter template**: [Kore Template](https://github.com/Ayfri/Kore-Template)

For hands-on examples, follow the doc pages above — most pages include runnable snippets and links to test cases in the repository.
