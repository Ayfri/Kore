---
root: .components.layouts.MarkdownLayout
title: Items
nav-title: Items
description: Object-oriented item creation, spawning, and NBT serialization with the Kore OOP module.
keywords: minecraft, datapack, kore, oop, items, item stack, summon, give, nbt, data modify
date-created: 2026-03-03
date-modified: 2026-10-22
routeOverride: /docs/oop/items
---

# Items

The OOP module keeps item usage concise by letting you build an [item stack](https://minecraft.wiki/w/Item) once and
then reuse it for giving,
spawning, or embedding it in wider entity workflows.

> [!FOOTPRINT]
> Nothing of its own: each `summon()` creates one `minecraft:item` entity. No objective, storage, forceloaded chunk, tick or load function.

## Basic usage

```kotlin
function("item_demo") {
	val sword = itemStack(Items.DIAMOND_SWORD) {
		enchantments {
			enchantment(Enchantments.SHARPNESS, 5)
			enchantment(Enchantments.UNBREAKING, 3)
		}
	}
	player.giveItem(sword)

	// spawns the stack as a dropped item entity in the world, not into an inventory
	sword.summon()
	sword.summon(textComponent("My Sword", Color.GOLD))
}
```

{{{ .components.mc.ItemTooltipMockup }}}

`summon()` spawns the stack as a `minecraft:item` entity at the given position (defaulting to `~ ~ ~`). The overload
taking a `ChatComponents` or a `String` + `Color` sets the entity's visible custom name.

## Writing a stack into NBT

`toNbt()` returns the `{id, count?, components?}` compound Minecraft expects wherever an item stack lives in NBT: the
`Item` tag of an item entity, a container's `Items` entries, or a `data modify ... set value` target. Empty components
are omitted, matching how vanilla serializes a stack with no component patch.

```kotlin
function("stock_shop") {
	val sword = itemStack(Items.DIAMOND_SWORD, 3) {
		enchantments {
			enchantment(Enchantments.SHARPNESS, 5)
		}
	}

	data(storage("shop", "kore")).modify("offer.item", sword.toNbt())
}
```

```mcfunction
data modify storage kore:shop offer.item set value {id:"minecraft:diamond_sword",count:3,components:{enchantments:{"minecraft:sharpness":5}}}
```

Pass explicit components to serialize a variant of the same stack without rebuilding it: `sword.toNbt(otherComponents)`.

## Configuring the spawned entity

`summon` takes a trailing `ItemEntitySummonData` block that controls the spawned entity's root NBT:

```kotlin
function("shop_display") {
	val item = itemStack(Items.DIAMOND_SWORD) {
		customName(textComponent("Shop", Color.GOLD))
	}

	item.summon {
		showcase = true          // never despawns, cannot be picked up
		tags = listOf("display") // scoreboard tags, handy to select it again later
	}
}
```

Generated output:

```mcfunction
summon minecraft:item 0.0 0.0 0.0 {CustomName:{text:Shop,color:gold},CustomNameVisible:1b,Invulnerable:1b,NoGravity:1b,Tags:[display],Age:-32768s,PickupDelay:32767s,Item:{id:"minecraft:diamond_sword",components:{custom_name:{text:Shop,color:gold}}}}
```

`showcase = true` is a shortcut for a static display item: it sets `PickupDelay` and `Age` so the entity never
despawns and cannot be picked up, plus `Invulnerable` and `NoGravity`. Any of those set explicitly win over the
shortcut, and the item's `custom_name` component is mirrored onto the entity so the name renders in the world.
An item entity with a `customName` shows it in the world unless `customNameVisible = false`.

### Shared entity fields

These come from `EntitySummonData`, the base shared by item entities, [mannequins](/docs/helpers/mannequins),
[display entities](/docs/helpers/display-entities) and [interactions](/docs/helpers/interactions). They are written first,
before the fields of the entity itself:

| Field               | Builder                     | NBT tag             | Type                |
|---------------------|-----------------------------|---------------------|---------------------|
| `air`               |                             | `Air`               | `Short`             |
| `customName`        | `customName(text, color)`   | `CustomName`        | `ChatComponents`    |
| `customNameVisible` |                             | `CustomNameVisible` | `Boolean`           |
| `data`              | `customData { }`            | `data`              | `NbtCompound`       |
| `fallDistance`      |                             | `fall_distance`     | `Double`            |
| `fire`              | `onFire(duration)`          | `Fire`              | `Short`             |
| `glowing`           |                             | `Glowing`           | `Boolean`           |
| `hasVisualFire`     |                             | `HasVisualFire`     | `Boolean`           |
| `invulnerable`      |                             | `Invulnerable`      | `Boolean`           |
| `invulnerableTime`  | `invulnerableFor(duration)` | `invulnerable_time` | `Int`               |
| `motion`            | `motion(dx, dy, dz)`        | `Motion`            | `List<Double>`      |
| `noGravity`         |                             | `NoGravity`         | `Boolean`           |
| `onGround`          |                             | `OnGround`          | `Boolean`           |
| `passengers`        | `passenger(entity)`         | `Passengers`        | `List<NbtCompound>` |
| `portalCooldown`    |                             | `PortalCooldown`    | `Int`               |
| `rotation`          | `rotation(yaw, pitch)`      | `Rotation`          | `List<Float>`       |
| `silent`            |                             | `Silent`            | `Boolean`           |
| `tags`              | `tag(vararg names)`         | `Tags`              | `List<String>`      |
| `team`              | `team(team)`                | `Team`              | `String`            |
| `ticksFrozen`       | `frozen(duration)`          | `TicksFrozen`       | `Int`               |

`invulnerableTime` is a number of ticks during which the entity ignores damage, while `invulnerable` makes it immune for good. The
duration builders take a `TimeNumber` and write ticks: `invulnerableFor(3.seconds)` writes `invulnerable_time:60`, `onFire(5.seconds)`
writes `Fire:100s` (at most 32767 ticks) and `frozen(1.seconds)` writes `TicksFrozen:20`. `team` is the name of the team the entity
joins when it spawns, and `data` is free NBT any entity stores, matched with `nbt={data:{...}}` in selectors. The `pos` of an entity isn't
a field because `summon` already takes its position.

```kotlin
val red = team("red")

function("launch_item") {
	itemStack(Items.TNT).summon {
		customName("Boom", Color.RED)          // CustomName, shown in the world
		customData { this["quest"] = "intro" } // data
		glowing = true                         // draws the outline seen through walls
		motion(0.0, 0.6, 0.0)                  // blocks per tick on each axis, so this pops it upwards
		onFire(5.seconds)                      // Fire:100s
		rotation(90f, 0f)                      // yaw then pitch, in degrees
		tag("loot", "quest")                   // adds to the tags already set
		team(red)                              // Team:"red"
	}
}
```

```mcfunction
summon minecraft:item 0.0 0.0 0.0 {CustomName:{text:Boom,color:red},CustomNameVisible:1b,data:{quest:intro},Fire:100s,Glowing:1b,Motion:[0.0d,0.6d,0.0d],Rotation:[90.0f,0.0f],Tags:[loot,quest],Team:red,Item:{id:"minecraft:tnt"}}
```

`passenger(entity)` adds a model with its `id`, as the model is when it is added. Mannequins, display entities and interactions take it,
an item entity isn't possible as a passenger because its `Item` compound only comes with `ItemStack.summon`:

```kotlin
val label = textDisplay {
	text("Boss")
	passenger(blockDisplay { blockState(Blocks.STONE) })
	passenger(mannequin { pose = MannequinPose.STANDING })
}

summon(label.entityType, vec3(), label.toNbt())
```

```mcfunction
summon minecraft:text_display ~ ~ ~ {Passengers:[{id:"minecraft:block_display",block_state:"minecraft:stone"},{id:"minecraft:mannequin",pose:standing}],text:Boss}
```

### Item-only fields

`ItemEntitySummonData` adds the fields that only exist on `minecraft:item`:

| Field                | NBT tag              | Description                                        |
|----------------------|----------------------|----------------------------------------------------|
| `age`                | `Age`                | Ticks lived - negative values delay despawning     |
| `health`             | `Health`             | Item entity health                                 |
| `owner`              | `Owner`              | UUID allowed to pick the item up                   |
| `pickupDelay`        | `PickupDelay`        | Ticks before pickup is allowed                     |
| `showcase`           | -                    | Shortcut described above                           |
| `thrower`            | `Thrower`            | UUID that threw the item                           |

For anything not covered, `summon` also accepts an `extraEntityNbt` builder applied last, so you can write arbitrary
root tags (a fixed `UUID`, modded data, and so on).

## Practical example

```kotlin
function("reward_drop") {
	val reward = itemStack(Items.NETHERITE_INGOT) {
		lore(textComponent("A rare reward dropped by the champion", Color.GRAY))
	}

	player.executeAt {
		run {
			reward.summon(textComponent("Champion Reward", Color.GOLD))
		}
	}
}
```

This pattern is useful when the same item should be given directly in one context and spawned as a visible reward in
another.

## See also

- [Entities & Players](/docs/oop/entities-and-players) - Give, replace, or spawn item stacks from entity-scoped helpers.
- [Events](/docs/oop/events) - React to item use or consumption with event-driven logic.
- [Components](/docs/concepts/components) - Define richer custom names, lore, and metadata for the items you build.
