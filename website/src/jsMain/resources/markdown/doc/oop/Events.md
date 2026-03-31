---
root: .components.layouts.MarkdownLayout
title: Events
nav-title: Events
description: Advancement-based event system for player and entity actions with the Kore OOP module.
keywords: minecraft, datapack, kore, oop, events, advancement, player, entity, death, click, consume, kill
date-created: 2026-03-03
date-modified: 2026-03-31
routeOverride: /docs/oop/events
---

# Events

Events use [advancements](https://minecraft.wiki/w/Advancement/JSON_format) to detect player/entity actions
and dispatch them to [function tags](https://minecraft.wiki/w/Tag#Function_tags). Multiple handlers can be
registered for the same event - they all fire together.

This makes events a convenient bridge between vanilla triggers and OOP-style gameplay code: you register interest once,
then let the generated dispatchers call your handlers when Minecraft reports the action.

## Player events

```kotlin
val player = player("Steve")

function("my_events") {
	player.onBlockUse { say("Interacted with a block!") }
	player.onConsumeItem { say("Consumed something!") }
	player.onConsumeItem(Items.GOLDEN_APPLE) { say("Golden apple!") }
	player.onHurtEntity { say("Hit!") }
	player.onInventoryChange { say("Inventory changed!") }
	player.onItemUsedOnBlock { say("Used item on block!") }
	player.onKill { say("Kill!") }
	player.onPlaceBlock { say("Placed a block!") }
	player.onRightClick(Items.STICK) { say("Right click with stick!") }
}
```

Available player events (alphabetical):

| Function              | Trigger                               |
|-----------------------|---------------------------------------|
| `onBlockUse`          | Right-click any block                 |
| `onConsumeItem`       | Consume any item (food, potion, etc.) |
| `onConsumeItem(item)` | Consume a specific item               |
| `onHurtEntity`        | Deal damage to an entity              |
| `onInventoryChange`   | Inventory contents change             |
| `onItemUsedOnBlock`   | Use an item on a block                |
| `onKill`              | Kill an entity                        |
| `onPlaceBlock`        | Place a block                         |
| `onRightClick(item)`  | Right-click while holding an item     |

## Typical workflow

1. Declare the entity or player handle that should receive the event helpers.
2. Register one or more event handlers inside a function.
3. Keep the handler bodies focused on gameplay reactions such as score updates, messaging, or spawning.

This pattern works well for mini-games where multiple systems need to react to the same player action.

## Entity events

```kotlin
val zombie = Entity().apply { selector.type = EntityTypes.ZOMBIE }

function("mob_events") {
	zombie.onDeath { say("A zombie died!") }
}
```

The death event uses a loot-table trigger: on death the entity drops a hidden item detected by a tick dispatcher that
runs all death handlers then removes the item.

## See also

- [Cooldowns](/docs/oop/cooldowns) – Gate event-driven abilities or interactions so players cannot spam them.
- [Entities & Players](/docs/oop/entities-and-players) – Build the selectors and entity handles that receive these event
  helpers.
- [Items](/docs/oop/items) – React to item usage or rewards with reusable item stacks.
