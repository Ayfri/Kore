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

## When to use which delegate

- Use **`scoreboard(...)`** for integers that must participate in score comparisons, timers, cooldowns, or arithmetic.
- Use **`storage(...)`** for richer state that fits naturally in NBT-backed data trees.
- Use **`scoreboardEntity(...)`** when you want concise `+=` / `-=` style operations instead of direct assignment.

## API summary

| Function / type         | Purpose                                            |
|-------------------------|----------------------------------------------------|
| `scoreboard(...)`       | Create a scoreboard-backed delegate for an entity. |
| `scoreboardEntity(...)` | Create a score handle for relative arithmetic.     |
| `storage(...)`          | Create an NBT storage-backed delegate.             |
| `ScoreboardDelegate`    | Lazy scoreboard delegate implementation.           |
| `StorageDelegate<T>`    | Generic storage delegate implementation.           |

## See also

- [Scoreboard Math](/docs/helpers/scoreboard-math) – Feed delegated scoreboard values into trigonometric or algebraic
  helpers.
- [Cooldowns](/docs/oop/cooldowns) – A concrete scoreboard-based gameplay system where delegated state can stay concise.
- [Scoreboards](/docs/oop/scoreboards) – Broader patterns for organizing objectives and player state.
