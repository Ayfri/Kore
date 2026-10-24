---
root: .components.layouts.MarkdownLayout
title: Kore Optimization Passes - Prune Dead Functions From Your Datapack
nav-title: Optimization
description: Run whole-pack optimization passes before Kore writes your datapack, pruning dead functions, shortening execute chains and sorting selectors, or plug in your own.
keywords: kore optimization, datapack dead code, prune empty mcfunction, datapack size, minecraft datapack optimization, kore configuration passes
date-created: 2026-09-05
date-modified: 2026-10-24
routeOverride: /docs/guides/optimization
position: 8
---

# Optimization passes

Kore already optimizes while you build: a single-command `run { }` block is inlined straight into its `execute` chain,
an empty block generates no function at all, and two generated functions with the same body are merged. Those are
*local* decisions, taken with only the surrounding statement in view.

Optimization passes are the opposite: they run once, on the finished pack, right before it is written, when every
function, tag and resource is known. Every output path runs them, `generate()`, `generateZip()`, `generateJar()`,
`generateZipBytes()` and `exportAsStrings()` alike. They are disabled by default, since a pass deletes content.

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
`verbose = false` to silence them. The lines go through `KoreLogger`, see [Configuration](/docs/guides/configuration#logging)
to route them to your own logger.

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

A call is only removed when removing it changes nothing. Lines carrying `store`, `summon`, `on`, `return` or an
`if function` condition keep their call, because the `execute` chain itself has an effect there, and macro lines
(starting with `$`) are left alone. A function mentioned by any line that isn't a removable call (`schedule function`,
`return run function`, a kept call) is kept too, so no line ever points at a missing function.

Pruning repeats until nothing changes, so a function left empty by the removal of its own calls is pruned in turn:

```kotlin
val empty = function("empty") {}
val relay = function("relay") { function(empty) }
function("caller") { function(relay) }
// all three functions are gone
```

## `simplify-execute-chains`

Rewrites every `execute` chain into the shortest form running the exact same command.

- `execute run <command>` loses its chain entirely, and `run execute` merges into one chain:
  `execute as @a run execute at @s run say hi` becomes `execute as @a at @s run say hi`.
- An `as @s` clause preceded by another `as` is dropped, since the executor it re-selects is already the current one.
- A clause repeated right after itself is dropped when running it twice changes nothing, so `at @s at @s` collapses
  while `at @e[type=pig] at @e[type=pig]` stays (the second one forks over every pig again), and so does
  `positioned ~ ~1 ~ positioned ~ ~1 ~` (the second one moves one block further).
- `if entity` and `unless entity` on `@e` or `@a` only test whether something matches, so they lose their `sort` and
  get `limit=1`, which lets the game stop scanning at the first match instead of collecting and sorting every entity.
- `if data <target> <path>` guarding a `data remove` of the same path is dropped, since the removal fails on its own
  when the path is missing. A chain carrying `store` keeps it, the stored result being different.

```
execute if entity @e[type=zombie,sort=nearest] run say near
execute if entity @e[type=zombie,limit=1] run say near

execute if data storage ns:s queue[0] run data remove storage ns:s queue[0]
data remove storage ns:s queue[0]
```

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
`summon`, `store`), and only into an `@a`, `@e` or `@s` selector carrying neither `scores` nor `limit`: a limit picks
its entities before the hoisted condition would filter them, and `@p`, `@r` and `@n` carry an implicit `limit=1`.
A second condition on an objective already hoisted stays a condition, since `scores` keeps only one range per
objective.

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
it. Pruning a function re-checks the generated functions it called, so a chain of generated functions calling each
other collapses in one run. Two of them calling each other are kept, since each still references the other.

Every pass reading references (`prune-empty-functions`, `dedupe-functions` and this one) indexes the whole pack once,
matching whole `namespace:path` tokens: `ns:foo` never counts as a mention of it inside `ns:foo/bar` or `xns:foo`.

## `warn-unreachable-code`

Reports the commands sitting after an unconditional `return` in the same function, which the game never runs. Only a
top-level `return` ends the function for sure, so a `return` behind an `execute` chain or on a macro line is ignored.
Nothing is removed: dead code after a `return` is almost always a mistake in the surrounding logic rather than
something to silently drop.

## Opt-in passes

These passes ship with Kore but aren't in the default list: add them with `this += ...`. They trade a change in the
shape of the pack for what the game may save, so they stay off until you want them. In game, a context provider read from a
file measured 0 to 25 % cheaper than the same inline text on big trees and equal on small ones, and no measurement exists for predicates.

There are two of them, `hoist-context-providers` and `hoist-inline-predicates`.

### `hoist-context-providers`

Moves the inline [context providers](/docs/data-driven/loot-tables#context-providers) repeated across the pack into
`context_int_provider` and `context_float_provider` files, and points the lines at them:

```kotlin
configuration {
	optimization {
		this += HoistContextProvidersPass()
	}
}

function("a") { compute(self()) { score("kills", EntityTarget.TARGET_ENTITY) + 1 } }
function("b") { data(storage("foo")).compute("bar", self()) { score("kills", EntityTarget.TARGET_ENTITY) + 1 } }
```

```
compute entity @s integer my_pack:generated_scopes/provider_28a268a6
data modify storage minecraft:foo bar set compute entity @s integer my_pack:generated_scopes/provider_28a268a6
```

The provider is written once, in `data/my_pack/context_int_provider/generated_scopes/provider_28a268a6.json`. A
provider is hoisted when it is the whole provider argument of a `compute` command or of a `data modify ... compute`
source, written inline in at least `minUses` function lines (`HoistContextProvidersPass(minUses = 3)`, `2` by default).
Constants and references stay as they are, an int and a float provider never share a file, and the scale of a float
`compute` stays on the line. Providers nested in a resource (a loot table, an item modifier) and lines starting with
`$` (macros) are left alone.

The file name is `<generated functions folder>/provider_<hash>`, the hash being computed from the provider text, so the
same pack gives the same names on every build, on the JVM and on JS. When two providers have the same hash, the second
one stays inline rather than sharing the file.

### `hoist-inline-predicates`

The same move for the inline predicates of `execute` conditions, into `predicate` files:

```kotlin
configuration {
	optimization {
		this += HoistInlinePredicatesPass()
	}
}

function("a") { execute { ifCondition { predicate { randomChance(0.5f) } }; run { say("hi") } } }
function("b") { execute { unlessCondition { predicate { randomChance(0.5f) } }; run { say("bye") } } }
```

```
execute if predicate my_pack:generated_scopes/predicate_3c2b6659 run say hi
execute unless predicate my_pack:generated_scopes/predicate_3c2b6659 run say bye
```

The predicate is written once, in `data/my_pack/predicate/generated_scopes/predicate_3c2b6659.json`, whatever the
`if` or `unless` it follows. `minUses` (`2` by default) counts how many times the same inline predicate is written in
the function lines of the pack, and one line holding two different predicates hoists each of them. A reference to a
predicate stays as it is. Predicates nested in a resource (a loot table, an advancement, an item modifier) and lines
starting with `$` (macros) are left alone. Names follow the rule of `hoist-context-providers`.

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
