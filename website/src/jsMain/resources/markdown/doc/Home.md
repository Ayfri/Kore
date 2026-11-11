---
root: .components.layouts.MarkdownLayout
title: Type-Safe Minecraft Datapack Generator in Kotlin
nav-title: Introduction
description: Kore is a Kotlin DSL that generates Minecraft Java Edition datapacks, so you write type-safe code instead of JSON and mcfunction by hand.
keywords: minecraft datapack generator, datapack maker, minecraft data pack creator, kotlin datapack, kore, minecraft datapack dsl, datapack development, minecraft java edition, mcfunction generator, datapack library
date-created: 2024-04-06
date-modified: 2026-11-11
routeOverride: /docs/home
---

# Kore Documentation

Kore is a Kotlin library for building Minecraft datapacks with a concise, type-safe Kotlin DSL. It focuses on readable builders, stable
generation of datapack JSON, and tight integration with vanilla concepts (functions, loot tables, predicates, worldgen, ...).

## Start here

- **New to Kore?** Follow the [Getting Started](/docs/getting-started) guide, a step-by-step introduction to creating your first datapack.
- **Already writing datapacks by hand?** Read [Coming from Datapacks](/docs/guides/from-datapacks-to-kore), and [Why Kore](/docs/guides/why-kore) for the comparison.
- **Want to try it first?** The [playground](/playground) runs Kore in your browser, with nothing to install.
- **Looking for something to copy?** Browse the [Cookbook](/docs/guides/cookbook) for practical patterns you can reuse.

Prerequisites: Java {{java.version}} (JDK {{java.version}}) and a Kotlin 2.4+ build environment. For a ready-to-run project, use the
[Kore Template](https://github.com/Kore-Minecraft/Kore-Template).

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

## Browse by topic

{{{ .components.doc.FeatureGrid }}}

The sidebar search (Ctrl K) finds any page, and the [Features](/features) page lists everything Kore covers with in-game previews.

## Modules

Kore is split into installable modules: start with `kore`, then add the others depending on the abstractions or tooling you need.

| Module     | What it adds                                                                                                                         | Artifact                                       | Docs                                         |
|------------|--------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------|----------------------------------------------|
| `kore`     | The core DSL to build datapacks.                                                                                                     | `io.github.ayfri.kore:kore:{{kore.release}}`     | [Getting Started](/docs/getting-started)     |
| `oop`      | Higher-level abstractions for boss bars, cooldowns, entities, game states, scoreboards, spawners, teams, and timers.                 | `io.github.ayfri.kore:oop:{{kore.release}}`      | [OOP Module](/docs/oop/oop-utilities)     |
| `helpers`  | Renderers, raycasts, scheduler utilities, scoreboard math, state delegates, particle helpers, and related utilities.                 | `io.github.ayfri.kore:helpers:{{kore.release}}`  | [Helpers Module](/docs/helpers/utilities) |
| `bindings` | Importer of existing datapacks that generates type-safe Kotlin bindings for their functions, resources, and tags.                    | `io.github.ayfri.kore:bindings:{{kore.release}}` | [Importing Datapacks](/docs/advanced/bindings)          |

All four modules are **Kotlin Multiplatform** (JVM + JS): file/ZIP/JAR generation works on the JVM and Node.js, while browser JS consumers
build the DSL and call `exportAsStrings()` / `generateZipBytes()` instead. See [Multiplatform Support](/docs/advanced/multiplatform) for
what runs where.

`{{kore.release}}` targets Minecraft {{mc.version}}: the [version matrix](/docs/guides/version-support#version-matrix) lists the release for
each Minecraft version, and the [snapshot builds](/docs/getting-started#snapshot-builds-latest-unreleased-changes) section of Getting Started
shows how to use the latest unreleased changes.

## Tooling

- **Kore Assistant**: gutter icons, hovers, and declaration navigation for the Kore DSL in your editor, for
  [IntelliJ IDEA](https://plugins.jetbrains.com/plugin/27025-kore-assistant) and
  [VS Code](https://marketplace.visualstudio.com/items?itemName=ayfri.kore-assistant).
- **Gradle plugin**: generates the datapack, copies it into your worlds and reloads a running server, see
  [Gradle Plugin](/docs/guides/gradle-plugin).
- **Starter template**: [Kore Template](https://github.com/Kore-Minecraft/Kore-Template), a ready-to-run project.
- **LLM-friendly documentation**: [llms.txt](https://kore.ayfri.com/llms.txt) | [llms-full.txt](https://kore.ayfri.com/llms-full.txt) |
  every page also has a plain Markdown copy at its URL with `.md` appended, such as
  [`/docs/getting-started.md`](https://kore.ayfri.com/docs/getting-started.md)
- **AI Agents skills**: [Kore-Skill](https://github.com/Kore-Minecraft/Kore-Skill) (optional skills pack for AI-assisted
  Kore work)

## Help and community

- **Discord**: join the [Kore Discord](https://discord.ayfri.com) for questions and to chat with other datapack devs.
- **Issues & bugs**: report them on the [GitHub repository](https://github.com/Ayfri/Kore/issues).
- **Known workarounds**: check [Known Issues](/docs/advanced/known-issues) before filing a bug.
- **Read first**: [Kore Hello World](https://ayfri.com/articles/kore-hello-world/), a hands-on intro article.

To contribute to Kore itself, start with [Contributing to Kore](/docs/contributing/contributing), the hub for architecture, workflow, and
maintainer docs.

### Built with Kore

Real projects built with Kore, useful to see the DSL used at full project scale:

- [SimplEnergyKore](https://github.com/e-psi-lon/SimplEnergyKore)
- [Kore-Bindings](https://github.com/e-psi-lon/Kore-Bindings)
- [OreCrops](https://github.com/e-psi-lon/OreCrops)
- [realms-map](https://github.com/Aeltumn/realms-maps)
- [jumpr](https://github.com/LluisJM/jumpr)

To add your project to the list, open an issue on the
[repository](https://github.com/Ayfri/Kore/issues) or ping me on [Discord](https://discord.ayfri.com).
