---
root: .components.layouts.MarkdownLayout
title: Why Kore - Kotlin DSL vs Raw Datapacks & Other Generators
nav-title: Why Kore
description: Compare Kore with raw datapacks, Sandstone, Beet, and other generators. Type safety, autocomplete, one Kotlin language for all JSON and MCFunction. No more error-prone hand-written datapack files.
keywords: kore vs sandstone, kore vs beet, datapack generator comparison, kotlin datapack dsl, minecraft datapack alternative, type-safe datapack, why kore, datapack generator benefits, mcfunction generator, minecraft json generator
date-created: 2026-06-24
date-modified: 2026-07-02
routeOverride: /docs/guides/why-kore
position: 0
---

# Why Kore

Datapacks are powerful but painful to write by hand: stringly-typed commands, sprawling JSON, no autocomplete, and
silent
failures you only discover after `/reload`. Kore replaces all of that with a single, strongly-typed Kotlin DSL that
generates the exact same vanilla output - just faster, safer, and without the boilerplate.

This page explains what Kore gives you over raw datapacks, and how it compares to other generators.

## The problem with hand-written datapacks

A real datapack is a mix of two error-prone formats:

- **`.mcfunction`** - plain text commands with no validation. A typo in a selector, a wrong block id, or a misremembered
  argument order fails silently or breaks at runtime.
- **JSON** - loot tables, predicates, advancements, recipes, worldgen, dialogs. Deeply nested, verbose, and easy to get
  subtly wrong. No editor tells you a field is misspelled until the game refuses to load it.

There is no autocomplete, no type checking, no refactoring, and no way to share logic except copy-paste. As a pack
grows,
this gets worse fast.

## What Kore changes

### One language, no JSON

You write Kotlin. Kore generates the `.mcfunction` and JSON for you. You never hand-write a brace.

```kotlin
dataPack("example") {
	function("hello") {
		tellraw(allPlayers(), textComponent("Hello World!"))
	}
}.generateZip()
```

### Everything is typed

Every vanilla list - blocks, items, entities, effects, enchantments, biomes, sounds - is a generated enum. You cannot
misspell `minecraft:diamond_sword`, because you write `Items.DIAMOND_SWORD` and the compiler checks it. The same applies
to command arguments, NBT paths, and data-driven schemas. See [Arguments](/docs/concepts/arguments).

### Real code structure

Because it is Kotlin, you get everything the language offers: extension functions to split a pack across files, loops
and
conditionals to generate repetitive content at build time (see [Runtime Logic](/docs/concepts/runtime-logic)), and
refactoring/autocomplete from your IDE. Big projects stay maintainable.

### Tested, predictable output

Kore is a **build-time generator**. There is no runtime, no plugin, no server mod. The output is exactly the vanilla
datapack you would have written by hand - every public feature ships with tests asserting the exact generated JSON and
command strings. What you generate is what loads.

### Up to date

Kore tracks recent Minecraft versions closely, including newer systems like command macros, dialogs, timelines, and the
current pack format. You get modern features with type safety on day one.

## How Kore compares to other generators

Kore is not the only datapack generator. Here is an honest, broad-strokes comparison. (Verify specifics against each
project before quoting - tooling moves fast.)

| Tool                   | Language          | Style                         | Type safety                        | Vanilla lists as symbols                    |
|------------------------|-------------------|-------------------------------|------------------------------------|---------------------------------------------|
| **Kore**               | Kotlin            | Type-safe builder DSL         | Strong (no `any`, generated enums) | Yes (`Items.DIAMOND_SWORD`, `Blocks.STONE`) |
| **Sandstone**          | TypeScript        | Functional TS API             | Good (TS strict)                   | Partial                                     |
| **Beet** (+ **mecha**) | Python            | Plugin / pipeline, decorators | Medium (type hints)                | Via libraries                               |
| **Raw datapack**       | mcfunction + JSON | Hand-written                  | None                               | No (plain strings)                          |

**Where Kore wins:** a single strongly-typed Kotlin DSL with generated registries, so vanilla ids are real symbols the
compiler checks, plus exact-output tests shipped with every feature. The DSL mirrors Minecraft's own structure, so it
reads naturally to anyone who knows datapacks.

**Where others may fit better:** Beet has a very mature plugin ecosystem and `mecha`'s command compiler, and is a good
fit
if your team lives in Python. Sandstone is a strong choice for TypeScript-first teams. If you only need a handful of
commands and never plan to scale, raw datapacks are fine.

Kore's bet is that for anything beyond trivial, a typed compiler-checked DSL saves more time than it costs to learn.

## When to choose Kore

Reach for Kore when:

- Your pack is more than a few functions, or you expect it to grow.
- You want autocomplete, refactoring, and the compiler catching mistakes before `/reload`.
- You already know Kotlin (or are happy to learn a small subset - see [Getting Started](/docs/getting-started)).
- You want data-driven content (loot, recipes, advancements, worldgen, dialogs) without writing JSON.

## Next steps

- [Getting Started](/docs/getting-started) - build your first datapack
- [Runtime Logic](/docs/concepts/runtime-logic) - the one concept that makes everything click
- [Creating a Datapack](/docs/guides/creating-a-datapack) - lifecycle and output options
- [Cookbook](/docs/guides/cookbook) - practical patterns to copy
