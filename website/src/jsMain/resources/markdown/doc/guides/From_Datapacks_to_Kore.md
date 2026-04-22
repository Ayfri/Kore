---
root: .components.layouts.MarkdownLayout
title: From Datapacks to Kore
nav-title: Datapack Veterans
description: Deep technical guide for experienced datapack authors adopting Kore and Kotlin in production.
keywords: minecraft, datapack, kore, kotlin, advanced, migration, architecture, production
date-created: 2026-04-22
date-modified: 2026-04-22
routeOverride: /docs/guides/from-datapacks-to-kore
---

# From Datapacks to Kore (Advanced)

This page is for datapack authors who already ship real projects and want to adopt Kore without losing low-level
control.
You already understand vanilla systems, resource formats, and function/tag architecture. The goal here is to map that
experience to Kore's model, explain the boundaries clearly, and provide practical migration patterns with
production-style Kotlin.

## Who this guide is for

You are the target audience if you already:

- Structure large datapacks with folders, tags, and shared utility functions.
- Work with scoreboards, predicates, loot tables, recipes, and worldgen.
- Care about maintainability, testability, and deterministic generation.
- Want type safety and refactors without losing low-level control.

If you are new to datapacks, start with [Getting Started](/docs/getting-started) first.

## Why Kotlin (for datapack veterans)

Kotlin does not replace Minecraft logic. It replaces fragile authoring workflows.
Minecraft still runs generated `.mcfunction` and JSON. Kotlin gives you a safer, composable way to produce them.

The practical gains for experienced datapack teams are:

- **Typed APIs over stringly-typed commands**: fewer invalid IDs, selectors, and argument combinations.
- **Refactorability**: IDE rename/find-usages works across your gameplay code.
- **Composition**: extract reusable builders instead of copy-pasting command blocks.
- **Expressive abstraction**: extensions and data classes let you encode conventions once and reuse everywhere.
- **Deterministic output**: generation from code makes large packs easier to audit and reproduce.

If you want a quick baseline first, read [Creating A Datapack](/docs/guides/creating-a-datapack), then come back here.

## Kore mental model in one sentence

Kore is a **compile-time authoring DSL and generator**, not an in-game runtime framework.

That distinction matters:

- Kore code runs on your machine (or CI) during generation.
- Generated datapack files run in Minecraft.
- Kotlin objects and functions do not exist in-game after generation.

Think of Kore as a programmable build system for datapack content.

## What Kore gives you

At a high level, Kore ships as four installable modules:

- `kore`: core DSL for functions, commands, and data-driven resources.
- `oop`: higher-level gameplay utilities (entities, teams, timers, scoreboards, events, and more).
- `helpers`: utility layer (renderers, math, raycasts, delegates, visual helpers, scheduler patterns).
- `bindings`: importer that generates Kotlin bindings from external datapacks (**experimental**).

Use `kore` as your baseline, then add modules only when you need their abstractions.
For the canonical module overview, see [Home](/docs/home).

## What Kore does not give you (or not yet)

Important boundaries and limitations:

- **Not a resource pack tool**: Kore currently targets datapacks, not resource packs.
- **Some SNBT gaps**: heterogeneous SNBT lists and SNBT operations like `bool(arg)`/`uuid(arg)` are not fully supported
  yet.
- **No magical runtime optimization**: Kore improves authoring quality; Minecraft execution cost is still defined by
  your generated logic.
- **Bindings stability caveat**: `bindings` is explicitly experimental and may evolve quickly.

For known caveats, see [Known Issues](/docs/advanced/known-issues).

## How Kore works under the hood (practical pipeline)

A practical pipeline view:

1. You describe datapack content in Kotlin builders.
2. Kore builds an in-memory model of functions/resources.
3. Kore serializes this model to `.mcfunction` and JSON files.
4. It writes output via `.generate()`, `.generateZip()`, or `.generateJar()`.

Key implications:

- Generation is deterministic from your Kotlin source and config.
- You can inspect generated output at any time.
- CI can regenerate and diff output to enforce consistency.

