---
root: .components.layouts.MarkdownLayout
title: Cookbook
nav-title: Cookbook
description: Practical Kore recipes for common datapack patterns such as setup functions, custom items, timers, worldgen, and bindings.
keywords: minecraft, datapack, kore, cookbook, recipes, patterns, guide
date-created: 2026-04-21
date-modified: 2026-04-21
routeOverride: /docs/guides/cookbook
position: 3
---

# Cookbook

This page collects practical patterns you can lift into a real Kore datapack. It does not try to replace the full
reference pages; instead, it shows how several documented features fit together.

If your main goal is migrating an already mature datapack architecture, pair this page with
[From Datapacks to Kore](/docs/guides/from-datapacks-to-kore).

## Recipe 1 - A clean pack bootstrap

Use a dedicated setup function for one-time registration and a tick function for recurring gameplay logic.

```kotlin
fun DataPack.registerCoreSystems() {
	load("setup") {
		scoreboard.objectives.add("round", "dummy")
		scoreboard.objectives.add("lives", "dummy")
	}

	tick("game_loop") {
		execute {
			asTarget(allPlayers())
			run {
				say("tick")
			}
		}
	}
}

fun main() = dataPack("arena") {
	registerCoreSystems()
}.generateZip()
```

Use this pattern when you want a clear separation between initialization and runtime logic.

Related pages:

- [Functions](/docs/commands/functions)
- [Creating A Datapack](/docs/guides/creating-a-datapack)

## Recipe 2 - Reuse logic by exposing named functions from `Function`

Sometimes you want a helper that can be called from multiple places while still generating a normal datapack function.
An ergonomic pattern is to attach it directly to `Function`:

```kotlin
fun Function.myFunction() = function("my_function") {
	say("yay")
	say("also, yay")
}

load {
	function(myFunction())
}
```

This is totally fine even if several call sites end up recreating the same named function declaration. Kore is heavily
optimized for this kind of reuse, so regenerating the same function is effectively instant and keeps your code much
cleaner than manually caching every `FunctionArgument` yourself.

Use this when the code should remain callable as a proper datapack function, for example for scheduling, tags,
cross-function reuse, or debug visibility.

Related pages:

- [Functions](/docs/commands/functions)
- [Commands](/docs/commands/commands)

## Recipe 3 - Reuse code inline without creating a function

Not every reusable snippet needs its own generated function. If you just want to share a small block of commands,
regular `Function` extensions are often enough:

```kotlin
fun Function.saySomething() {
	say("yay")
	say("also, yay")
}

load {
	saySomething()
}
```

Prefer this pattern when the reused logic is small and should stay inlined at the call site instead of becoming a
separate `/function` entry.

Related pages:

- [Functions](/docs/commands/functions)
- [Cookbook](/docs/guides/cookbook)

## Recipe 4 - Gate gameplay with selectors and scores

Keep complex target selection in reusable values instead of repeating long selector builders.

```kotlin
val activePlayers = allPlayers {
	scores = scores {
		"round" greaterThanOrEqualTo 1
		"lives" greaterThan 0
	}
	gamemode = !Gamemode.SPECTATOR
}

function("start_wave") {
	effect.give(activePlayers, Effects.RESISTANCE, 5, 0)
	tellraw(activePlayers, textComponent("Wave started"))
}
```

This keeps wave logic readable and centralizes the rules that define an eligible player.

Related pages:

- [Selectors](/docs/concepts/selectors)
- [Scoreboards](/docs/concepts/scoreboards)

## Recipe 5 - Define a custom item and validate it later

Combine item components with predicates when the item should remain recognizable after being moved between inventories.

```kotlin
val arenaBlade = Items.DIAMOND_SWORD {
	customName(textComponent("Arena Blade", Color.AQUA))
	tooltipDisplay(showInTooltip = true)
}

val arenaBladePredicate = predicate("arena_blade") {
	matchTool(arenaBlade)
}

function("check_weapon") {
	execute {
		ifCondition(arenaBladePredicate)
		run {
			say("Correct weapon equipped")
		}
	}
}
```

Use this when a datapack needs both rich item metadata and reliable runtime checks.

Related pages:

- [Components](/docs/concepts/components)
- [Predicates](/docs/data-driven/predicates)
- [Item Modifiers](/docs/data-driven/item-modifiers)

## Recipe 6 - Schedule delayed actions instead of duplicating code

Wrap delayed logic in a named function and schedule it instead of inlining the same command sequence multiple times.

```kotlin
val explosionWarning = function("explosion_warning") {
	tellraw(allPlayers(), textComponent("Boom in 5 seconds!", Color.RED))
}

val explodeNow = function("explode_now") {
	summon(Entities.TNT, vec3())
}

function("trigger_explosion") {
	function(explosionWarning)
	schedule.function(explodeNow, 5.seconds)
}
```

This pattern is a good default for cutscenes, telegraphs, cooldowns, and delayed effects.

Related pages:

- [Commands](/docs/commands/commands)
- [Helpers Utilities](/docs/helpers/utilities)
- [Scheduler](/docs/helpers/scheduler)

## Recipe 7 - Choose between core, helpers, and oop early

When a system starts simple, keep it in `kore`. Add `helpers` for reusable glue. Move to `oop` once gameplay objects
need long-lived identities.

- Use **`kore`** for raw commands, data-driven JSON, tags, functions, and lightweight selectors.
- Use **`helpers`** for rendering pipelines, geometry, scheduler utilities, or state delegates.
- Use **`oop`** for players, teams, boss bars, timers, spawners, scoreboards, and state machines.

That decision alone prevents many documentation and architecture mistakes later.

Related pages:

- [Home](/docs/home)
- [Helpers Utilities](/docs/helpers/utilities)
- [OOP Utilities](/docs/oop/oop-utilities)

## Recipe 8 - Import an existing datapack, then wrap it with Kotlin

Use `bindings` when you already have a datapack and want typed access to its resources instead of stringly typed calls.

Typical flow:

1. Configure a binding source
2. Generate Kotlin wrappers
3. Call imported functions/resources from your own pack

This is especially useful for large internal libraries or third-party datapacks that your project depends on.

Related pages:

- [Bindings](/docs/advanced/bindings)
- [Functions](/docs/commands/functions)

## How to use this page

Treat these recipes as starting points:

- Extract repeating code into small reusable helpers
- Move cross-cutting conditions into selectors or predicates
- Favor typed arguments over handwritten command strings
- Keep links to the relevant reference pages nearby while you iterate