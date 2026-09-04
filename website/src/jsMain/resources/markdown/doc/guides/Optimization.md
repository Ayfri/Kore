---
root: .components.layouts.MarkdownLayout
title: Kore Optimization Passes - Prune Dead Functions From Your Datapack
nav-title: Optimization
description: Run whole-pack optimization passes before Kore writes your datapack, pruning empty functions and the calls to them, and plug in your own passes.
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
