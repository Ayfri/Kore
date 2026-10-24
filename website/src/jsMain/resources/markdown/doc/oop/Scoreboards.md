---
root: .components.layouts.MarkdownLayout
title: Scoreboards
nav-title: Scoreboards
description: Object-oriented scoreboard management with the Kore OOP module - objectives, display slots, per-entity score operations, and score arithmetic operators.
keywords: minecraft, datapack, kore, oop, scoreboard, objective, score, display slot, fake player, operators, constants, expressions, compute, random float
date-created: 2026-03-03
date-modified: 2026-10-24
routeOverride: /docs/oop/scoreboards
---

# Scoreboards

Wraps [Minecraft scoreboards](https://minecraft.wiki/w/Scoreboard) with two distinct handle types:

- **`Scoreboard`** - an objective, identified by name. Create it, display it, name it.
- **`ScoreboardEntity`** - one entity's score *in* one objective. Read it, write it, copy it.

That split mirrors how vanilla splits `scoreboard objectives ...` from `scoreboard players ...`.

> [!FOOTPRINT]
> Only the objectives you create. `*=`, `/=` and `%=` against a plain `Int` add the `kore_constants` objective and a `#<value>` holder, written inline the first time a function uses that value. No storage, entity, forceloaded chunk, tick or load function.

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

| Function              | Description                                                             |
|-----------------------|-------------------------------------------------------------------------|
| `set`                 | Set score to a value                                                    |
| `add`                 | Add to the score                                                        |
| `remove`              | Subtract from the score                                                 |
| `reset`               | Reset the score                                                         |
| `copyTo`              | Copy this score to another holder/objective, or into entity/storage NBT |
| `copyFrom`            | Copy from another holder/objective                                      |
| `copyDataFrom`        | Store a numeric NBT value (entity or storage) into this score           |
| `copyEntityCountFrom` | Store how many entities match a selector into this score                |
| `copyMemberCountFrom` | Store a team's member count into this score                             |

### Operators

`ScoreboardEntity` supports `+=` and `-=` against a literal:

```kotlin
function("on_kill") {
	val kills = player.getScoreEntity("kills")
	kills += 1
	kills -= 1
}
```

The same operators work between two score handles, and `*=`, `/=`, `%=` are available on both sides. Every one of them
compiles to a single `scoreboard players operation`:

```kotlin
function("score_math") {
	val score = player.getScoreEntity("score")
	val kills = player.getScoreEntity("kills")

	score += kills  // scoreboard players operation ... += ...
	score *= kills
	score /= kills  // floored division, as vanilla does
	score %= kills  // floored modulo

	score setTo kills
	score minWith kills   // keeps the smaller of the two
	score maxWith kills   // keeps the larger of the two
	score swapWith kills
}
```

`*=`, `/=` and `%=` also accept an `Int`. Vanilla has no literal right-hand side for `operation`, so Kore reads the
value from a fake-player constant, declared once per function in the `kore_constants` objective
(rename it through `OopConstants.constantsObjective`):

```kotlin
function("halve_score") {
	val score = player.getScoreEntity("score")
	score /= 2
}
```

```mcfunction
scoreboard objectives add kore_constants dummy
scoreboard players set #2 kore_constants 2
scoreboard players operation @e[...] score /= #2 kore_constants
```

### Fake players

A score holder starting with `#` is a *fake player*: a holder no real entity owns, which is how datapacks keep globals
and constants out of the sidebar. `fakePlayer` builds an `Entity` handle for one, so every `ScoreboardEntity` API works
against it:

```kotlin
function("next_wave") {
	val wave = fakePlayer("wave").getScoreEntity("game_state")
	wave += 1
	wave.copyTo(storage("game", "kore"), "wave")
}
```

```mcfunction
scoreboard players add #wave game_state 1
execute store result storage kore:game wave int 1 run scoreboard players get #wave game_state
```

`fakePlayer("wave")` and `fakePlayer("#wave")` both produce `#wave`. Use the `FakePlayer` constructor directly for
holders that must not carry that prefix, such as the `§0`..`§f` holders behind sidebar lines.

A fake player has no entity behind it, so `asSelector()` throws instead of emitting `@e[name=#wave]`, which matches an
entity custom name and would silently target nothing. Only score APIs, which go through `asScoreHolder()`, accept one.

### Expressions

`setTo { }` computes a score from other scores in one [`compute`](/docs/commands/commands#compute-command), where the
`scoreboard players operation` route needs a temporary holder per step. Inside the lambda, `score(entry)` reads a
`ScoreboardEntity` and the [provider operators](/docs/data-driven/loot-tables#expressions) build the tree:

```kotlin
function("combo") {
	val kills = player.getScoreEntity("kills")
	val bonus = fakePlayer("bonus").getScoreEntity("game")
	val points = fakePlayer("points").getScoreEntity("game")

	points setTo { score(kills) + score(bonus) * 2 }
}
```

```mcfunction
execute store result score #points game run compute entity @e[...] integer {type:"minecraft:add",inputs:[{type:"minecraft:score",score:kills,target:{type:"minecraft:context",target:target_entity}},{type:"minecraft:mul",inputs:[{type:"minecraft:score",score:game,target:{type:"minecraft:fixed",name:"#bonus"}},2]}]}
```

A fake player is read by name. A real entity is read as the `compute entity` target, so an expression reads one real
entity at most, in as many objectives as it needs, next to any number of fake players. Reading two different real
entities isn't possible because `compute` has one target: copy one score into a fake player first. A selector that can
match several entities throws for the same reason. With only fake players, the command is `compute default`.
`data(storage).computeScores("path") { ... }` builds the same expression into a `data modify` target, with the same rules.

A lambda returning a float provider runs in `float` mode, and the optional `scale` multiplies the result before it is
rounded down into the score (`2.5` with a scale of `100` gives `250`). That makes random floats a one-liner:

```kotlin
function("roll") {
	val chance = fakePlayer("chance").getScoreEntity("game")
	chance.setTo(1000f) { uniform(0.5f, 2f) }  // a score between 500 and 1999
}
```

```mcfunction
execute store result score #chance game run compute default float {type:"minecraft:uniform",max:2,min:0.5d} 1000
```

A `Float` factor on `*=` and `/=` is one `compute` too, where an `Int` factor keeps the `scoreboard players operation`
route above:

```kotlin
function("bonus") {
	val gold = fakePlayer("gold").getScoreEntity("game")
	gold *= 1.5f
	gold /= 2.5f
}
```

```mcfunction
execute store result score #gold game run compute default float {type:"minecraft:mul",inputs:[{type:"minecraft:from_int",input:{type:"minecraft:score",score:game,target:{type:"minecraft:fixed",name:"#gold"}}},1.5d]}
execute store result score #gold game run compute default float {type:"minecraft:div",left:{type:"minecraft:from_int",input:{type:"minecraft:score",score:game,target:{type:"minecraft:fixed",name:"#gold"}}},right:2.5d}
```

| Behavior                  | Result                                                                                       |
|---------------------------|----------------------------------------------------------------------------------------------|
| Float results             | Rounded down, so `-3 * 1.5` gives `-5`, and saturated at the int range                       |
| A failing expression      | Stores `0`: a division by zero (`/= 0f`), an overflow or a result that isn't finite          |
| Float precision           | `from_int` and the float providers are exact up to 2^24 only, use the `Int` form beyond that |

### Copying between scores, NBT, and counts

```kotlin
function("sync") {
	val stat = player.getScoreEntity("stats.charge")

	stat.copyFrom(player.asSelector(), "deaths") // from another score

	// from NBT: scores are integers, so a scale converts fractional values
	stat.copyDataFrom(player, "Inventory[0].count")
	stat.copyDataFrom(storage("stats", "kore"), "charge")

	// from a live count of matching entities
	stat.copyEntityCountFrom(entity(EntityTypes.ZOMBIE, limitToOne = false))
	stat.copyMemberCountFrom(team("red"))

	// and back out into NBT
	stat.copyTo(player, "kore.charge", DataType.INT)
	stat.copyTo(storage("stats", "kore"), "charge", DataType.INT)
}
```

A score to score copy is a single `scoreboard players operation <this> = <source>`, the NBT and count copies compile to
an `execute store result ... run ...` chain, so you never have to write the store plumbing by hand.

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
operators (`++` / `--` need a `var`, Kotlin reassigns the variable), and the `min` / `max` infix operations:

```kotlin
function("self_score_math") {
	var mine = scoreboard.objective(self(), "score")
	val best = scoreboard.objective(literal("#best"), "score")

	mine++
	best max mine // keeps the larger of the two in best
	mine.operation(Operation.SET, literal("#best"), "score")
}
```

Use `scoreboard.objective(...)` whenever the score holder is `@s` or any selector you already have as an `Argument`.
Use `ScoreboardEntity` when you hold an OOP `Entity`, including a [fake player](#fake-players).

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
