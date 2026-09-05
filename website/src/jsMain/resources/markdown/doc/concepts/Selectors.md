---
root: .components.layouts.MarkdownLayout
title: Selectors
nav-title: Selectors
description: Build Minecraft target selectors in Kore with typed Kotlin builders. Compose entity filters, sorting, and score-based conditions instead of writing @e[...] strings by hand.
keywords: minecraft, datapack, kore, selectors, target selectors, entities, players, commands
date-created: 2026-04-21
date-modified: 2026-08-14
routeOverride: /docs/concepts/selectors
---

# Selectors

Minecraft target selectors choose players or entities without hardcoding a UUID or exact player name. Kore exposes them
as
typed builders, so you can compose filters in Kotlin instead of manually writing `@e[...]` strings.

For the vanilla syntax reference, see
the [Minecraft Wiki target selectors page](https://minecraft.wiki/w/Target_selectors).

## Base selector helpers

Kore provides helpers for the common Java Edition selector bases:

- `allPlayers()` -> `@a`
- `allEntities()` -> `@e`
- `nearestPlayer()` -> `@p`
- `nearestEntity()` -> `@n`
- `randomPlayer()` -> `@r`
- `self()` -> `@s`
- `player("Name")` -> player-name-filtered `@a[...]`

```kotlin
val everyone = allPlayers()
val executor = self()
val nearest = nearestPlayer()
```

`allPlayers` and `allEntities` take either a `limitToOne: Boolean` or an explicit `limit: Int` as their first
argument, so `allEntities(3)` and `allEntities(limitToOne = true)` both avoid writing `limit` inside the builder.
`allEntitiesLimitToOne()` is a named shorthand for the latter. `selector(SelectorType.X)` is the generic entry point
the others delegate to.

All of them return a `SelectorArgument`. That type implements `EntityArgument`, `DataArgument`, `PossessorArgument`,
and `ScoreHolderArgument`, which is why the same value can be passed to commands that target entities, read NBT, or
hold scores.

## Filtering targets

Each selector helper accepts a `SelectorArguments` builder.

```kotlin
val nearbyZombies = allEntities {
	type = EntityTypes.ZOMBIE
	distance = rangeOrIntEnd(16) // ..16, meaning up to 16 blocks away
	sort = Sort.NEAREST          // sort runs before limit, so this keeps the 5 closest
	limit = 5
}
```

This generates a selector equivalent to:

```mcfunction
@e[type=minecraft:zombie,distance=..16,sort=nearest,limit=5]
```

## Common selector arguments

Kore exposes the main Java Edition selector filters directly as mutable properties.

- position: `x`, `y`, `z`
- volume: `dx`, `dy`, `dz`
- distance: `distance`
- scoreboard filters: `scores`
- advancement filters: `advancements`
- sort and cap: `sort`, `limit`
- player/entity metadata: `name`, `team`, `tag`, `gamemode`, `type`, `predicate`, `nbt`
- rotations: `xRotation`, `yRotation`

Example with position and volume:

```kotlin
// x/y/z is one corner of the box, dx/dy/dz its size along each axis
val entitiesInRoom = allEntities {
	x = 10.0
	y = 64.0
	z = -4.0
	dx = 8.0
	dy = 4.0
	dz = 8.0
}
```

## Score-based filtering

Selectors integrate nicely with [Scoreboards](/docs/concepts/scoreboards) and other scoreboard-driven logic. Open a
`scores { }` block inside the selector builder - it assigns the filter for you, so do not write `scores = scores { }`.

```kotlin
val activePlayers = allPlayers {
	scores {
		"round" greaterThanOrEqualTo 1
		"lives" greaterThan 0
	}
}
```

Three notations are available inside the block, and they can be mixed:

```kotlin
val filtered = allEntities {
	scores {
		"baz" lessThan 1
		score("bar") greaterThanOrEqualTo 1
		score("foo", rangeOrInt(1))
	}
}
```

```mcfunction
@e[scores={baz=..0,bar=1..,foo=1}]
```

The comparison words are `lessThan`, `lessThanOrEqualTo`, `equalTo`, `greaterThanOrEqualTo`, `greaterThan`, and
`matches` (which takes a range). Kore converts each into the vanilla inclusive-range syntax, so `greaterThan 0`
becomes `1..`.

`advancements { }` works the same way for advancement filters.

That is especially useful in [`execute`](/docs/commands/commands), timers, game loops, and mini-game state tracking.

## Inverting filters

Several selector filters support inversion.

```kotlin
// ! negates a filter, becoming gamemode=!spectator in the generated selector
val nonSpectators = allPlayers {
	gamemode = !Gamemode.SPECTATOR
	team = !"admins"
}
```

You can also invert `type`, `predicate`, and `nbt` filters.

## Limit and sorting

Kore keeps the vanilla `sort` + `limit` pattern explicit.

```kotlin
val oneRandomPlayer = allPlayers {
	sort = Sort.RANDOM
	limit = 1
}

val nearestMarkedEntity = allEntities(limitToOne = true) {
	tag = "arena_marker"
	sort = Sort.NEAREST
}
```

Use `limitToOne = true` when you want a concise single-target selector without repeating `limit = 1`.

## Parsing selector strings

You can build a selector from its vanilla command representation with the `selector(String)` overload, then optionally
refine it with the regular builder.

```kotlin
val fighters = selector("@a[tag=fighter,gamemode=!spectator]")

val nearbyFighters = selector("@a[tag=fighter]") {
  distance = rangeOrIntEnd(16)
}
```

Lower-level entry points are also available: `Selector.fromString("@e[limit=1]")` returns a `Selector`, and
`SelectorArguments.fromString("limit=1,tag=!foo")` parses only the bracket content. All selector arguments are
supported, including nested `scores`, `advancements`, and SNBT `nbt` filters.

## Using selectors in commands

Selectors can be reused anywhere an `EntityArgument`, `DataArgument`, `PossessorArgument`, or `ScoreHolderArgument` is
accepted, so they show up naturally across the [Commands](/docs/commands/commands)
and [Functions](/docs/commands/functions) APIs.

```kotlin
val fighters = allPlayers {
	tag = "fighter"
}

function("round_start") {
	effect(fighters) { give(Effects.SPEED, duration = 10, amplifier = 1) }
	tellraw(fighters, textComponent("Fight!"))
	scoreboard.players.set(fighters, "combo", 0)
}
```

## Selectors and OOP entity handles

The [`oop` module](/docs/oop/entities-and-players) wraps selectors in `Entity` handles so gameplay code can call
methods instead of rebuilding filters. The two layers connect in one direction only:

- `Entity.asSelector()` turns a handle into a `SelectorArgument` you can pass to any command.
- There is **no** conversion the other way. `self()`, `allPlayers()`, and friends return `SelectorArgument`, not
  `Entity`, so they cannot be passed to `Entity`-only helpers such as `getScoreEntity` or `batch`.

`Entity` also always renders as `@e[...]`, so no handle can represent `@s`. When the executing entity is already
`@s`, stay on the selector layer and use the core command DSL:

```kotlin
function("charge_up") {
	val lastCharge = scoreboard.objective(self(), "last_crystal_charge")
	lastCharge += 10
}
```

```mcfunction
scoreboard players add @s last_crystal_charge 10
```

## Practical tips

- Prefer reusable selector values when the same filter appears in several functions.
- Use `self()` when logic should apply to the current execution context.
- Use generated entity types and predicates instead of raw strings whenever possible.
- Keep complex filters readable by assigning them to `val`s before entering large command blocks, and check
  the [Cookbook](/docs/guides/cookbook) if you want to turn those values into reusable helpers.

## See also

- [Entities & Players](/docs/oop/entities-and-players) - OOP handles built on top of these selectors
- [Commands](/docs/commands/commands) - where selectors are consumed
- [Arguments Internals](/docs/contributing/arguments) - contributor-facing details about Kore's broader argument system
- [Minecraft Wiki: Target selectors](https://minecraft.wiki/w/Target_selectors) - vanilla syntax and semantics