Output targets and packaging strategy are explained in detail
in [Creating A Datapack](/docs/guides/creating-a-datapack).

## Vanilla-to-Kore mapping

If your current pack is hand-written, this is the direct conceptual mapping:

- `data/<ns>/function/...` -> `fun Function.someFeature() = function("feature/some_feature") { ... }`
- `minecraft:load` tag editing -> `load("...") { function(someFeature()) }`
- `minecraft:tick` tag editing -> `tick("...") { function(runtimeStep()) }`
- JSON resources -> dedicated typed builders (`advancement`, `lootTable`, `recipe`, `predicate`, `enchantment`,
  `worldgen`, ...).

Kore does not hide vanilla concepts. It formalizes them.

## A realistic project shape

A scalable source layout for Kore projects:

- `pack/` for pack bootstrap and configuration.
- `feature/` for domain modules (combat, quests, economy, UI, progression).
- `runtime/` for lifecycle and tick routing.
- `resources/` for data-driven definitions.
- `interop/` for imported/bound external packs.

This keeps gameplay code readable while preserving direct datapack semantics in generated output.

Example:

```kotlin
data object Objectives {
	const val LIVES = "lives"
	const val ROUND = "round"
}

fun Function.combatInit() = function("feature/combat/init") {
	tellraw(allPlayers(), textComponent("[combat] initialized"))
}

fun Function.combatTick() = function("feature/combat/tick") {
	// Keep tick work small and dispatch if it grows.
}

fun Function.progressionTick() = function("feature/progression/tick") {
	// Keep progression routing isolated from combat.
}

fun DataPack.registerLifecycle() {
	load("system/bootstrap") {
		scoreboard.objectives.add(Objectives.LIVES, "dummy")
		scoreboard.objectives.add(Objectives.ROUND, "dummy")
		function(combatInit())
	}

	tick("runtime/main") {
		function(combatTick())
		function(progressionTick())
	}
}

fun main() {
	dataPack("arena_core") {
		registerLifecycle()
	}.generate()
}
```

For deeper lifecycle docs, see [Functions](/docs/commands/functions).

## Kotlin patterns that actually pay off in Kore

### 1) Type-safe function references first (recommended)

Prefer function factories over string paths when wiring lifecycle hooks. You get refactors, find usages, and
compile-time
safety on every caller:

```kotlin
fun Function.welcomeAnnounce() = function("feature/welcome/announce") {
	tellraw(allPlayers(), textComponent("Welcome to the server"))
}

fun Function.joinEffects() = function("feature/welcome/join_effects") {
	effect.give(allPlayers(), Effects.RESISTANCE, 3, 0)
	say("Join effects applied")
}

fun DataPack.registerJoinFlow() {
	load("system/join_bootstrap") {
		function(welcomeAnnounce())
		function(joinEffects())
	}
}
```

### 2) Extension-based feature modules

Use `DataPack` extensions for registration, and `Function` extensions for reusable command snippets.

```kotlin
fun Function.combatPipeline() = function("feature/combat/pipeline") {
	applyJoinEffects()
	runRoundRules()
}

fun Function.applyJoinEffects() {
	effect.give(allPlayers(), Effects.RESISTANCE, 3, 0)
}

fun Function.runRoundRules() {
	say("Round rules applied")
}

fun DataPack.registerCombatPipeline() {
	tick("runtime/combat_router") {
		function(combatPipeline())
	}
}
```

### 3) Typed selectors as reusable domain rules

Avoid rewriting long selector constraints inline.
Keep domain intent in one selector value and reuse it across systems.

```kotlin
val activePlayers = allPlayers {
	gamemode = !Gamemode.SPECTATOR
	scores = scores {
		"lives" greaterThan 0
		"round" greaterThanOrEqualTo 1
	}
}

fun DataPack.registerRoundMessaging() {
	function("feature/round/status") {
		tellraw(activePlayers, textComponent("Round running"))
	}
}
```

See [Selectors](/docs/concepts/selectors) and [Scoreboards](/docs/concepts/scoreboards) for full syntax.

