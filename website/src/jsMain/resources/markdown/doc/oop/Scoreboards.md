---
root: .components.layouts.MarkdownLayout
title: Scoreboards
nav-title: Scoreboards
description: Object-oriented scoreboard management with the Kore OOP module - objectives, display slots, per-entity score operations, and score arithmetic operators.
keywords: minecraft, datapack, kore, oop, scoreboard, objective, score, display slot, plusAssign, operators
date-created: 2026-03-03
date-modified: 2026-08-14
routeOverride: /docs/oop/scoreboards
---

# Scoreboards

Wraps [Minecraft scoreboards](https://minecraft.wiki/w/Scoreboard) with two distinct handle types:

- **`Scoreboard`** - an objective, identified by name. Create it, display it, name it.
- **`ScoreboardEntity`** - one entity's score *in* one objective. Read it, write it, copy it.

That split mirrors how vanilla splits `scoreboard objectives ...` from `scoreboard players ...`.

## Which handle do I need?

| You want to...                                    | Use                                                      |
|---------------------------------------------------|----------------------------------------------------------|
| Create/remove an objective, set its display slot  | `scoreboard("name")` → `Scoreboard`                      |
| Read or write an OOP `Entity`'s score             | `entity.getScoreEntity("obj")` or `scoreboard.getScore(entity)` |
| Read or write a whole team's score                | `scoreboard.getScore(team)`                              |
| Read or write the score of `@s`, or any raw selector | `scoreboard.objective(selector, "obj")` - see [Raw selectors](#raw-selectors-and-s) |

`ScoreboardEntity`, `getScoreEntity`, and `getScore` all require a `Function` context, so call them **inside** a
`function { }` block, not at datapack top level.

## Objectives

```kotlin
function("scoreboard_setup") {
	scoreboard("kills") {
		create()                             // the objective must exist before any score is written
		setDisplaySlot(DisplaySlots.sidebar) // shows the scores on the right of the screen
		setDisplayName("Kill Count")         // shown to players, unlike the internal name "kills"
	}
}
```

| Function         | Description                              |
|------------------|------------------------------------------|
| `create`         | Create the objective (default `dummy`)   |
| `remove`         | Remove the objective                     |
| `setDisplaySlot` | Assign a display slot                    |
| `setDisplayName` | Set the display name (string or component) |
| `setRenderType`  | Set the render type                      |
| `getScore`       | Get a `ScoreboardEntity` for an entity or team |

`create()` takes an optional criterion, e.g. `create(ScoreboardCriteria.DEATH_COUNT)`.

## Per-entity scores

There are two equivalent ways to reach the same `ScoreboardEntity` - pick whichever reads better at the call site:

```kotlin
function("score_ops") {
	val kills = scoreboard("kills")

	val fromEntity = player.getScoreEntity("kills") // entity-first
	val fromObjective = kills.getScore(player)      // objective-first, same result

	fromEntity.set(10)
	fromEntity.add(5)
	fromEntity.remove(2)
	fromEntity.reset()
}
```

| Function                | Description                                            |
|-------------------------|--------------------------------------------------------|
| `set`                   | Set score to a value                                   |
| `add`                   | Add to the score                                       |
| `remove`                | Subtract from the score                                |
| `reset`                 | Reset the score                                        |
| `copyTo`                | Copy this score to another holder/objective, or into entity/storage NBT |
| `copyFrom`              | Copy from another holder/objective                     |
| `copyDataFrom`          | Store a numeric NBT value (entity or storage) into this score |
| `copyEntityCountFrom`   | Store how many entities match a selector into this score |
| `copyMemberCountFrom`   | Store a team's member count into this score            |

### Operators

`ScoreboardEntity` supports `+=` and `-=`:

```kotlin
function("on_kill") {
	val kills = player.getScoreEntity("kills")
	kills += 1
	kills -= 1
}
```

### Copying between scores, NBT, and counts

```kotlin
function("sync") {
	val stat = player.getScoreEntity("stats.charge")

	stat.copyFrom(player.asSelector(), "deaths") // from another score

	// from NBT: scores are integers, so a scale converts fractional values
	stat.copyDataFrom(player, "Inventory[0].count")
	stat.copyDataFrom(storage("kore", "stats"), "charge")

	// from a live count of matching entities
	stat.copyEntityCountFrom(entity(EntityTypes.ZOMBIE, limitToOne = false))
	stat.copyMemberCountFrom(team("red"))

	// and back out into NBT
	stat.copyTo(player, "kore.charge", DataType.INT)
	stat.copyTo(storage("kore", "stats"), "charge", DataType.INT)
}
```

Each of these compiles to an `execute store result score ... run ...` chain, so you never have to write the store
plumbing by hand.

## Raw selectors and `@s`

`ScoreboardEntity` is built around an OOP [`Entity`](/docs/oop/entities-and-players) handle, and `Entity` always
renders as `@e[...]`. There is no `Entity` that renders as `@s`, and `self()` returns a `SelectorArgument`, not an
`Entity` - so it cannot be passed to `getScoreEntity` or `getScore`.

For `@s` (or any other raw selector) use the core `scoreboard.objective(...)` DSL instead. It returns a
`PlayerObjective`, which supports the same arithmetic including operators:

```kotlin
function("charge_up") {
	val lastCharge = scoreboard.objective(self(), "last_crystal_charge")
	lastCharge += 10
}
```

This emits `scoreboard players add @s last_crystal_charge 10`.

`PlayerObjective` covers `set`, `add`, `remove`, `reset`, `get`, `enable`, `operation`, the `+=` / `-=` / `++` / `--`
operators, and the `min` / `max` infix operations:

```kotlin
function("score_math") {
	val mine = scoreboard.objective(self(), "score")
	// a name starting with # is a fake player: a score holder no real entity owns,
	// which is the usual way to store a global value
	val best = scoreboard.objective(literal("#best"), "score")

	mine++
	best max mine // keeps the larger of the two in best
	mine.operation(Operation.SET, literal("#best"), "score")
}
```

Use `scoreboard.objective(...)` whenever the score holder is `@s`, a fake player (`literal("#total")`), or any
selector you already have as an `Argument`. Use `ScoreboardEntity` when you already hold an OOP `Entity`.

## Practical pattern

```kotlin
function("match_setup") {
	val kills = scoreboard("kills")
	kills.create()
	kills.setDisplaySlot(DisplaySlots.sidebar)

	val playerKills = kills.getScore(player)
	playerKills.set(0)
}
```

Configure the objective once, then retrieve score handles wherever gameplay code needs to increment, reset, or copy
values.

## See also

- [Entities & Players](/docs/oop/entities-and-players) - The `Entity` handles that `getScoreEntity` and `getScore` need.
- [Teams](/docs/oop/teams) - `scoreboard.getScore(team)` and team member counters.
- [Timers](/docs/oop/timers) - Scoreboard-backed countdowns built on this API.
