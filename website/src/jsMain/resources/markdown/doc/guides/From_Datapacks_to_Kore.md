---
root: .components.layouts.MarkdownLayout
title: Migrating a Minecraft Datapack to Kore - Guide for Datapack Veterans
nav-title: Datapack Veterans
description: Port an existing hand-written datapack to Kore without a rewrite - vanilla-to-Kotlin mapping, slice-by-slice migration, project structure and common mistakes.
keywords: datapack migration, migrate datapack to kotlin, mcfunction to kotlin, kore migration guide, datapack architecture, datapack refactor, advanced datapack development
date-created: 2026-04-22
date-modified: 2026-08-05
routeOverride: /docs/guides/from-datapacks-to-kore
position: 4
---

# From Datapacks to Kore

This page is for authors who **already ship a working datapack** and want to move it to Kore without losing low-level
control or freezing development for a rewrite.

You are the target audience if you already structure packs with folders, tags and shared utility functions, work
comfortably with scoreboards, predicates, loot tables and worldgen, and care about maintainability and deterministic
output.

New to datapacks? Start with [Getting Started](/docs/getting-started) instead. Still deciding whether to migrate at all?
[Why Kore](/docs/guides/why-kore) covers the trade-off, including when to stay hand-written.

## The one thing to internalize first

Kore is a **compile-time authoring DSL and generator**, not an in-game runtime framework.

- Your Kotlin runs on your machine or in CI, during generation.
- The generated `.mcfunction` and JSON files run in Minecraft.
- Kotlin objects and functions do **not** exist in-game afterwards.

Almost every early misunderstanding traces back to this line. A Kotlin `val` is not a scoreboard value; a Kotlin `if`
picks what gets *written into* the pack, not what the game evaluates at runtime. When you do want in-game conditionals
and variables, that is a separate mechanism - see [Runtime Logic](/docs/concepts/runtime-logic).

Think of Kore as a programmable build system for datapack content.

## Vanilla-to-Kore mapping

Direct translation of what you already have:

| Hand-written | Kore |
|--------------|------|
| `data/<ns>/function/feature/x.mcfunction` | `fun Function.x() = function("feature/x") { ... }` |
| editing the `minecraft:load` tag | `load("...") { function(someFeature()) }` |
| editing the `minecraft:tick` tag | `tick("...") { function(runtimeStep()) }` |
| `@a[tag=fighter,gamemode=!spectator]` | typed selector builders, or `selector("@a[tag=fighter,gamemode=!spectator]")` to parse the vanilla string as-is |
| JSON resource files | typed builders: `advancement`, `lootTable`, `recipe`, `predicate`, `enchantment`, `worldgen`, ... |
| folder nesting | the path string in the builder (`"feature/combat/init"`) |

Kore does not hide vanilla concepts. It formalizes them - the generated output is the same pack you would have written
by hand, so anything you know about load order, tag dispatch, or `execute` semantics still applies.

### What Kore will not do for you

Set expectations before you start:

- **Not a resource pack tool.** Datapacks only.
- **Some SNBT gaps.** Heterogeneous SNBT lists and operations like `bool(arg)`/`uuid(arg)` are not fully supported.
- **No runtime optimization.** Execution cost is still defined by the commands you generate.
- **`bindings` is experimental** and may change between versions.

See [Known Issues](/docs/advanced/known-issues) for the current list.

## Migrate incrementally, never big-bang

The mistake that kills migrations is porting the whole pack before testing any of it. Go slice by slice:

1. **Freeze behavior.** Write a smoke checklist first: `/reload`, expected bootstrap output, one command per feature.
   This is your regression baseline, and you cannot recover it later.
2. **Port one vertical slice** - a full feature end to end, not a folder. Onboarding flow, or one combat mechanic.
3. **Keep hostile details verbatim.** Paste complex selectors into `selector("@a[...]")` during this pass rather than
   retyping them into the builder; convert them once the slice is validated.