### 4) Data classes for repeatable feature config

When you duplicate numeric tuning values, move them into typed configs.

```kotlin
data class WaveConfig(
	val id: String,
	val title: String,
	val warningSeconds: Int,
)

fun DataPack.registerWave(config: WaveConfig) {
	val warning = function("feature/waves/${config.id}_warning") {
		tellraw(allPlayers(), textComponent(config.title))
	}

	function("feature/waves/${config.id}_start") {
		function(warning)
		schedule.function(warning, config.warningSeconds.seconds)
	}
}
```

This keeps balancing changes local and reviewable.

## Advanced example: command + data-driven feature in one flow

A common production pattern is to pair a command function with a typed data resource and lifecycle wiring.

```kotlin
fun DataPack.registerArenaBlade() {
	val arenaBlade = Items.DIAMOND_SWORD {
		customName(textComponent("Arena Blade", Color.AQUA))
		tooltipDisplay(showInTooltip = true)
	}

	val arenaBladePredicate = predicate("arena_blade") {
		matchTool(arenaBlade)
	}

	fun Function.checkArenaBlade() = function("feature/items/check_arena_blade") {
		execute {
			ifCondition(arenaBladePredicate)
			run {
				tellraw(allPlayers(), textComponent("Arena Blade detected"))
			}
		}
	}

	load("system/items_bootstrap") {
		function(checkArenaBlade())
	}
}
```

Reference pages used in that pattern:

- [Components](/docs/concepts/components)
- [Predicates](/docs/data-driven/predicates)
- [Functions](/docs/commands/functions)

## Migration strategy for existing packs (incremental, no big-bang)

When you already have stable gameplay in vanilla datapack files, migrate slice-by-slice.

1. **Freeze behavior** with a smoke test checklist (`/reload`, bootstrap output, one command per feature).
2. **Port one vertical slice** (for example onboarding flow or one combat mechanic).
3. **Generate to folder** with `.generate()` and inspect the output.
4. **Compare runtime behavior** in-game against your baseline.
5. **Repeat by subsystem**, then standardize shared Kotlin helpers.

If your project depends on another datapack, import it with [Bindings](/docs/advanced/bindings) instead of string
literals.

## Minecraft parity checkpoints (official docs)

Use these vanilla checkpoints when validating generated output. They match what Kore emits and help avoid stale
assumptions:

