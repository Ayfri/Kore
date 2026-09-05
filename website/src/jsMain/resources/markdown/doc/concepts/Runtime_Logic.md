---
root: .components.layouts.MarkdownLayout
title: "Runtime Logic - Kotlin vs Minecraft: Variables, Conditions & Loops"
nav-title: Runtime Logic
description: "The key concept for Kore datapacks: how compile-time Kotlin maps to runtime Minecraft logic. Variables via scoreboards and storage, conditions via execute if/unless, and loops via tick functions and schedulers."
keywords: minecraft runtime logic, datapack variables, datapack conditions, datapack loops, kore runtime, datapack tick function, execute if unless, minecraft scoreboard variable, datapack programming, kore compile time
date-created: 2026-06-24
date-modified: 2026-06-24
routeOverride: /docs/concepts/runtime-logic
position: 2
---

# Runtime Logic

This is the single most important concept to understand when moving from raw datapacks (or any imperative language)
to Kore. Get this right and everything else clicks into place.

## The two worlds

When you write Kore, your code runs in **two completely separate worlds** at two different times:

|                     | Compile-time (build)                                   | Runtime (in-game)                                  |
|---------------------|--------------------------------------------------------|----------------------------------------------------|
| **When**            | When you run your `main` function to generate the pack | When Minecraft executes the generated functions    |
| **Language**        | Kotlin                                                 | Minecraft commands (`.mcfunction`)                 |
| **`val` / `var`**   | Real Kotlin variables                                  | Do not exist - use scoreboards / storage           |
| **`if` / `else`**   | Real Kotlin branches                                   | Do not exist - use `execute if` / predicates       |
| **`for` / `while`** | Real Kotlin loops                                      | Do not exist - use recursion / `schedule` / macros |
| **Knows**           | Everything in your Kotlin code                         | Only the live world state                          |

Kotlin `val`, `var`, `if`, `for`, `while` are **generation-time** tools. They decide *what commands get written into
your
datapack*. They are gone by the time the pack runs. They never look at the player, the world, or a score.