4. **Generate to a folder** with `.generate()` and read the output. Diff it against the original files.
5. **Compare in-game** against your baseline before moving on.
6. **Repeat by subsystem**, then extract the shared Kotlin helpers the repetition has revealed.

Steps 4 and 5 are the point. A slice that generates plausible-looking files but was never loaded in-game is not
migrated.

Porting selectors deserves a note, because it is where most of the friction is. Both forms below produce the same
selector model, so a parsed string can be migrated property-by-property with no behavior change:

```kotlin
val activePlayersPorted = selector("@a[gamemode=!spectator,scores={lives=1..,round=1..}]")

val nearbyActivePlayers = selector("@a[gamemode=!spectator]") {
	distance = rangeOrIntEnd(16)
}
```

If your pack depends on **another** datapack, import it with [Bindings](/docs/advanced/bindings) rather than
re-declaring its IDs as string literals.

## Project structure that scales

Once more than one slice has landed, the source layout starts to matter. A shape that works for large packs:

- `pack/` - pack bootstrap and configuration.
- `feature/` - domain modules (combat, quests, economy, UI, progression).
- `runtime/` - lifecycle and tick routing.
- `resources/` - data-driven definitions.
- `interop/` - imported or bound external packs.

Mirror those prefixes in the **generated** paths too (`feature/`, `runtime/`, `system/`), so a function you find
in-game maps back to a Kotlin file without guessing.

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

Two conventions carry most of the weight here: `DataPack` extensions **register** things, `Function` extensions
**produce or emit** commands.

## Kotlin patterns worth adopting

### Reference functions by value, not by path string

Function factories give you rename, find-usages, and a compile error on every caller when something moves. String paths
give you none of that:

```kotlin
fun Function.welcomeAnnounce() = function("feature/welcome/announce") {
	tellraw(allPlayers(), textComponent("Welcome to the server"))
}

fun Function.joinEffects() = function("feature/welcome/join_effects") {
	effect(allPlayers()) { give(Effects.RESISTANCE, duration = 3, amplifier = 0) }
	say("Join effects applied")
}

fun DataPack.registerJoinFlow() {
	load("system/join_bootstrap") {
		function(welcomeAnnounce())
		function(joinEffects())
	}
}
```

This is the single highest-value habit to adopt early - retrofitting it later means touching every call site.

### Split registration from reusable snippets

