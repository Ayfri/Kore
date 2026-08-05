---
root: .components.layouts.MarkdownLayout
title: Kore Cookbook - Practical Datapack Patterns in Kotlin
nav-title: Cookbook
description: "Working Kore patterns to copy: bootstrap and tick structure, reusable functions, selector and predicate helpers, scheduled actions, and module choice."
keywords: minecraft datapack patterns, kore examples, kotlin datapack code, datapack bootstrap, reusable mcfunction, datapack selectors, scheduled function, kore cookbook
date-created: 2026-04-21
date-modified: 2026-08-05
routeOverride: /docs/guides/cookbook
position: 3
---

# Cookbook

Short, self-contained answers to questions that come up in every real pack. Each entry states the problem, shows the
Kore version, and says when to prefer it over the alternative.

These are patterns, not a reference: each one links to the page that documents the API in full. If you are porting an
existing hand-written pack, read [From Datapacks to Kore](/docs/guides/from-datapacks-to-kore) first for the migration
strategy, then use this page for the individual pieces.

## Structure a pack: bootstrap vs tick

**Problem:** everything ends up in one function, and one-time setup gets re-run every tick.

Split the two lifecycles explicitly. `load` runs once after `/reload`, `tick` runs every game tick:

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

Registering objectives inside `tick` would work but wastes a command every tick; anything idempotent and one-time
belongs in `load`.

See [Functions](/docs/commands/functions) and [Creating a Datapack](/docs/guides/creating-a-datapack).

## Reuse logic as a real function or inlined

**Problem:** the same commands appear in several places, and it is not obvious whether they deserve their own
`.mcfunction`.

Both options are `Function` extensions - the difference is whether you call `function(...)` inside.

**Generate a real function** when the logic must be callable in its own right (scheduling, function tags, `/function`
from chat, debug visibility):

```kotlin
fun Function.myFunction() = function("my_function") {
	say("yay")
	say("also, yay")
}

load {
	function(myFunction())
}
```

Several call sites re-declaring the same named function is fine. Kore is optimized for this, so regenerating the same
function is effectively instant and stays cleaner than caching every `FunctionArgument` by hand.

**Inline the commands** when the snippet is small and only exists to avoid copy-paste - no extra `/function` call at
runtime:

```kotlin
fun Function.saySomething() {
	say("yay")
	say("also, yay")
}

load {
	saySomething()
}
```

Rule of thumb: inline by default, extract into a real function the moment something needs to *reference* it.

See [Functions](/docs/commands/functions) and [Commands](/docs/commands/commands).

## Stop repeating long selectors

**Problem:** the same `@a[...]` constraints are rewritten in ten places, and updating the rule means finding all ten.

A selector is a value. Name it once and reuse it:

```kotlin
val activePlayers = allPlayers {
	scores = scores {
		"round" greaterThanOrEqualTo 1
		"lives" greaterThan 0
	}
	gamemode = !Gamemode.SPECTATOR
}

function("start_wave") {
	effect(activePlayers) { give(Effects.RESISTANCE, duration = 5, amplifier = 0) }
	tellraw(activePlayers, textComponent("Wave started"))
}
```

The name documents the intent ("who counts as playing right now") and one edit updates every use.

See [Selectors](/docs/concepts/selectors) and [Scoreboards](/docs/concepts/scoreboards).

## Make a custom item recognizable later

**Problem:** you give a player a custom item, then cannot reliably identify it once it has moved between inventories.

Define the item once with its components, and derive a predicate from that same value:

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

Because the predicate is built from `arenaBlade`, changing the item cannot desynchronize the check.

See [Components](/docs/concepts/components), [Predicates](/docs/data-driven/predicates) and
[Item Modifiers](/docs/data-driven/item-modifiers).

## Delay an action without duplicating it

**Problem:** a telegraph, cooldown, or cutscene beat needs the same commands now and again later.

Name the delayed step as a function and schedule that:

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

`schedule` needs a real function reference, which is exactly why this case justifies generating one rather than
inlining.

See [Commands](/docs/commands/commands), [Helpers Utilities](/docs/helpers/utilities) and
[Scheduler](/docs/helpers/scheduler).

## Pick the right module

**Problem:** unclear whether a system should use plain `kore`, or pull in `helpers`/`oop`.

Escalate only when the simpler layer starts hurting:

- **`kore`** - raw commands, data-driven JSON, tags, functions, selectors. Start here, always.
- **`helpers`** - add it when you are rewriting infrastructure glue: renderers, geometry, raycasts, scheduler patterns,
  scoreboard math, state delegates.
- **`oop`** - add it when gameplay needs long-lived identities that several systems share: players, teams, boss bars,
  timers, spawners, state machines.

The failure mode is reaching for `oop` on day one and wrapping everything in abstractions the pack never needed. Write
it with `kore`, then move up when the duplication is real.

See [Home](/docs/home), [Helpers Utilities](/docs/helpers/utilities) and [OOP Utilities](/docs/oop/oop-utilities).

## Depend on an existing datapack

**Problem:** your pack calls into another pack's functions using hand-typed ID strings that break silently when that
pack changes.

Import it with `bindings` and get typed Kotlin constants instead:

1. Configure a binding source.
2. Generate the Kotlin wrappers.
3. Call the imported functions and resources from your own pack.

Worth it for large internal libraries or third-party packs you track across versions - a renamed function then becomes a
compile error rather than a silent no-op in-game.

See [Bindings](/docs/advanced/bindings), which is **experimental**, and [Functions](/docs/commands/functions).

## Principles behind these

- Extract repeated code into small helpers before it spreads.
- Move cross-cutting conditions into named selectors or predicates.
- Prefer typed arguments over hand-written command strings.
- Let Kotlin values (items, selectors, function references) be the single source of truth, and derive everything else
  from them.
