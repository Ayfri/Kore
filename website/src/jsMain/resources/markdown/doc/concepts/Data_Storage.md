---
root: .components.layouts.MarkdownLayout
title: Data Storage - Runtime NBT Variables with /data Command in Kore
nav-title: Data Storage
description: Use Minecraft data storage as runtime NBT variables in Kore. Set, modify, copy, and read values with the /data command DSL. Store strings, lists, and compounds beyond integer-only scoreboards.
keywords: minecraft data storage, /data command, datapack nbt storage, data modify, data get, data merge, runtime variables minecraft, kore data, storage macros, datapack variable container
date-created: 2026-06-24
date-modified: 2026-06-24
routeOverride: /docs/concepts/data-storage
position: 3
---

# Data Storage

Data storage is Minecraft's general-purpose **runtime variable container**. Where
a [scoreboard](/docs/concepts/scoreboards)
only holds integers, storage holds any NBT value: strings, decimals, lists, and nested compounds. It is the natural
place
to keep in-game state that is not a plain number.

If you are unsure when to use Kotlin variables versus storage, read [Runtime Logic](/docs/concepts/runtime-logic)
first -
storage lives entirely at **runtime**, in the running world, not at generation time.

This page covers the storage form of the [`/data` command](https://minecraft.wiki/w/Commands/data). The same DSL also
works on entities (`data(self())`) and blocks (`data(blockPos)`); only the target changes.

## Creating a storage handle

A storage is identified by a namespaced id. Create a typed handle with `storage(...)`:

```kotlin
val state = storage("state", "my_pack") // -> my_pack:state
```

The first argument is the path, the second is the namespace (defaults to `minecraft`). Reuse the same handle everywhere
you
read or write that storage.

## Writing values

Open the `/data` DSL with `data(target) { ... }` and write with `set`:

```kotlin
function("init_state") {
	data(state) {
		set("player_name", "Steve")  // string
		set("level", 1)              // int
		set("ratio", 0.5f)           // float
		set("active", true)          // boolean
	}
}
```

Each `set("path", value)` emits a `data modify storage my_pack:state <path> set value <value>` line.

### Writing compounds and lists

For structured data, use `merge` with an NBT builder, which writes a whole compound at once:

```kotlin
function("init_player") {
	data(state) {
		merge {
			this["name"] = "Steve"
			this["level"] = 1
			this["inventory"] = nbtListOf("sword", "shield")
		}
	}
}
```

See [NBTs](/docs/concepts/nbts) for the full NBT builder DSL.

## Reading values

Use `get` to read a value (Minecraft prints it / returns it as a command result):

```kotlin
function("show_level") {
	data(state) {
		get("level")              // data get storage my_pack:state level
		get("ratio", scale = 100.0) // read and scale the numeric result
	}
}
```

The `scale` parameter multiplies a numeric result - handy for moving a value into a scoreboard with a fixed-point factor
(see [Bridging storage and scoreboards](#bridging-storage-and-scoreboards)).

## Modifying values

`modify` exposes every `/data modify` operation - `set`, `merge`, `append`, `prepend`, `insert`, and the `from`/`string`
source forms. The block receiver is `DataModifyOperation`:

```kotlin
function("update_state") {
	data(state) {
		// overwrite a path
		modify("level") { set(2) }

		// copy a value from another data source (entity, block, or storage)
		modify("position") { set(self(), "Pos") }

		// list operations
		modify("inventory") { append("bow") }
		modify("inventory") { prepend("helmet") }
		modify("inventory") { insert(1, "potion") }

		// substring of a string source
		modify("initial") { set(self(), "SelectedItem.id", 0, 1) }
	}
}
```

There are convenient shorthands for the common `set` cases:

```kotlin
data(state) {
	modify("level", 5)               // set to a literal
	modify("owner", self(), "UUID")  // copy from another target's path
}
```

## Removing values

```kotlin
function("reset_state") {
	data(state) {
		remove("inventory")
		remove("level")
	}
}
```

## Bracket shorthand

For one-off reads and writes you can skip the block and use index syntax:

```kotlin
function("quick") {
	data(state)["level"] = 5     // write
	data(state)["level"]         // read (data get)
}
```

## Bridging storage and scoreboards

Storage and scoreboards are the two runtime containers, and you often move values between them. Use
`execute store` to write a command's numeric result into either one (see [Execute](/docs/commands/execute)):

```kotlin
function("level_to_score") {
	// read storage `level` and store it into the `levels` scoreboard objective
	execute {
		storeResult {
			score(self(), "levels")
		}
		run {
			data(state) { get("level") }
		}
	}
}
```

The reverse - score into storage - is the same pattern with
`storeResult { storage(state, "level", DataType.INT, 1.0) }`.

## Macros read straight from storage

Storage is the canonical source for [macro](/docs/commands/macros) arguments. You can pass a storage path as the
arguments to a function call, and its compound becomes the macro inputs:

```kotlin
val teleport = function("teleport") {
	teleport(player(macro("name")), vec3())
}

function("run_teleport") {
	// fills $(name) from the `target` compound in storage
	function(teleport, arguments = state, path = "target")
}
```

## When to use storage vs scoreboards

| Use a **scoreboard** when...             | Use **storage** when...                             |
|------------------------------------------|-----------------------------------------------------|
| The value is a single integer            | The value is text, a decimal, a list, or a compound |
| You need fast arithmetic / comparisons   | You need to pass structured data to a macro         |
| It is per-entity (uses the score holder) | It is global or grouped state                       |
| You branch on it with `execute if score` | You branch on it with `execute if data`             |

In practice most packs use both: scoreboards for counters and math, storage for everything structured.

## See also

- [Runtime Logic](/docs/concepts/runtime-logic) - where storage fits in the compile-time vs runtime model
- [NBTs](/docs/concepts/nbts) - the NBT builder DSL used by `set` / `merge`
- [Execute](/docs/commands/execute) - `store` results into storage, branch with `if data`