```kotlin
fun Function.combatPipeline() = function("feature/combat/pipeline") {
	applyJoinEffects()
	runRoundRules()
}

fun Function.applyJoinEffects() {
	effect(allPlayers()) { give(Effects.RESISTANCE, duration = 3, amplifier = 0) }
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

`combatPipeline` becomes a real `.mcfunction`; `applyJoinEffects` and `runRoundRules` inline into it. See
[the Cookbook](/docs/guides/cookbook#reuse-logic-as-a-real-function-or-inlined) for when to pick which.

### Turn tuning values into typed configs

Balancing numbers scattered across functions are the hardest thing to review in a hand-written pack. Make them data:

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

One `WaveConfig` list now defines every wave, and a balance pass is a reviewable diff instead of an archaeology
session.

More patterns - selectors as domain rules, custom items validated by predicates, module choice - are in the
[Cookbook](/docs/guides/cookbook).

## Data-driven resources are values, not files

This is where the migration pays off most, and it is the part veterans usually underestimate. In a hand-written pack,
a loot table or a predicate is a **file**, and everything that points at it is a **string** you retyped:
`"mypack:predicates/is_active"`, spelled slightly differently in four places, verified only by `/reload`.

In Kore, every data-driven builder **returns a typed argument** identifying the resource it just declared:

| Builder | Returns |
|---------|---------|
| `predicate("...") { }` | `PredicateArgument` |
| `lootTable("...") { }` | `LootTableArgument` |
| `itemModifier("...") { }` | `ItemModifierArgument` |
| `advancement("...") { }` | `AdvancementArgument` |
| `function("...") { }` | `FunctionArgument` |

Assign that to a `val` and the resource becomes a first-class value. Rename it, find its usages, and get a compile
error the moment something points at a resource that no longer exists.

### Declare once, reference everywhere

The same `PredicateArgument` works as an `execute` condition, as a nested condition inside another predicate via
`reference`, and as an enchantment effect requirement - all from one declaration:

```kotlin
fun DataPack.registerCombatRules() {
	val isActiveFighter = predicate("rules/is_active_fighter") {
		entityScores(EntityType.THIS) {
			this["lives"] = intRange(1f, 99f)
		}
	}

	// 1. As a command condition.
	function("feature/combat/tick") {
		execute {
			ifCondition(isActiveFighter)
			run {
				say("fighting")
			}
		}
	}

	// 2. Composed into another predicate.
	predicate("rules/is_wounded_fighter") {
		reference(isActiveFighter)
		entityScores(EntityType.THIS) {
			this["health"] = intRange(1f, 5f)
		}
	}
}
```

Compare that with the vanilla version, where the second predicate embeds
`{"condition": "minecraft:reference", "name": "mypack:rules/is_active_fighter"}` - a string nothing checks.

The same holds across resource kinds. A `LootTableArgument` feeds the `loot` command, the `containerLoot` item
component, and advancement rewards:

```kotlin
fun DataPack.registerRewards() {
	val bossDrop = lootTable("rewards/boss_drop") {
		pool {
			entries {
				item(Items.DIAMOND)
			}
		}
	}

	function("feature/boss/on_death") {
		loot(allPlayers(), bossDrop)
	}

	advancement("progression/boss_slain") {
		rewards {
			loots(bossDrop)
		}
	}
}
```

Move or rename `bossDrop` and both call sites follow. In a hand-written pack this is a grep-and-pray refactor.

### Generate families of resources instead of copy-pasting JSON

Hand-written packs accumulate near-identical JSON files: nine tier variants of a loot table, one advancement per
collectible, a recipe per material. Kore turns that into a loop over data, which is the single biggest file-count
reduction in most migrations:

```kotlin
data class Tier(val id: String, val item: ItemArgument, val weight: Int)

val tiers = listOf(
	Tier("common", Items.IRON_INGOT, 10),
	Tier("rare", Items.GOLD_INGOT, 4),
	Tier("epic", Items.DIAMOND, 1),
)

fun DataPack.registerTierDrops(): Map<String, LootTableArgument> = tiers.associate { tier ->
	tier.id to lootTable("rewards/${tier.id}_drop") {
		pool {
			entries {
				item(tier.item) {
					weight = tier.weight
				}
			}
		}
	}
}
```

One list is now the source of truth for every tier. Adding a tier is one line, not a new file plus three call sites you
have to remember to update - and the returned map keeps every table addressable by id, still typed.

### Factor out shared fragments, not just whole resources

Reuse is not limited to complete resources. Any nested builder chunk can become a `fun`, so a condition or a pool that
appears in twelve loot tables is written once:

```kotlin
fun LootTable.commonJunkPool() = pool {
	rolls = constant(1f)
	entries {
		item(Items.STRING) { weight = 5 }
		item(Items.BONE) { weight = 3 }
	}
}

