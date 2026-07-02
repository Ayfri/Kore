---
root: .components.layouts.MarkdownLayout
title: NBTs
nav-title: NBTs
description: Work with Minecraft NBT data in Kore using a shared Kotlin DSL across commands, predicates, and chat components. Includes SNBT helpers, path access, and reuse patterns.
keywords: minecraft, datapack, kore, nbt, snbt, knbt, chat components, predicates, data command
date-created: 2026-05-29
date-modified: 2026-05-29
routeOverride: /docs/concepts/nbts
---

# NBTs

NBT (Named Binary Tag) is Minecraft's structured data format. Kore uses it anywhere vanilla expects embedded NBT or
SNBT,
such as:

- command payloads like `summon`, `data merge`, or [storage writes](/docs/concepts/data-storage)
- chat components that read values from blocks, entities, or storage
- predicate sub-structures that expose an `nbt { ... }` block
- helpers and domain objects that serialize themselves with `toNbt()`

Kore's NBT support is built on top of [`knbt`](https://github.com/BenWoodworth/knbt), so the same builder style is
reused
throughout the DSL.

An NBT **compound** is the object/map-shaped tag in NBT: a group of named entries such as `CustomName`, `Health`, or
`display`. In Kore, `nbt { ... }` builds that compound structure.

## Core builder

The main entry point is `nbt { ... }`, which creates an `NbtCompound`.

```kotlin
val customData = nbt {
	this["CustomName"] = "\"Hero\""
	this["Health"] = 20
	this["Invulnerable"] = true
	this["Tags"] = nbtListOf("kore", "example")

	this["Weapon"] = nbt {
		this["id"] = "minecraft:diamond_sword"
		this["Count"] = 1.toByte()
	}
}
```

This pattern is used by Kore in most places that accept raw NBT.
When you write compounds manually, it is often worth sorting keys by name to keep large payloads easier to scan and
diff.

## Common ways to build values

Inside an `nbt { ... }` block, the most common pattern is assigning with `this["key"] = value`.

### Primitive values

```kotlin
val payload = nbt {
	this["cooldown"] = 40.toShort()
	this["count"] = 1.toByte()
	this["enabled"] = true
	this["level"] = 3
	this["name"] = "Kore"
	this["seed"] = 1234L
	this["speed"] = 0.5
}
```

Vanilla key names are not perfectly consistent across Minecraft versions. Mojang used `PascalCase` for most older keys,
then introduced some newer keys in `camelCase` for a while, and more recent additions often use `snake_case`. The
migration is slow, so real-world NBT frequently mixes conventions in the same compound.

Because of that, I advise you to not guess key names. Always verify them against the Minecraft Wiki, or inspect the
exact live data
with `/data get entity ...`, `/data get block ...`, or `/data get storage ...` in-game.

### Nested compounds

Use another `nbt { ... }` block for a nested compound when one key contains another group of named values:

```kotlin
val nested = nbt {
	this["display"] = nbt {
		this["Name"] = "\"Treasure\""
	}
}
```

### Lists

Use `nbtListOf(...)` for a homogeneous NBT list when you already know the elements:

```kotlin
val listExample = nbt {
	this["Pos"] = nbtListOf(0.0, 64.0, 0.0)
	this["Tags"] = nbtListOf("boss", "phase_1")
}
```

`knbt` lists are typed, so a single NBT list must contain the same kind of values.

If you want to build a list incrementally, use `nbtList { ... }`:

```kotlin
val passengers = nbtList {
	addNbtCompound {
		this["CustomName"] = "\"Left Guard\""
		this["id"] = "minecraft:armor_stand"
	}

	addNbtCompound {
		this["CustomName"] = "\"Right Guard\""
		this["id"] = "minecraft:armor_stand"
	}
}
```

This is especially useful for lists of compounds, where each element is itself a small NBT object.

### Raw SNBT when needed

Most of the time you should prefer the typed builders above. If you need a hand-written SNBT fragment, Kore also exposes
helpers such as `stringifiedNbt(...)` for contexts that accept SNBT text directly.

