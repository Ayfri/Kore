---
root: .components.layouts.MarkdownLayout
title: Kore - Type-Safe Minecraft Datapack Generator
nav-title: Home
description: Kore is a Kotlin DSL datapack generator for Minecraft Java Edition. Create datapacks with type-safe code instead of writing JSON and MCFunction by hand. Open-source and production-ready.
keywords: minecraft datapack generator, datapack maker, minecraft data pack creator, kotlin datapack, kore, minecraft datapack dsl, datapack development, minecraft java edition, mcfunction generator, datapack library
date-created: 2024-04-06
date-modified: 2026-08-05
routeOverride: /docs/home
position: 0
---

# Kore

**Welcome to the Kore wiki!**

Kore is a Kotlin library for building Minecraft datapacks with a concise, type-safe Kotlin DSL. It focuses on readable builders, stable
generation of datapack JSON, and tight integration with vanilla concepts (functions, loot tables, predicates, worldgen, ...). Coming from
raw datapacks? Read [Why Kore](/docs/guides/why-kore) or the migration-focused [From Datapacks to Kore](/docs/guides/from-datapacks-to-kore).

## Quick start

- **Getting started**: Check out the [Getting Started](/docs/getting-started) guide for a step-by-step introduction to creating your first datapack.
- **Prerequisites**: Java 25 (JDK 25) and a Kotlin 2.4+ build environment.
- **Starter template**: use the `Kore Template` for a ready-to-run project: [
  `Kore Template`](https://github.com/Kore-Minecraft/Kore-Template).
- **Build faster**: browse the [Cookbook](/docs/guides/cookbook) for practical patterns you can reuse.

### Minimal example

```kotlin
fun main() {
	val datapack = dataPack("example") {
		function("display_text") {
			tellraw(allPlayers(), textComponent("Hello World!"))
		}
	}

	datapack.generateZip()
}
```

### IDE tooling

Kore Assistant brings gutter icons, hovers, and declaration navigation for the Kore DSL straight into your editor:
[IntelliJ IDEA](https://plugins.jetbrains.com/plugin/27025-kore-assistant) |
[VS Code](https://marketplace.visualstudio.com/items?itemName=ayfri.kore-assistant).

## Jump to a topic

{{{ .components.doc.FeatureGrid }}}

Looking for something more specific? The full page list, with search, lives in the sidebar on the left.

## Installable modules

Kore is split into installable modules. Start with `kore`, then add the others depending on the abstractions or tooling
you need.

All four modules are **Kotlin Multiplatform** (JVM + JS): the same coordinates publish JVM, JS, and common metadata
variants. File/ZIP/JAR generation works on the JVM and Node.js; browser JS consumers build the DSL and call
`exportAsStrings()` / `generateZipBytes()` instead. See [Multiplatform Support](/docs/advanced/multiplatform) for
what runs where.

### `kore` - Core DSL

- Build datapacks with the main Kore DSL.
- Artifact: `io.github.ayfri.kore:kore:VERSION`
- Snapshot builds from each commit on `master`: add `https://central.sonatype.com/repository/maven-snapshots/` and use
  `VERSION-SNAPSHOT`

### `oop` - Object-oriented gameplay utilities

- Add higher-level abstractions for boss bars, cooldowns, entities, game states, scoreboards, spawners, teams, and
  timers.
- Especially useful when several gameplay systems need to exchange data cleanly, such as syncing a `Team` with a boss
  bar or reusing an `Entity` handle across scoreboards and commands.
- Artifact: `io.github.ayfri.kore:oop:VERSION`
- Explore: [OOP Utilities](/docs/oop/oop-utilities)

### `helpers` - Utility-focused helpers

- Add renderers, raycasts, scheduler utilities, scoreboard math, state delegates, particle helpers, and related
  utilities.
- These helpers complement the core DSL well for advanced text pipelines, reusable state access, geometric particles, or
  command-heavy math routines.
- Artifact: `io.github.ayfri.kore:helpers:VERSION`
- Explore: [Helpers Utilities](/docs/helpers/utilities)

### `bindings` - Datapack importer

- Import existing datapacks and generate type-safe Kotlin bindings for their functions, resources, and tags.
- Artifact: `io.github.ayfri.kore:bindings:VERSION`
- Explore: [Bindings](/docs/advanced/bindings)

## Version Matrix

One Kore version per stable Minecraft version, the latest tagged pre-release as a separate `Snapshot` row, and the
latest continuous `-SNAPSHOT` build from `master` as `Maven Snapshot`. Gradle coordinates link straight to Maven
Central. Generated from GitHub releases and the current project version on every website build, so it always
reflects the latest published version without manual edits.

{{{ .components.doc.VersionMatrix }}}

## Contributing to Kore

If you want to contribute to Kore itself, start with [Contributing to Kore](/docs/contributing/contributing), the hub
for architecture, workflow, issue/PR, and maintainer docs.

Useful contributor-facing internals:

- [Architecture and Patterns](/docs/contributing/architecture-and-patterns) - module boundaries and recurring
  implementation patterns.
- [Arguments Internals](/docs/concepts/arguments) - how the typed argument layer, resource wrappers, and literals fit
  together.

## Short tips

- Keep builders small and reusable; prefer extracting predicates and modifiers.
- Enable `prettyPrint` in [`Configuration`](/docs/guides/configuration) during development for readable JSON.
- Reach for [`OOP Utilities`](/docs/oop/oop-utilities) when multiple gameplay features should share the same handles
  instead of re-building selectors and score names manually.
- Use [`Components`](/docs/concepts/components) + [`Predicates`](/docs/data-driven/predicates) together for robust item checks and inventory
  management.
- Reach for [`NBTs`](/docs/concepts/nbts) when you need to build a payload once and reuse it across commands, chat, or
  predicates.
- Use [`Helpers Utilities`](/docs/helpers/utilities) to avoid reimplementing common glue code such as renderers,
  scheduler patterns, raycasts, or scoreboard-based maths.
- Hit a wall? Check [Known Issues](/docs/advanced/known-issues) for known workarounds before filing a bug.

## Need help?

- **Discord**: join the [Kore Discord](https://discord.ayfri.com) for questions and to chat with other datapack devs.
- **Issues & bugs**: report them on the [GitHub repository](https://github.com/Ayfri/Kore/issues).
- **Stuck on setup?**: the [Getting Started](/docs/getting-started) guide has a dedicated Troubleshooting section.
- **Read first**: [Kore Hello World](https://ayfri.com/articles/kore-hello-world/), a hands-on intro article.

## Community & source

- **Repository**: [Kore](https://github.com/Ayfri/Kore)
- **Starter template**: [Kore Template](https://github.com/Kore-Minecraft/Kore-Template)
- **LLM-friendly documentation**: [llms.txt](https://kore.ayfri.com/llms.txt) | [llms-full.txt](https://kore.ayfri.com/llms-full.txt)
- **AI Agents skills**: [Kore-Skill](https://github.com/Kore-Minecraft/Kore-Skill) (optional skills pack for AI-assisted
  Kore work)

For hands-on examples, follow the doc pages above - most pages include runnable snippets and links to test cases in the repository.
