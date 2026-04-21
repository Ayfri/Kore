---
root: .components.layouts.MarkdownLayout
title: Selectors
nav-title: Selectors
description: Build Minecraft target selectors in Kore with typed helpers, filters, sorting, and score-based conditions.
keywords: minecraft, datapack, kore, selectors, target selectors, entities, players, commands
date-created: 2026-04-21
date-modified: 2026-04-21
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

## Filtering targets

Each selector helper accepts a `SelectorArguments` builder.

```kotlin
val nearbyZombies = allEntities {
	type = EntityTypes.ZOMBIE
	distance = rangeOrIntEnd(16)
	sort = Sort.NEAREST
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

Selectors integrate nicely with scoreboard-driven logic.

```kotlin
val activePlayers = allPlayers {
	scores = scores {
		"round" greaterThanOrEqualTo 1
		"lives" greaterThan 0
	}
}
```

That is especially useful in `execute`, timers, game loops, and mini-game state tracking.

## Inverting filters

Several selector filters support inversion.

```kotlin
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

## Using selectors in commands

Selectors can be reused anywhere an `EntityArgument`, `DataArgument`, `PossessorArgument`, or `ScoreHolderArgument` is
accepted.

```kotlin
val fighters = allPlayers {
	tag = "fighter"
}

function("round_start") {
	effect.give(fighters, Effects.SPEED, 10, 1)
	tellraw(fighters, textComponent("Fight!"))
	scoreboard.players.set(fighters, "combo", 0)
}
```

## Practical tips

- Prefer reusable selector values when the same filter appears in several functions.
- Use `self()` when logic should apply to the current execution context.
- Use generated entity types and predicates instead of raw strings whenever possible.
- Keep complex filters readable by assigning them to `val`s before entering large command blocks.

## See also

- [Arguments](/docs/concepts/arguments) - the broader typed argument model in Kore
- [Commands](/docs/commands/commands) - command builders that consume selectors everywhere
- [Functions](/docs/commands/functions) - organizing selector-heavy logic into reusable functions
- [Scoreboards](/docs/concepts/scoreboards) - selectors often pair with scoreboard state
- [Minecraft Wiki: Target selectors](https://minecraft.wiki/w/Target_selectors) - vanilla syntax and semantics