- **`pack.mcmeta` format evolution**: modern packs use `min_format`/`max_format`, with compatibility behavior for older
  formats. See [Minecraft Wiki - pack.mcmeta](https://minecraft.wiki/w/Pack.mcmeta) and
  [Minecraft Wiki - pack format](https://minecraft.wiki/w/Pack_format).
- **Datapack root + namespace rules**: generated folder/zip output should keep vanilla root conventions.
  See [Minecraft Wiki - data pack](https://minecraft.wiki/w/Data_pack).
- **Lifecycle tags**: `load` and `tick` are still function-tag wiring under the hood. Kore gives you typed composition,
  but
  the runtime behavior remains vanilla tag dispatch.
- **Scheduling semantics**: `schedule function ...` behavior is still Minecraft-native. Kore only improves authoring
  ergonomics around it.

When something looks surprising in-game, inspect generated files and compare with these references first.

## Interop with existing datapacks via `bindings`

This is useful when your pack relies on internal shared packs or third-party resources.

```kotlin
import io.github.ayfri.kore.bindings.api.importDatapacks

importDatapacks {
	configuration {
		outputPath("src/main/kotlin")
		packagePrefix = "kore.dependencies"
	}

	github("pixigeko.minecraft-default-data:1.21.11") {
		subPath = "data"
	}
}
```

Then consume generated constants in your own functions instead of hand-written IDs.
See the full flow in [Bindings](/docs/advanced/bindings).

## Choosing between `kore`, `helpers`, and `oop`

Use this rule of thumb:

- Stay on `kore` when the logic is still easy to reason about with plain builders.
- Add `helpers` when you repeat infrastructure glue (rendering, scheduler, math, state delegates, raycasts).
- Add `oop` when systems need stable gameplay objects and cross-system coordination (teams, timers, entities, state
  machines).

Related deep dives:

- [Helpers Utilities](/docs/helpers/utilities)
- [OOP Utilities](/docs/oop/oop-utilities)
- [Dynamic Strings](/docs/oop/dynamic-strings)

## Recent Kore features worth using in migration projects

If your mental model is still from older Kore versions, these are high-impact upgrades:

- **Pack metadata parity for modern Minecraft**: `minFormat`/`maxFormat`, overlays, and legacy compatibility handling
  are
  available directly in the pack DSL.
- **Interop at scale with `bindings`**: import external datapacks and consume generated Kotlin constants instead of
  hand-maintained IDs.
- **Dynamic Strings (`oop`)**: a macro-backed string toolkit over storage/NBT for advanced runtime text pipelines
  (substring, split, replace, case conversion, trim/pad, lists).
- **Multiple output targets in the same workflow**: keep `.generate()` for review and CI diffs, then switch to
  `.generateZip()`/`.generateJar()` for releases.

## Verification workflow for advanced teams

A robust dev/release loop:

1. Generate unpacked output with `.generate()` for reviewability.
2. Run focused smoke checks in-game (`/reload`, lifecycle hooks, one key command per feature).
3. Check generated tags/resources for namespace and path correctness.
4. Package releases with `.generateZip()` or `.generateJar()` only after behavior checks.
5. Keep generated outputs deterministic so CI and code review can catch regressions early.

For packaging details (zip/jar/merge), see [Creating A Datapack](/docs/guides/creating-a-datapack).

## Common migration mistakes (and fixes)

- **Mistake**: Porting file-by-file instead of behavior-by-behavior.  
  **Fix**: Migrate vertical slices and validate each runtime path before moving on.
- **Mistake**: Building giant wrappers too early.  
  **Fix**: Start with direct DSL usage and extract only repeated patterns.
- **Mistake**: Ignoring naming conventions in generated paths.  
  **Fix**: Adopt stable prefixes (`feature/`, `runtime/`, `system/`) from day one.
- **Mistake**: Treating Kotlin as runtime state.  
  **Fix**: Remember Kotlin runs at generation time only.
- **Mistake**: Keeping critical IDs as ad-hoc strings everywhere.  
  **Fix**: Centralize objectives/resource IDs in constants and helper APIs.

## Detailed migration checklist

Before migration:

- Freeze current pack behavior (manual test matrix or GameTest strategy).
- Identify shared naming conventions and objective IDs.
- Decide your initial module set (`kore` only, or `kore` + `helpers`/`oop`).

First week:

- Port lifecycle (`load`, `tick`) and one gameplay feature.
- Introduce extension-based registration (`DataPack.registerX()`).
- Add one data-driven resource with typed builder for parity checks.

Stabilization:

- Add reusable selector/predicate helpers.
- Introduce typed config objects for balancing-heavy systems.
- Optional: integrate external resources through `bindings`.

## Should you adopt Kore?

Kore is an excellent fit if you want:

- Long-term maintainability for non-trivial datapacks.
- Safer refactors and shared abstractions.
- Team workflows with code review and generation checks.

Stay hand-written if your project is very small, short-lived, or intentionally one-off.

## Where to go next

- [Getting Started](/docs/getting-started) for a minimal end-to-end baseline.
- [Creating A Datapack](/docs/guides/creating-a-datapack) for output and generation details.
- [Functions](/docs/commands/functions) for function composition and lifecycle hooks.
- [Commands](/docs/commands/commands) for typed command usage.
- [Cookbook](/docs/guides/cookbook) for practical composition patterns.
- [OOP Utilities](/docs/oop/oop-utilities) for higher-level gameplay abstractions.
- [Helpers Utilities](/docs/helpers/utilities) for reusable utility patterns.
- [Bindings](/docs/advanced/bindings) for importing external datapacks.
- [Known Issues](/docs/advanced/known-issues) for current limitations.

