---
root: .components.layouts.MarkdownLayout
title: Items
nav-title: Items
description: Object-oriented item creation and spawning with the Kore OOP module.
keywords: minecraft, datapack, kore, oop, items, item stack, summon, give
date-created: 2026-03-03
date-modified: 2026-08-14
routeOverride: /docs/oop/items
---

# Items

The OOP module keeps item usage concise by letting you build an [item stack](https://minecraft.wiki/w/Item) once and
then reuse it for giving,
spawning, or embedding it in wider entity workflows.

## Basic usage

```kotlin
function("item_demo") {
	val sword = itemStack(Items.DIAMOND_SWORD) {
		enchantments {
			sharpness(5)
			unbreaking(3)
		}
	}
	player.giveItem(sword)

	// spawns the stack as a dropped item entity in the world, not into an inventory
	sword.summon()
	sword.summon(textComponent("My Sword", Color.GOLD))
}
```

`summon()` spawns the stack as a `minecraft:item` entity at the given position (defaulting to `~ ~ ~`). The overload
taking a `ChatComponents` or a `String` + `Color` sets the entity's visible custom name.

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
summon minecraft:item 0.0 0.0 0.0 {Invulnerable:1b,NoGravity:1b,Tags:["display"],Item:{id:"minecraft:diamond_sword",components:{custom_name:{text:"Shop",color:"gold"}}},PickupDelay:32767s,Age:-32768s,CustomName:{text:"Shop",color:"gold"},CustomNameVisible:1b}
```

`showcase = true` is a shortcut for a static display item: it sets `PickupDelay` and `Age` so the entity never
despawns and cannot be picked up, plus `Invulnerable` and `NoGravity`. Any of those set explicitly win over the
shortcut, and the item's `custom_name` component is mirrored onto the entity so the name renders in the world.

### Shared entity fields

These come from `EntitySummonData` and apply to every entity summoned through this API:

| Field                            | NBT tag                       |
|----------------------------------|-------------------------------|
| `air`                            | `Air`                         |
| `customName`                     | `CustomName`                  |
| `fallDistance`                   | `FallDistance`                |
| `fire`                           | `Fire`                        |
| `glowing`                        | `Glowing`                     |
| `hasVisualFire`                  | `HasVisualFire`               |
| `invulnerable`                   | `Invulnerable`                |
| `motion` / `motion(dx, dy, dz)`  | `Motion`                      |
| `noGravity`                      | `NoGravity`                   |
| `onGround`                       | `OnGround`                    |
| `portalCooldown`                 | `PortalCooldown`              |
| `pos`                            | `Pos`                         |
| `rotation` / `rotation(yaw, pitch)` | `Rotation`                 |
| `silent`                         | `Silent`                      |
| `tags`                           | `Tags`                        |

`motion(dx, dy, dz)` and `rotation(yaw, pitch)` are helper functions so you do not have to build the underlying
`Triple` or `Vec2` yourself:

```kotlin
function("launch_item") {
	itemStack(Items.TNT).summon {
		motion(0.0, 0.6, 0.0) // blocks per tick on each axis, so this pops it upwards
		rotation(90f, 0f)     // yaw then pitch, in degrees
		glowing = true        // draws the outline seen through walls
	}
}
```

### Item-only fields

`ItemEntitySummonData` adds the fields that only exist on `minecraft:item`:

| Field                | NBT tag              | Description                                        |
|----------------------|----------------------|----------------------------------------------------|
| `age`                | `Age`                | Ticks lived - negative values delay despawning     |
| `displayName`        | `CustomName`         | Name as `ChatComponents`, also settable via `displayName(...)` |
| `displayNameVisible` | `CustomNameVisible`  | Whether the name renders without looking at it     |
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