To react to the live game (a player's score, a block in the world, an entity that exists right now), you need
**runtime tools**: scoreboards, data storage, the `execute` command, predicates, and macros.

## Compile-time: Kotlin builds your commands

Standard Kotlin runs once, when you generate the pack. Use it to remove repetition and assemble functions.

```kotlin
function("setup_teams") {
	// This Kotlin `for` runs at generation time. It does NOT loop in-game.
	// It writes 3 separate command lines into the generated function.
	for (color in listOf("red", "blue", "green")) {
		scoreboard.objectives.add("kills_$color")
	}
}
```

The generated `.mcfunction` is just three flat lines:

```mcfunction
scoreboard objectives add kills_red dummy
scoreboard objectives add kills_blue dummy
scoreboard objectives add kills_green dummy
```

There is no loop left in the output. The `for` was a **code generator**, not in-game behavior. The same goes for `if`:

```kotlin
val debugMode = true

function("tick") {
	movePlayers()
	if (debugMode) {
		// Included only because `debugMode` was true AT BUILD TIME.
		// Flip it to false and this command simply isn't generated.
		say("debug: tick ran")
	}
}
```

## Runtime: variables

In-game you cannot store a value in a Kotlin `var`. The two runtime containers are **scoreboards** (for integers) and
**data storage** (for any NBT: strings, lists, compounds, decimals).

### Scoreboards - integer variables

See [Scoreboards](/docs/concepts/scoreboards) for the full command reference.

```kotlin
load {
	scoreboard.objectives.add("coins")
}

function("give_coin") {
	// runtime: read-modify-write a per-player integer
	scoreboard.players.add(self(), "coins", 1)
}
```

For arithmetic between scores, use `operation`, or reach for [Scoreboard Math](/docs/helpers/scoreboard-math) when you
need trigonometry/algebra.

### Data storage - everything else

See [Data Storage](/docs/concepts/data-storage) for the full guide. Storage holds strings, lists and compounds that
scores cannot:

```kotlin
val state = storage("state", "my_pack")

function("set_name") {
	data(state) {
		set("player_name", "Steve")
	}
}
```

## Runtime: conditions (if / else)

There is no in-game `if`. You branch with the `execute if` / `execute unless` chain (
see [Execute](/docs/commands/execute))
or by referencing a [Predicate](/docs/data-driven/predicates).

```kotlin
function("reward_rich_players") {
	// runtime: "if the player's coins >= 100, run the reward"
	execute {
		ifCondition {
			score(self(), "coins", rangeOrInt(100)) // matches 100..
		}

		run {
			say("You are rich!")
		}
	}
}
```

### Emulating if / else

Minecraft has no `else`. The cleanest pattern is two mirrored checks (`if` then `unless`), or an early `return` so the
second branch only runs when the first did not.

```kotlin
function("check_score") {
	execute {
		ifCondition { score(self(), "coins", rangeOrInt(100)) }
		run { say("rich") }
	}

	execute {
		unlessCondition { score(self(), "coins", rangeOrInt(100)) }
		run { say("poor") }
	}
}
```

For reusable, complex conditions, build a [Predicate](/docs/data-driven/predicates) once and reference it:

```kotlin
val isRaining = predicate("is_raining") {
	weatherCheck(raining = true, thundering = false)
}

function("rain_warning") {
	execute {
		ifCondition(isRaining)
		run { say("It is raining") }
	}
}
```

## Runtime: loops

There is no in-game `for` or `while`. The three runtime looping patterns are **function recursion**, **`schedule`**, and
**iterating an entity selector**.

### Recursion (loop a fixed/conditional number of times)

A function that calls itself loops once per tick step. Use a score as the counter and `execute if`/`unless` as the
guard so it stops.

```kotlin
load {
	scoreboard.objectives.add("countdown")
	scoreboard.players.set(self(), "countdown", 5)
}

val tickFunction = function("countdown_tick") {
	say("tick")
	scoreboard.players.remove(self(), "countdown", 1)

	// keep going only while countdown > 0
	execute {
		ifCondition { score(self(), "countdown", rangeOrInt(1)) } // 1..
		run(this@function) // self-recursion: call this same function again
	}
}
```

### `schedule` (loop over time)

`schedule` re-runs a function after a delay - ideal for spacing work across ticks instead of hammering every tick. See
[Time](/docs/concepts/time) for the `.ticks` / `.seconds` helpers.

```kotlin
val loop = function("spawn_wave") {
	summon(EntityTypes.ZOMBIE, vec3())
	schedule(2.seconds, this@function) // re-run myself in 2 seconds
}
```

### Iterating entities

To "loop over all players", run the body **as** each matched entity - the selector does the iteration:

```kotlin
function("heal_everyone") {
	execute {
		asTarget(allPlayers())
		run {
			effect(self()) {
				give(Effects.REGENERATION, duration = 5)
			}
		}
	}
}
```

## Macros: when text must be dynamic

A score holds a number, storage holds NBT, but sometimes you need to inject a runtime value into a part of a command
that is normally fixed text (a name, a coordinate). That is what [Macros](/docs/commands/macros) are for - runtime
string
substitution. They are not variables: you cannot do math on them, only paste them into the command text.

```kotlin
function("greet") {
	// `$(name)` is filled in from the NBT passed when the function is called
	say("Hello ${macro("name")}!")
}
```

## Mental model cheat sheet

- Need to **remove repetition while writing** the pack? Use Kotlin `for` / `if` / functions. (compile-time)
- Need to **remember a number** in-game? Scoreboard. (runtime)
- Need to **remember text / a list / decimals** in-game? Data storage. (runtime)
- Need to **branch** on live world state? `execute if` / `unless` or a predicate. (runtime)
- Need to **repeat** in-game? Recursion, `schedule`, or an entity selector. (runtime)
- Need to **paste a runtime value into command text**? Macro. (runtime)

## See also

- [Execute](/docs/commands/execute) - the runtime control-flow command
- [Data Storage](/docs/concepts/data-storage) - the runtime NBT variable container
- [Scoreboards](/docs/concepts/scoreboards) - runtime integer variables
