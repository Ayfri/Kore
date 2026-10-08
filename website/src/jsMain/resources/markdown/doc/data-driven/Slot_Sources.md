---
root: .components.layouts.MarkdownLayout
title: Slot Sources
nav-title: Slot Sources
description: Select inventory slots in Minecraft loot tables, /item and /execute with Kore's type-safe slot source DSL and slot source files.
keywords: minecraft, datapack, kore, slot sources, slot_source, item command, execute if items, execute if slots, loot tables
date-created: 2026-10-08
date-modified: 2026-10-08
routeOverride: /docs/data-driven/slot-sources
---

# Slot Sources

A slot source selects inventory slots: the slots of an entity or a block entity, the items stored inside an item, or a filtered, limited or
merged mix of them. The same builders are used in four places:

| Where                                | Kore                                       |
|--------------------------------------|--------------------------------------------|
| Slot source file                     | `slotSource("name") { ... }`               |
| `slots` loot table entry             | `slots { slotSources { ... } }`            |
| `/item` source and destination slots | `items.replace(self(), slots, ...)`        |
| `/execute if items` and `if slots`   | `ifCondition { slots(self(), slots) }`     |

Several sources in one block are concatenated, a single one is written as an object and several as an array.

Reference: [Slot source](https://minecraft.wiki/w/Slot_source)

## Slot Source Files

`slotSource` writes `data/<namespace>/slot_source/<name>.json` and returns its `SlotSourceArgument`:

```kotlin
val hands = slotSource("hands") {
	slotRange(WEAPON.all())
}
```

```json
{
	"type": "minecraft:slot_range",
	"slots": "weapon.*"
}
```

Another slot source points to a file with `reference(hands)`, which writes `{"type": "minecraft:reference", "name": "<namespace>:hands"}`.

## In Commands

`/item` and `/execute if items|slots` take a `SlotsArgument`, which is one of:

- a slot or a range, written as a slot string: `ARMOR.CHEST` is `armor.chest`, `HOTBAR` and `HOTBAR.all()` are `hotbar.*`;
- a slot source file, like `hands` above;
- an inline slot source, `inlineSlotSource { ... }`, written as SNBT.

```kotlin
function("give_kit") {
	items.fill(self(), HOTBAR, Items.BREAD) // item fill entity @s hotbar.* with minecraft:bread
	items.override(self(), hands, Items.STONE_SWORD) // item override entity @s my_pack:hands with minecraft:stone_sword

	execute {
		ifCondition { slots(self(), inlineSlotSource { slotRange(ARMOR) }) }
		run { say("Wearing armor") }
	}
}
```

`items.replace`, `items.fill` and `items.override` spread the source items over the destination slots differently, here with destination
slots `[12345]` and source items `(ABC)`:

| Function   | Result    | Destination slots left over |
|------------|-----------|-----------------------------|
| `replace`  | `[ABC45]` | untouched                   |
| `fill`     | `[ABCAB]` | filled by repeating sources |
| `override` | `[ABC__]` | emptied                     |

`/execute if slots` counts the selected slots that exist on the target, `/execute if items` the selected slots whose item matches the
predicate. Slot sources used by commands are evaluated with the `command_slot_source` loot context.

## Slot Source Types

### Slot Range

Selects the slots of a slot string from an inventory. Without an origin, it reads the loot context's container: the target of the command, or
the inventory a loot table runs on.

```kotlin
slotSources {
	// Every hotbar slot of the context's container
	slotRange(HOTBAR)

	// One slot of the block entity
	slotRange(SlotSourceOrigin.BLOCK_ENTITY, ARMOR.HEAD)

	// A raw slot string from the looting entity
	slotRange(SlotSourceOrigin.THIS, "container.*")
}
```

Available origins: `ATTACKING_ENTITY`, `BLOCK_ENTITY`, `CONTAINER`, `DIRECT_ATTACKER`, `INTERACTING_ENTITY`, `LAST_DAMAGE_PLAYER`,
`TARGET_ENTITY`, `THIS`.

### Contents

Selects the slots stored in the inventory component of the items selected by `slotSource`:

```kotlin
slotSources {
	contents(InventoryComponentType.CONTAINER) {
		slotSource {
			slotRange(SlotSourceOrigin.BLOCK_ENTITY, "container.*")
		}
	}
}
```

Available component types: `BUNDLE_CONTENTS`, `CHARGED_PROJECTILES`, `CONTAINER`.

### Empty

An empty selection containing no slots:

```kotlin
slotSources {
	empty()
}
```

### Filtered

Keeps only the selected slots whose item matches `itemFilter`:

```kotlin
slotSources {
	filtered {
		slotSource {
			slotRange(SlotSourceOrigin.THIS, HOTBAR)
		}

		itemFilter {
			count = rangeOrInt(16..64)
		}
	}
}
```

### Group

Merges several slot sources into one:

```kotlin
slotSources {
	group {
		slotRange(SlotSourceOrigin.THIS, HOTBAR)
		empty()
	}
}
```

### Limit Slots

Keeps the first `limit` selected slots:

```kotlin
slotSources {
	limitSlots(5) {
		slotSource {
			slotRange(SlotSourceOrigin.THIS, HOTBAR)
		}
	}
}
```

### Reference

Selects the slots of a slot source file:

```kotlin
slotSources {
	reference(hands)
}
```

## See Also

- [Loot Tables](/docs/data-driven/loot-tables) - The `slots` entry
- [Execute](/docs/commands/execute) - The `items` and `slots` conditions
