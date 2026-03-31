---
root: .components.layouts.MarkdownLayout
title: Entities & Players
nav-title: Entities & Players
description: Create and manage entities and players with the Kore OOP module — selectors, execute helpers, batch commands, entity commands, and entity effects.
keywords: minecraft, datapack, kore, oop, entity, player, commands, execute, batch, effects, teleport, kill, damage
date-created: 2026-03-03
date-modified: 2026-03-03
routeOverride: /docs/oop/entities-and-players
position: 5
---

# Entities & Players

The OOP module models Minecraft entities and players as Kotlin objects with selectors and context-aware
extension functions.

## Creating entities

```kotlin
val player = player("Steve") {
	gamemode = Gamemode.SURVIVAL
	team = "red"
}

val zombie = entity {
	type = EntityTypes.ZOMBIE
}
```

`player()` creates a `Player` instance (subclass of `Entity`) with `type = minecraft:player`, `limit = 1`,
and the given name. `entity()` creates a generic `Entity` with custom selector arguments.

## Execute helpers

Entity-scoped execute shortcuts emit `/execute as`, `/execute at`, or both:

```kotlin
function("teleport_self") {
	player.executeAs {
		run { it.teleportTo(it) }
	}

	player.executeAt {
		run { it.giveItem(itemStack("diamond")) }
	}

	player.executeAsAt {
		run { it.sendMessage("Hello from my location!") }
	}
}
```

## Batch

`batch()` creates a named sub-function that groups multiple commands under a single entity context:

```kotlin
function("setup") {
	player.batch("init_player") {
		giveItem(itemStack("diamond_sword"))
		giveEffect(Effects.SPEED, duration = 200)
		sendMessage("Welcome!")
	}
}
```

## Entity Commands

Extension functions on `Entity` for common [Minecraft commands](https://minecraft.wiki/w/Commands):

```kotlin
function("commands_demo") {
	player.kill()
	player.damage(5f)
	player.addTag("vip")
	player.removeTag("vip")
	player.giveXp(10.levels)
	player.setXp(0.points)
	player.setGamemode(Gamemode.CREATIVE)
	player.sendMessage("Hello!")
	player.showTitle(textComponent("Title"), textComponent("Subtitle"))
	player.showActionBar(textComponent("Action bar text"))
	player.playSound(Sounds.ENTITY_EXPERIENCE_ORB_PICKUP)
	player.mount(zombie)
	player.dismount()
	player.clearItems()
	player.giveItem(itemStack("diamond"))
	player.replaceItem(ItemSlotType.MAINHAND, itemStack("netherite_sword"))
}
```

| Function        | Description                               |
|-----------------|-------------------------------------------|
| `addTag`        | Add a scoreboard tag                      |
| `clearItems`    | Clear inventory (optionally filtered)     |
| `damage`        | Deal damage with optional damage type     |
| `dismount`      | Dismount from current vehicle             |
| `giveItem`      | Give an item stack                        |
| `giveXp`        | Add experience (levels or points)         |
| `kill`          | Kill the entity                           |
| `mount`         | Mount another entity                      |
| `playSound`     | Play a sound at the entity                |
| `removeTag`     | Remove a scoreboard tag                   |
| `replaceItem`   | Replace an item in a specific slot        |
| `sendMessage`   | Send a tellraw message                    |
| `setGamemode`   | Change the player's gamemode              |
| `setXp`         | Set experience to an exact value          |
| `showActionBar` | Display text on the action bar            |
| `showTitle`     | Display a title and optional subtitle     |
| `teleportTo`    | Teleport to coordinates or another entity |

## Entity Effects

Extension functions on `Entity` for giving, clearing, and managing
[mob effects](https://minecraft.wiki/w/Effect):

```kotlin
function("buff_player") {
	player.giveEffect(Effects.SPEED, duration = 200, amplifier = 1)
	player.giveInfiniteEffect(Effects.NIGHT_VISION, hideParticles = true)
	player.clearEffect(Effects.SPEED)
	player.clearAllEffects()
}
```

| Function             | Description                           |
|----------------------|---------------------------------------|
| `giveEffect`         | Give a timed effect with optional amp |
| `giveInfiniteEffect` | Give an infinite-duration effect      |
| `clearEffect`        | Remove a specific effect              |
| `clearAllEffects`    | Remove all effects                    |
| `effects { ... }`    | Builder block for multiple operations |
