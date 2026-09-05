---
root: .components.layouts.MarkdownLayout
title: Kore Optimization Passes - Prune Dead Functions From Your Datapack
nav-title: Optimization
description: Run whole-pack optimization passes before Kore writes your datapack, pruning dead functions, shortening execute chains and sorting selectors, or plug in your own.
keywords: kore optimization, datapack dead code, prune empty mcfunction, datapack size, minecraft datapack optimization, kore configuration passes
date-created: 2026-09-05
date-modified: 2026-09-05
routeOverride: /docs/guides/optimization
position: 8
---

# Optimization passes

Kore already optimizes while you build: a single-command `run { }` block is inlined straight into its `execute` chain,
an empty block generates no function at all, and two generated functions with the same body are merged. Those are
*local* decisions, taken with only the surrounding statement in view.

Optimization passes are the opposite: they run once, on the finished pack, right before it is written, when every
function, tag and resource is known. They are disabled by default, since a pass deletes content.

## Enabling them

```kotlin
dataPack("mypack") {
	configuration {
		optimization()
	}

	// ... rest of datapack code
}
```

Writing the `optimization { }` block enables the passes, and every built-in pass runs in order. Each pass that changed
something prints one summary line, `[kore:prune-empty-functions] pruned 12 empty functions and 4 calls to them`. Set
`verbose = false` to silence them.

```kotlin
configuration {
	optimization {
		verbose = false
	}
}
```

The built-in passes run in this order, each one feeding the next: `prune-empty-functions`,
`simplify-execute-chains`, `hoist-conditions-into-selectors`, `reorder-selector-arguments`, `dedupe-functions`,
`prune-unreferenced-generated-functions`, `warn-unreachable-code`.

## `prune-empty-functions`

Removes user functions that contain no command, then removes the calls made to them. Comment-only functions count as
empty, since they do nothing in game.

A function is kept when its id still appears in any resource, so a function listed in a function tag, used as an
advancement reward or targeted by an item modifier survives even while empty.

A call is only removed when removing it changes nothing. Lines carrying `store`, `summon`, `on` or `return` keep their
call, because the `execute` chain itself has an effect there, and macro lines (starting with `$`) are left alone.

Pruning repeats until nothing changes, so a function left empty by the removal of its own calls is pruned in turn:

```kotlin
val empty = function("empty") {}
val relay = function("relay") { function(empty) }
function("caller") { function(relay) }
// all three functions are gone
```

## `simplify-execute-chains`

Rewrites every `execute` chain into the shortest form running the exact same command.

- `execute run <command>` loses its chain entirely.
- An `as @s` clause preceded by another `as` is dropped, since the executor it re-selects is already the current one.
- A clause repeated right after itself is dropped when running it twice cannot fork the execution context, so
  `at @s at @s` collapses while `at @e[type=pig] at @e[type=pig]` stays, the second one forking over every pig again.

A leading `as @s` is kept: a function called from a function tag has no executor, so the chain must still fail there.
Macro lines, and chains whose clauses contain a quoted string, are left alone.

## `hoist-conditions-into-selectors`

Moves the score conditions testing the executor into the selector that picked it:

```
execute as @e[type=marker] if score @s timer matches 1 run say hi
execute as @e[type=marker,scores={timer=1}] run say hi
```

The second form builds one execution context per matching entity instead of one per marker, and runs one command node
less. The range syntax is identical on both sides, so the rewrite is a move, not a translation.

A condition is only hoisted while nothing between the `as` and the condition can change the executor (`as`, `on`,
`summon`, `store`), and only into a selector that carries no `scores` argument yet.

## `reorder-selector-arguments`

The game tests selector arguments in the order they are written, so `@e[nbt={...},type=marker]` deserializes the NBT of
every entity in range before checking the type, while `@e[type=marker,nbt={...}]` reads it for markers only. The result
set is the same either way, which makes the reordering free. The pass sorts them cheapest first, roughly
`type`, `tag`, `team`, `scores`, `level`, `gamemode`, `name`, position and distance, then `advancements`, `predicate`
and `nbt`.

## `dedupe-functions`

Merges functions sharing the exact same body and redirects the calls to the survivor. `addGeneratedFunction` already
merges two generated functions built with identical lines, but only at the moment they are created, and two of them
become identical again once `simplify-execute-chains` rewrites their lines.

Hand-written functions stay untouched by default, since their names are part of what the pack exposes and something
outside the pack may call them. Opt in with `this += DedupeFunctionsPass(includeUserFunctions = true)` after removing
the default instance. A duplicate is also kept when a resource mentions its id, since a function tag or an advancement
reward holds it in a typed field the pass cannot rewrite.

## `prune-unreferenced-generated-functions`

Generated functions only exist because a DSL construct created one, so they are unreachable as soon as the line calling
them is gone, which happens when a later edit of the same `DataPack` drops the callsite or when an earlier pass removes
it. Pruning repeats until it converges, so a chain of generated functions calling each other collapses in one run. Two
of them calling each other are kept, since each still references the other.

## `warn-unreachable-code`

Reports the commands sitting after an unconditional `return` in the same function, which the game never runs. Only a
top-level `return` ends the function for sure, so a `return` behind an `execute` chain or on a macro line is ignored.
Nothing is removed: dead code after a `return` is almost always a mistake in the surrounding logic rather than
something to silently drop.

## Writing your own pass

A pass is a `DataPackPass`, mutating the pack in place and reporting what it did:

```kotlin
data object PrefixCommentPass : DataPackPass {
	override val name = "prefix-comment"

	override fun run(dataPack: DataPack): PassResult {
		dataPack.functions.forEach { it.lines.add(0, "# built with Kore") }
		return PassResult(dataPack.functions.size, "commented ${dataPack.functions.size} functions")
	}
}

configuration {
	optimization {
		this += PrefixCommentPass
	}
}
```

`this -= PruneEmptyFunctionsPass` drops a built-in pass, and `passes` can be replaced outright to control the exact
list and its order.

## What to read next

- [Configuration](/docs/guides/configuration) - the rest of the `configuration { }` block
- [Functions](/docs/commands/functions) - what the passes operate on