See [Known Issues](/docs/advanced/known-issues#nbt-and-snbt-via-knbt) for the main `knbt` limitations and trade-offs.

## Where you use NBT in Kore

Different APIs expose NBT in different ways, but the underlying builder style stays the same.

### Commands

Commands usually accept an `NbtCompound` directly.

```kotlin
function("summon_example") {
	summon(EntityTypes.ARMOR_STAND, vec3(0, 64, 0), nbt = nbt {
		this["CustomName"] = "\"Guide\""
		this["NoGravity"] = true
	})
}
```

The `data` command also uses NBT builders naturally:

```kotlin
function("storage_seed") {
	data(self()) {
		merge {
			this["CustomName"] = "\"Hero\""
			this["Invulnerable"] = true
		}
	}
}
```

If you are learning the command surface itself, see [Commands](/docs/commands/commands#data-command).

### Chat components

`NbtComponent` reads a value from block, entity, or storage NBT and displays it in chat.

```kotlin
val nameFromStorage = nbtComponent("player.name", storage("kore:ui")) {
	interpret = true
	separator = textComponent(", ")
}
```

Common properties in this context:

- `nbt`: the NBT path to read
- `interpret`: parse the resulting text as a chat component when the stored value contains component JSON
- `separator`: custom separator when the path resolves to multiple values
- source-specific arguments: block position, entity selector, or storage id

See [Chat Components](/docs/concepts/chat-components#nbtcomponent) for the full component guide.

### Predicates and sub-predicates

Some predicate builders expose an `nbt { ... }` block directly. For example, block sub-predicates can match block-entity
NBT:

```kotlin
block(Blocks.CHEST) {
	nbt {
		this["CustomName"] = "\"Loot Chest\""
	}
}
```

That mirrors the attached `Block` predicate DSL, where `fun Block.nbt(block: NbtCompoundBuilder.() -> Unit)` stores the
resulting compound in the predicate JSON.

When a predicate surface exposes `predicates { customData { ... } }` or another nested NBT-aware structure, you still
use
the same value assignment style.

See [Predicates](/docs/data-driven/predicates) for the surrounding condition DSL.

### Objects and helpers with `toNbt()`

Several Kore types can serialize themselves to NBT using `toNbt()`.

```kotlin
val entityNbt = myDisplayEntity.toNbt()
```

This is convenient when an API already knows its target NBT shape and you only need to pass the generated compound into
a
command or another builder.

Examples appear in helper docs such as [Display Entities](/docs/helpers/display-entities) and
[Mannequins](/docs/helpers/mannequins).

## Typical methods by context

Here is the short version of what you usually reach for:

| Context                    | Typical methods                                        |
|----------------------------|--------------------------------------------------------|
| Build a compound           | `nbt { ... }`                                          |
| Build a list from values   | `nbtListOf(...)`                                       |
| Build a list with a DSL    | `nbtList { ... }`, `addNbtCompound { ... }`            |
| Write entries              | `this["key"] = value`                                  |
| Nest another compound      | `this["key"] = nbt { ... }`                            |
| Reuse generated object NBT | `toNbt()`                                              |
| Read NBT into chat         | `nbtComponent(path, block/entity/storage)`             |
| Match NBT in a DSL         | context-specific `nbt { ... }` methods                 |
| Hand-write SNBT text       | `stringifiedNbt(...)` when the target API expects text |

## Practical tips

- Prefer the typed builder over raw SNBT strings whenever possible.
- Keep reusable compounds in local variables when several commands share the same payload.
- Remember that list element types must stay homogeneous.
- If you want readable generated output while iterating, enable `prettyPrint` in your configuration.
- When an API already exposes higher-level builders or `toNbt()`, prefer those over manually recreating the same shape.

## NBT vs SNBT in short

`Nbt` is the structured tag model itself: compounds, lists, strings, numbers, and the other tag types Kore builds in
memory with
`nbt { ... }`, `nbtListOf(...)`, `nbtList { ... }`, and `toNbt()`.

`Snbt` (stringified NBT) is the text form of that same data, written as a string such as
`{CustomName:"\"Hero\"",Health:20}`.

In Kore, you usually work with typed `Nbt` objects first, then let Kore serialize them when needed. Reach for SNBT
helpers such as
`stringifiedNbt(...)` when a target API specifically expects NBT as text instead of an `NbtTag` object.

## Related pages

- [Chat Components](/docs/concepts/chat-components) - includes `NbtComponent`
- [Commands](/docs/commands/commands) - especially the data command and NBT-carrying commands
- [Predicates](/docs/data-driven/predicates) - NBT-aware predicate helpers
- [Components](/docs/concepts/components) - item and custom component structures that often embed NBT-backed data
- [Known Issues](/docs/advanced/known-issues#nbt-and-snbt-via-knbt) - `knbt`-specific constraints
