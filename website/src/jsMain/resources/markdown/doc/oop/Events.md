---
root: .components.layouts.MarkdownLayout
title: Events
nav-title: Events
description: Advancement-based event system for player and entity actions with the Kore OOP module.
keywords: minecraft, datapack, kore, oop, events, advancement, player, entity, death, click, consume, kill, recipe, dimension, riding, tame, bed, target
date-created: 2026-03-03
date-modified: 2026-04-01
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
  player.onChangeDimension { say("Changed dimension!") }
	player.onConsumeItem { say("Consumed something!") }
	player.onConsumeItem(Items.GOLDEN_APPLE) { say("Golden apple!") }
  player.onEffectsChanged { say("Effects changed!") }
  player.onEnchantItem { say("Enchanted an item!") }
	player.onHurtEntity { say("Hit!") }
  player.onRecipeCrafted(Recipes.CRAFTING_TABLE) { say("Crafted a recipe!") }
	player.onInventoryChange { say("Inventory changed!") }
	player.onItemUsedOnBlock { say("Used item on block!") }
	player.onKill { say("Kill!") }
	player.onPlaceBlock { say("Placed a block!") }
	player.onRightClick(Items.STICK) { say("Right click with stick!") }
  player.onStartRiding { say("Started riding!") }
}
```

Available player events (alphabetical):

| Function              | Trigger                               |
|-----------------------|---------------------------------------|
| `onBlockUse`          | Right-click any block                 |
| `onChangeDimension`   | Change dimension                      |
| `onConsumeItem`       | Consume any item (food, potion, etc.) |
| `onConsumeItem(item)` | Consume a specific item               |
| `onEffectsChanged`    | Effects on the player change          |
| `onEnchantItem`       | Enchant an item                       |
| `onEntityHurtPlayer`  | A player is hurt by an entity         |
| `onFallFromHeight`    | Fall from a height                    |
| `onHurtEntity`        | Deal damage to an entity              |
| `onInventoryChange`   | Inventory contents change             |
| `onItemUsedOnBlock`   | Use an item on a block                |
| `onKill`              | Kill an entity                        |
| `onPlaceBlock`        | Place a block                         |
| `onRecipeCrafted`     | Craft a recipe                        |
| `onRightClick(item)`  | Right-click while holding an item     |
| `onSleptInBed`        | Sleep in a bed                        |
| `onStartRiding`       | Start riding an entity                |
| `onTameAnimal`        | Tame an animal                        |
| `onTargetHit`         | Hit a target block                    |

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