fun DataPack.registerChests() {
	lootTable("chests/hallway") {
		commonJunkPool()
		pool {
			entries { item(Items.IRON_INGOT) }
		}
	}

	lootTable("chests/vault") {
		commonJunkPool()
		pool {
			entries { item(Items.DIAMOND) }
		}
	}
}
```

The rule mirrors the one for functions: extension functions on the **builder type** (`LootTable`, `Predicate`,
`Advancement`) emit into whatever resource is being built, while extensions on `DataPack` register new resources.

### Migration order for data-driven content

Data-driven resources are usually the **easiest slices to port first**, before touching any command logic:

1. Pick one resource family (all your loot tables, or all your predicates).
2. Port them as-is, one `val` per file, without trying to deduplicate yet.
3. Generate with `.generate()` and **diff the JSON against the original files**. This is the strongest verification
   available anywhere in the migration - byte-level parity, no game needed.
4. Only once the diff is clean, collapse the near-duplicates into loops and shared fragments, and re-diff.

Step 3 is why data-driven content is a good starting point: unlike command logic, correctness is mechanically
checkable before you ever load the world.

Per-resource references: [Loot Tables](/docs/data-driven/loot-tables), [Predicates](/docs/data-driven/predicates),
[Recipes](/docs/data-driven/recipes), [Advancements](/docs/data-driven/advancements),
[Item Modifiers](/docs/data-driven/item-modifiers), [Tags](/docs/data-driven/tags).

## Common migration mistakes

- **Porting file-by-file instead of behavior-by-behavior.**
  Migrate vertical slices and validate each runtime path before moving on.
- **Building giant wrappers on day one.**
  Start with direct DSL usage; extract only patterns that have actually repeated.
- **Letting generated paths drift from your Kotlin structure.**
  Adopt stable prefixes (`feature/`, `runtime/`, `system/`) from the first slice.
- **Treating Kotlin as runtime state.**
  Kotlin runs at generation time only. See [Runtime Logic](/docs/concepts/runtime-logic) for real in-game state.
- **Keeping critical IDs as ad-hoc strings.**
  Centralize objectives and resource IDs in constants, as in `Objectives` above.
- **Skipping the in-game check because the JSON looks right.**
  Valid JSON that loads is not the same as behavior that matches your baseline.

## Verification loop

Once migrated, a release loop that catches regressions:

1. Generate unpacked output with `.generate()` so it is reviewable.
2. Run the smoke checks from step 1 of the migration in-game.
3. Check generated tags and resources for namespace and path correctness.
4. Package with `.generateZip()` or `.generateJar()` only after behavior checks pass.
5. Keep output deterministic so CI can diff generated files and code review catches surprises.

Generation is deterministic from your Kotlin source and config, which is what makes step 5 work: committing generated
output (or diffing it in CI) turns "did that refactor change anything?" into a mechanical question.

### Parity checkpoints

When something looks wrong in-game, inspect the generated files and compare against the vanilla references before
suspecting Kore:

- **`pack.mcmeta` format**: modern packs use `min_format`/`max_format`. See
  [Minecraft Wiki - pack.mcmeta](https://minecraft.wiki/w/Pack.mcmeta) and
  [pack format](https://minecraft.wiki/w/Pack_format).
- **Root and namespace rules**: see [Minecraft Wiki - data pack](https://minecraft.wiki/w/Data_pack).
- **Lifecycle tags**: `load` and `tick` are still plain function-tag wiring; runtime behavior is vanilla tag dispatch.
- **Scheduling**: `schedule function ...` semantics are Minecraft-native. Kore only improves the authoring ergonomics.

## Migration checklist

**Before you start**

- Freeze current behavior with a manual test matrix.
- Inventory shared naming conventions and objective IDs.
- Decide your module set (`kore` alone, or plus `helpers`/`oop` - see the [Cookbook](/docs/guides/cookbook#pick-the-right-module)).

**First slice**

- Port lifecycle (`load`, `tick`) plus one gameplay feature.
- Introduce extension-based registration (`DataPack.registerX()`).
- Port one data-driven resource to a typed builder and diff the JSON against the original.

**Stabilization**

- Extract reusable selector and predicate helpers.
- Introduce typed config objects for balancing-heavy systems.
- Optionally bring in external resources through `bindings`.

## Where to go next

- [Creating a Datapack](/docs/guides/creating-a-datapack) - metadata, output modes, packaging
- [Runtime Logic](/docs/concepts/runtime-logic) - in-game conditionals and variables
- [Functions](/docs/commands/functions) - composition and lifecycle hooks
- [Cookbook](/docs/guides/cookbook) - the individual patterns referenced above
- [Bindings](/docs/advanced/bindings) - importing external datapacks
- [Known Issues](/docs/advanced/known-issues) - current limitations
