---
root: .components.layouts.MarkdownLayout
title: State Delegates
nav-title: State Delegates
description: Kotlin property delegates that map scoreboard objectives or NBT storage to simple var properties with the Kore helpers module.
keywords: minecraft, datapack, kore, helpers, state, delegate, scoreboard, storage, nbt, property
date-created: 2026-03-03
date-modified: 2026-04-01
routeOverride: /docs/helpers/state-delegates
---

# State Delegates

Kotlin property delegates that map scoreboard objectives or [NBT storage](https://minecraft.wiki/w/Commands/data)
paths to simple `var` properties. Writing to the property emits the corresponding Minecraft command.

This helper reduces repetitive boilerplate in command-generation code. You write Kotlin that looks like state mutation,
while Kore still emits explicit vanilla commands underneath.

The two main delegate types are:

- `ScoreboardDelegate` for integer scoreboard-backed state.
- `StorageDelegate<T>` for NBT-backed storage paths.

## Scoreboard delegate

```kotlin
function("mana_system") {
	var mana by player.scoreboard("mana_obj", default = 100)
	mana = 80  // emits: /scoreboard players set <player> mana_obj 80
}
```

When the property is read during code generation, the delegate returns the compile-time `default` value. The real
runtime value still lives in the scoreboard, so you should think of the delegate as a concise **command emitter**, not a
live synchronized Kotlin variable.

The objective is created lazily the first time the property is accessed or assigned, and it uses `dummy` by default.

If you need relative changes instead of absolute sets, use the paired scoreboard entity handle:

```kotlin
function("mana_delta") {
	val manaObj = player.scoreboardEntity("mana_obj")
	manaObj += 10
	manaObj -= 20
}
```

`scoreboardEntity(...)` returns a `ScoreboardEntity`, which exposes the same intent in command form:

- `set(value)` for absolute updates.
- `add(value)` and `remove(value)` for relative updates.
- `plusAssign` / `minusAssign` as Kotlin operator sugar for those relative updates.

## Storage delegate

```kotlin
function("storage_demo") {
	var customTag by player.storage("my_namespace:data", "customTag", default = "hello")
	customTag = "world"  // emits: /data modify storage my_namespace:data customTag set value "world"
}
```

`storage(...)` accepts `Int`, `Float`, `String`, and `Boolean` directly; other values are stringified before being
written.

## Mixing delegates with other helpers

State delegates become more useful when you mix them with other command helpers in the same function. A common pattern
is to keep an integer on a scoreboard for arithmetic, while richer UI or session state stays in storage:

```kotlin
function("combat_state") {
  val player = entity()
  var combo by player.scoreboard("combo", default = 0)
  val comboScore = player.scoreboardEntity("combo")
  var phase by player.storage("my_namespace:state", "combat.phase", default = "idle")
  var shieldReady by player.storage("my_namespace:state", "combat.shield_ready", default = false)

  combo = 1
  comboScore += 4
  phase = "charged"
  shieldReady = true

  debug("Combat state updated")
}
```

This emits concise vanilla commands while keeping your Kotlin code expressive:

- `combo` stays easy to reuse with other scoreboard-based helpers such as cooldowns, timers, selectors, or math.
- `phase` and `shieldReady` live in storage, which fits better for descriptive or boolean state.
- `scoreboardEntity(...)` keeps relative changes (`+=`, `-=`) readable next to direct assignments.

You can also combine scoreboard-backed delegates with selector-heavy flows. For example, use a delegated score for the
canonical state, then feed the same objective into `execute if score`, inventory listeners, or scheduler callbacks.

If you want a condition like `if score == 45`, you can now keep the delegate itself around and feed it directly to a
helper. Internally,
these helpers lean on the same `ExecuteCondition.score(...)` and `Relation` primitives as the regular `execute` DSL, so
the generated syntax
stays aligned with the rest of Kore.

```kotlin
function("combo_ready") {
	val player = entity()
	val combo = player.scoreboard("combo", default = 0)
	var comboValue by combo

	comboValue = 45

	runIf(combo equalTo 45) {
		say("Combo is exactly 45")
	}
}
```

This still generates an `execute if score <target> combo matches 45 run ...` command, but without manually opening an
`execute` block every time. In practice, `equalTo(45)` is the most readable way to express an exact score check.

If you want a quick reference for every comparison helper available with delegated scores, they map directly to the
same scoreboard comparisons as the `execute` DSL:

| Kotlin helper               | Generated score comparison                                     |
|-----------------------------|----------------------------------------------------------------|
| `equalTo(...)`              | `matches <value>` for literal values, or `=` for another score |
| `notEqualTo(...)`           | `unless score ... matches <value>` style negation              |
| `greaterThan(...)`          | `>`                                                            |
| `greaterThanOrEqualTo(...)` | `>=`                                                           |
| `lessThan(...)`             | `<`                                                            |
| `lessThanOrEqualTo(...)`    | `<=`                                                           |

```kotlin
function("combo_comparisons") {
  val player = entity()
  val combo = player.scoreboard("combo", default = 0)
  val threshold = player.scoreboard("threshold", default = 10)

  runIf(combo equalTo 10) { say("combo == 10") }
  runIf(combo notEqualTo 10) { say("combo != 10") }
  runIf(combo greaterThan 10) { say("combo > 10") }
  runIf(combo greaterThanOrEqualTo 10) { say("combo >= 10") }
  runIf(combo lessThan 10) { say("combo < 10") }
  runIf(combo lessThanOrEqualTo 10) { say("combo <= 10") }

  runIf(combo greaterThan threshold) { say("combo > threshold") }
}
```

You can also compare two delegated scores directly:

```kotlin
function("combo_threshold") {
	val player = entity()
	val combo = player.scoreboard("combo", default = 0)
	val threshold = player.scoreboard("threshold", default = 0)
	var comboValue by combo
	var thresholdValue by threshold

	comboValue = 45
	thresholdValue = 45

	runIf(combo equalTo threshold) {
		say("Combo reached the threshold")
	}
}
```

That form emits `execute if score <target> combo = <target> threshold run ...`, which is useful when both the current
value and the threshold are computed elsewhere in your datapack.

For repeated checks, the same delegate can drive higher-level control flow helpers:

```kotlin
function("combo_loops") {
	val player = entity()
	val combo = player.scoreboard("combo", default = 0)
	val repeats = player.scoreboard("repeats", default = 0)
	var comboValue by combo
	var repeatCount by repeats

	comboValue = 3

	runWhile(combo greaterThan 0, name = "combo_while") {
		say("Combo loop")
		combo.scoreboardEntity() -= 1
	}

	comboValue = 9
	repeatCount = 2
	repeatScore(combo, counter = repeats, name = "combo_repeat") {
		say("Repeat once per score point")
	}
}
```

- `runIf(...)` wraps a single `execute if score ... run function ...` call.
- `runWhile(...)` generates a recursive helper function that reruns itself while the score condition stays true.
- `repeatScore(...)` is a small `for`-style helper built on top of `runWhile(...)`: by default it decrements the same
  delegated score, but you can pass `counter = ...` to keep the main score intact and consume another delegated score as
  the loop counter instead.

## When to use which delegate

- Use **`scoreboard(...)`** for integers that must participate in score comparisons, timers, cooldowns, or arithmetic.
- Use **`storage(...)`** for richer state that fits naturally in NBT-backed data trees.
- Use **`scoreboardEntity(...)`** when you want concise `+=` / `-=` style operations instead of direct assignment.
- Mix **`scoreboard(...)`** and **`storage(...)`** when you need both arithmetic-friendly counters and descriptive
  session data in the same workflow.

## API summary

| Function / type         | Purpose                                                                    |
|-------------------------|----------------------------------------------------------------------------|
| `scoreboard(...)`       | Create a scoreboard-backed delegate for an entity.                         |
| `scoreboardEntity(...)` | Create a score handle for relative arithmetic.                             |
| `runIf(...)`            | Run a block when a delegated score condition matches.                      |
| `runWhile(...)`         | Re-run a block while a delegated score condition stays true.               |
| `repeatScore(...)`      | Run a block once per score point, with an optional separate counter score. |
| `storage(...)`          | Create an NBT storage-backed delegate.                                     |
| `ScoreboardDelegate`    | Lazy scoreboard delegate implementation plus score conditions.             |
| `StorageDelegate<T>`    | Generic storage delegate implementation.                                   |

## See also

- [Scoreboard Math](/docs/helpers/scoreboard-math) – Feed delegated scoreboard values into trigonometric or algebraic
  helpers.
- [Scheduler](/docs/helpers/scheduler) – Pair delegated state with delayed or repeating helper callbacks.
- [Cooldowns](/docs/oop/cooldowns) – A concrete scoreboard-based gameplay system where delegated state can stay concise.
- [Scoreboards](/docs/oop/scoreboards) – Broader patterns for organizing objectives and player state.
