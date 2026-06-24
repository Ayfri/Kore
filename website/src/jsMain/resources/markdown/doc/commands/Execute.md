---
root: .components.layouts.MarkdownLayout
title: Execute
nav-title: Execute
description: Full guide to the execute command in Kore - context subcommands, conditions, stores, and the run clause.
keywords: minecraft, datapack, kore, execute, command, conditions, if, unless, store, run
date-created: 2026-06-24
date-modified: 2026-06-24
routeOverride: /docs/commands/execute
---

# Execute

`execute` is the most important command in any datapack. It modifies the **context** a command runs in (who runs it,
where,
facing which way), **branches** on world state, and **stores** a command's result. Almost all runtime logic flows
through
it - see [Runtime Logic](/docs/concepts/runtime-logic) for the bigger picture, and the
[Minecraft Wiki](https://minecraft.wiki/w/Commands/execute) for the vanilla command semantics.

In Kore you build an execute chain with the `execute { }` builder. Each call inside the block appends one subcommand, in
order, and the final `run` clause is the command that actually runs:

```kotlin
function("greet_nearby") {
	execute {
		asTarget(allPlayers())
		at(self())

		run {
			say("Hello!")
		}
	}
}
```

This generates `execute as @a at @s run say Hello!`.

## Context subcommands

These change who, where, and how the chained command runs. They can be combined freely and apply left to right.

```kotlin
execute {
	align(Axes.XYZ)                 // floor the position to block grid
	anchored(Anchor.EYES)           // set the local-coordinate anchor
	asTarget(allPlayers())          // run as each player (changes @s)
	at(self())                      // run at the executor's position/rotation/dimension
	facing(vec3(10, 64, 10))        // face an absolute position
	facingEntity(self(), Anchor.EYES) // face an entity's eyes/feet
	inDimension(Dimensions.THE_END) // switch dimension
	on(Relation.OWNER)              // hop to a related entity (owner, vehicle, ...)
	positioned(vec3(0, 64, 0))      // override the position
	positionedAs(self())            // take the position from a target
	positionedOver(HeightMap.WORLD_SURFACE) // snap Y to a heightmap
	rotated(rotation(0.rot, 0.rot)) // override the rotation
	rotatedAs(self())               // take rotation from a target
	summon(EntityTypes.MARKER)      // summon and run as the new entity

	run {
		say("context set")
	}
}
```

| Subcommand                                      | Effect                                                                      |
|-------------------------------------------------|-----------------------------------------------------------------------------|
| `align(axes, offset?)`                          | Floors the position onto the block grid on the given axes                   |
| `anchored(anchor)`                              | Sets `EYES`/`FEET` anchor for `^ ^ ^` and `facing`                          |
| `asTarget(target)`                              | Runs as each matched entity, changing `@s` and the executor                 |
| `at(target)`                                    | Sets position, rotation, and dimension to the target's                      |
| `facing(vec3)` / `facingEntity(target, anchor)` | Rotates to face a point or an entity                                        |
| `inDimension(dimension)`                        | Switches the execution dimension                                            |
| `on(relation)`                                  | Moves to a related entity (`OWNER`, `VEHICLE`, `TARGET`, `PASSENGERS`, ...) |
| `positioned(vec3)` / `positionedAs(target)`     | Sets the position absolutely or from a target                               |
| `positionedOver(heightMap)`                     | Sets Y to the top block of a heightmap at the current X/Z                   |
| `rotated(rotation)` / `rotatedAs(target)`       | Sets the rotation absolutely or from a target                               |
| `summon(entityType)`                            | Summons a new entity and runs as/at it                                      |

## Conditions: if / unless

`ifCondition` and `unlessCondition` gate the rest of the chain. A block can hold several checks - they are ANDed
together.

```kotlin
function("conditional") {
	execute {
		ifCondition {
			entity(allPlayers())                 // at least one player exists
			block(vec3(0, 63, 0), Blocks.STONE)  // block at pos is stone
			score(self(), "coins", rangeOrInt(10)) // score matches 10..
		}

		unlessCondition {
			score(self(), "frozen", rangeOrInt(1)) // and NOT frozen >= 1
		}

		run {
			say("All conditions matched")
		}
	}
}
```

Available checks inside the condition block:

| Check                                       | Meaning                                            |
|---------------------------------------------|----------------------------------------------------|
| `biome(pos, biome)`                         | The biome at `pos` matches a biome or biome tag    |
| `block(pos, block)`                         | The block at `pos` matches a block or block tag    |
| `blocks(start, end, dest, mode)`            | A region matches another region (`ALL` / `MASKED`) |
| `data(target, path)`                        | The data target has something at the NBT path      |
| `dimension(dimension)`                      | The execution is in the given dimension            |
| `entity(target)`                            | The selector matches at least one entity           |
| `function(function)`                        | A function returns a non-zero value                |
| `items(source, slots, predicate)`           | Items in a container's slots match a predicate     |
| `loaded(pos)`                               | The chunk at `pos` is fully loaded                 |
| `predicate(...)`                            | A predicate passes (by id, name, or inline block)  |
| `score(target, obj, range)`                 | A score matches an int range                       |
| `score(target, obj, src, srcObj, relation)` | Two scores compare with a relation                 |
| `stopwatch(id, range)`                      | A stopwatch has elapsed within a range (ms)        |

### Comparing scores fluently

Inside a condition block, `score(target, objective)` returns a handle with the full set of infix comparison operators:

| Kotlin DSL                                        | Generated syntax                  |
|---------------------------------------------------|-----------------------------------|
| `score(self(), "points") equalTo 10`              | `if score @s points matches 10`   |
| `score(self(), "points") greaterThan 10`          | `if score @s points > 10`         |
| `score(self(), "points") greaterThanOrEqualTo 10` | `if score @s points >= 10`        |
| `score(self(), "points") lessThan 10`             | `if score @s points < 10`         |
| `score(self(), "points") lessThanOrEqualTo 10`    | `if score @s points <= 10`        |
| `score(self(), "points") matches 1..5`            | `if score @s points matches 1..5` |
| `score(self(), "a") equalTo score(self(), "b")`   | `if score @s a = @s b`            |

The relation operators also accept another score handle to compare two scores directly:

```kotlin
execute {
	ifCondition {
		val coins = score(self(), "coins")

		coins greaterThanOrEqualTo 10  // if score @s coins matches 10..
		coins matches 1..5             // if score @s coins matches 1..5

		val lives = score(self(), "lives")
		coins greaterThan lives        // if score @s coins > @s lives
	}

	run {
		say("rich and winning")
	}
}
```

### Referencing predicates directly

If you already have a [Predicate](/docs/data-driven/predicates), pass it straight to `ifCondition`:

```kotlin
val isRaining = predicate("is_raining") {
	weatherCheck(raining = true, thundering = false)
}

execute {
	ifCondition(isRaining)
	run { say("It is raining") }
}
```

You can also write a one-off inline predicate without registering a file:

```kotlin
execute {
	ifCondition {
		predicate {
			randomChance(0.5f)
		}
	}
	run { say("Heads") }
}
```

## Stores: capturing the result

`storeResult` writes the chained command's numeric **return value**; `storeSuccess` writes `1`/`0` for whether it
succeeded. Both can target a score, storage, entity NBT, block NBT, or a boss bar.

```kotlin
function("count_players") {
	execute {
		storeResult {
			score(literal("#total"), "players") // store the count into a fake player's score
		}
		run {
			// `data get` returns the player count via the @a selector trick / entity count
			data(storage("state", "my_pack")) { get("player_count") }
		}
	}
}
```

Store destinations:

| Destination | Builder call                          |
|-------------|---------------------------------------|
| Score       | `score(target, objective)`            |
| Storage     | `storage(target, path, type, scale)`  |
| Entity NBT  | `entity(target, path, type, scale)`   |
| Block NBT   | `block(pos, path, type, scale)`       |
| Boss bar    | `bossBarValue(id)` / `bossBarMax(id)` |

The `type` is a [`DataType`](/docs/concepts/data-storage) (`BYTE`, `SHORT`, `INT`, `LONG`, `FLOAT`, `DOUBLE`) and
`scale`
is a multiplier applied before writing.

## The run clause

`run` is always last. It accepts three forms:

```kotlin
// 1. Inline block - a single command is inlined, multiple commands become a generated function
execute {
	at(self())
	run {
		say("inline")
	}
}

// 2. An existing function
val reward = function("give_reward") {
	give(self(), Items.DIAMOND)
}

execute {
	ifCondition { score(self(), "coins", rangeOrInt(100)) }
	run(reward)
}

// 3. A newly named generated function
execute {
	asTarget(allPlayers())
	run("welcome") {
		say("Welcome!")
	}
}
```

If you omit `run` entirely, Kore emits the bare `execute ...` chain (useful when the last subcommand, like `summon`,
already has an effect).

## Ordering matters

Subcommands apply in the order you call them, exactly like vanilla. `asTarget` then `at` is different from `at` then
`asTarget`. Keep `run` last - anything after a `run` in the same block replaces the previous run.

## See also

- [Runtime Logic](/docs/concepts/runtime-logic) - why execute is the backbone of in-game logic
- [Predicates](/docs/data-driven/predicates) - reusable conditions for `ifCondition`
- [Commands](/docs/commands/commands) - the full command reference
