---
root: .components.layouts.MarkdownLayout
title: NBT Paths
nav-title: NBT Paths
description: Build typed Minecraft NBT paths and split one NBT tree into static values plus runtime scores with the Kore helpers module.
keywords: minecraft, datapack, kore, helpers, nbt, path, storage, execute store, resolve, score, macro
date-created: 2026-09-04
date-modified: 2026-09-04
routeOverride: /docs/helpers/nbt-paths
---

# NBT Paths

Two related helpers for the NBT half of a datapack: `NbtPath` replaces interpolated path strings with typed segments,
and `resolveNbt` writes one tree whose values mix literals and runtime scores.

## Typed paths

Every core `data` and `execute store` API takes an NBT path as a `String`, so a typo compiles. `NbtPath` builds the
path from segments instead, quoting each one only when vanilla requires it:

```kotlin
val enchantments = nbtPath("equipment") / slot / "components" / "minecraft:enchantments"
// equipment.mainhand.components."minecraft:enchantments"
```

| Operation           | Result                       |
|---------------------|------------------------------|
| `path / "name"`     | `path.name`                  |
| `path / otherPath`  | Appends every segment        |
| `path[0]`           | `path[0]`                    |
| `path.all()`        | `path[]`                     |
| `path.matching { }` | `path[{Slot:0b}]`            |
| `path.isRoot`       | `true` for the root compound |

`String.toNbtPath()` parses a path already written in Minecraft's syntax, so an existing constant can join the typed
API. It splits only on the dots that separate segments, leaving quoted keys, list indices, and compound filters intact:

```kotlin
val custom = """Items[{Slot:0b}].components."minecraft:custom_data".id""".toNbtPath()
custom.segments // [Items[{Slot:0b}], components, "minecraft:custom_data", id]
```

The helpers module adds `NbtPath` overloads to `Data.modify`, `Data.get`, `Data.remove`, `ExecuteStore.storage`,
`ExecuteStore.entity`, and `ScoreboardEntity.copyTo`, so both halves of a store-plus-modify pair share one value:

```kotlin
function("equip") {
	val level = nbtPath("equipment") / "mainhand" / "components" / "minecraft:enchantments" / "minecraft:protection"

	data(player.asSelector()).modify(level, 4)
	execute {
		storeResult { entity(player.asSelector(), level, DataType.INT, 1.0) }
		run { scoreboard.players.get(literal("#armor"), "stats") }
	}
}
```

## Resolving scores into NBT

Minecraft cannot write a score into an NBT tree directly. The vanilla shape is one `data modify ... set value` for the
literal parts, then one `execute store result` per runtime value - two halves that drift apart as the tree grows.

`resolveNbt` declares the tree once and emits both halves from it:

```kotlin
function("build_offer") {
	val price = fakePlayer("price").getScoreEntity("shop")
	val stock = fakePlayer("stock").getScoreEntity("shop")

	resolveNbt(storage("kore", "shop"), nbtPath("offer")) {
		this["item"] = "minecraft:diamond"
		this["price"] = price
		compound("meta") {
			this["featured"] = true
			this["stock"] = stock
		}
	}
}
```

```mcfunction
data modify storage kore:shop offer set value {item:"minecraft:diamond",meta:{featured:1b}}
execute store result storage kore:shop offer.price int 1.0 run scoreboard players get #price shop
execute store result storage kore:shop offer.meta.stock int 1.0 run scoreboard players get #stock shop
```

`execute store result` creates the parent compounds it needs, so a nested compound holding only scores emits no
`data modify` line of its own. A tree with no literal at all emits only the store lines.

Values accept `NbtTag` and every Kotlin primitive, a `ScoreboardEntity`, or a
[`ScoreboardDelegate`](/docs/helpers/state-delegates). Scores default to `int` with no scaling; pass a `DataType` and a
`scale` for fixed-point scores:

```kotlin
resolveNbt(storage("kore", "physics"), nbtPath("motion")) {
	// a score counting thousandths of a block becomes a double
	set("y", velocity, DataType.DOUBLE, 0.001)
}
```

Passing the root path (the default) writes the literal half with `data merge` instead of `data modify`. An `Entity`
overload targets entity NBT rather than storage.

## Feeding a macro

The usual reason to assemble a tree at runtime is calling a macro function with it, which makes `resolveNbt` and
`function ... with storage` a natural pair:

```kotlin
function("teleport_to_score") {
	val macro = storage("kore", "macro")

	resolveNbt(macro) {
		this["x"] = posX
		this["y"] = posY
		this["z"] = posZ
	}

	function("do_teleport", arguments = macro)
}
```

## See also

- [Data Storage](/docs/concepts/data-storage) - The core `data` command DSL these helpers wrap.
- [Scoreboards](/docs/oop/scoreboards) - Fake players and the score handles `resolveNbt` reads from.
- [State Delegates](/docs/helpers/state-delegates) - Delegated scores usable as `resolveNbt` values.
