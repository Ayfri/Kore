---
root: .components.layouts.MarkdownLayout
title: Events
nav-title: Events
description: Advancement-based event system for player and entity actions with the Kore OOP module.
keywords: minecraft, datapack, kore, oop, events, advancement, player, entity, death, click, consume, kill, recipe, dimension, riding, tame, bed, target, fishing, crossbow, totem, tick, bucket, potion
date-created: 2026-03-03
date-modified: 2026-06-23
routeOverride: /docs/oop/events
---

# Events

Events use [advancements](https://minecraft.wiki/w/Advancement/JSON_format) to detect player/entity actions
and dispatch them to [function tags](https://minecraft.wiki/w/Tag#Function_tags). Multiple handlers can be
registered for the same event - they all fire together.

This makes events a convenient bridge between vanilla triggers and OOP-style gameplay code: you register interest once,
then let the generated dispatchers call your handlers when Minecraft reports the action.

## Registering events

Event helpers only need a `DataPack` in scope, so you can register them straight in the `datapack { }` block, without
wrapping them in a `function { }`:

```kotlin
datapack("my_pack") {
  val player = player("Steve")
  player.onKill { say("Kill!") }
}
```

Registering them inside a `function { }` still works (the surrounding datapack is in scope there too), which is handy
when you want to mix event registration with other commands.

### Handler receives the entity

Each handler is invoked with the entity/player handle it was registered on, so you can keep chaining the OOP API
without re-declaring the handle:

```kotlin
datapack("my_pack") {
  val player = player("Steve")
  player.onKill { self ->
    self.setScore("kills", 1)
    say("Kill counted!")
  }
}
```

> Minecraft runs reward functions **as** the player, so the handle you get back is always the player/entity the event
> was registered on. The other entity involved (the mob you killed, the attacker, etc.) is not exposed by the game and
> cannot be passed in.

## Player events

```kotlin
val player = player("Steve")

player.onBlockUse { say("Interacted with a block!") }
player.onConsumeItem(Items.GOLDEN_APPLE) { say("Golden apple!") }
player.onFishingRodHooked { say("Got a bite!") }
player.onRecipeCrafted(Recipes.CRAFTING_TABLE) { say("Crafted a recipe!") }
player.onRightClick(Items.STICK) { say("Right click with stick!") }
player.onUsedTotem { say("Cheated death!") }
```

Available player events (alphabetical):

| Function               | Trigger                               |
|------------------------|---------------------------------------|
| `onBlockUse`           | Right-click any block                 |
| `onBredAnimals`        | Breed two animals                     |
| `onBrewedPotion`       | Take a potion out of a brewing stand  |
| `onChangeDimension`    | Change dimension                      |
| `onConsumeItem`        | Consume any item (food, potion, etc.) |
| `onConsumeItem(item)`  | Consume a specific item               |
| `onEffectsChanged`     | Effects on the player change          |
| `onEnchantItem`        | Enchant an item                       |
| `onEntityHurtPlayer`   | A player is hurt by an entity         |
| `onFallFromHeight`     | Fall from a height                    |
| `onFilledBucket`       | Fill a bucket                         |
| `onFishingRodHooked`   | Hook something with a fishing rod     |
| `onHurtEntity`         | Deal damage to an entity              |
| `onInteractWithEntity` | Right-click an entity                 |
| `onInventoryChange`    | Inventory contents change             |
| `onItemUsedOnBlock`    | Use an item on a block                |
| `onKill`               | Kill an entity                        |
| `onKilledByArrow`      | Get killed by an arrow                |
| `onPlaceBlock`         | Place a block                         |
| `onRecipeCrafted`      | Craft a recipe                        |
| `onRightClick(item)`   | Right-click while holding an item     |
| `onShotCrossbow`       | Shoot a crossbow                      |
| `onSleptInBed`         | Sleep in a bed                        |
| `onStartRiding`        | Start riding an entity                |
| `onTameAnimal`         | Tame an animal                        |
| `onTargetHit`          | Hit a target block                    |
| `onTick`               | Every tick (per player)               |
| `onUsedEnderEye`       | Use an eye of ender                   |
| `onUsedTotem`          | Trigger a totem of undying            |

## Typical workflow

1. Declare the entity or player handle that should receive the event helpers.
2. Register one or more event handlers, either directly in the datapack or inside a function.
3. Keep the handler bodies focused on gameplay reactions such as score updates, messaging, or spawning.

This pattern works well for mini-games where multiple systems need to react to the same player action.

## Entity events

```kotlin
val zombie = Entity().apply { selector.type = EntityTypes.ZOMBIE }

zombie.onDeath { self -> say("A ${self.type?.name} died!") }
```

The death event uses a loot-table trigger: on death the entity drops a hidden item detected by a tick dispatcher that
runs all death handlers then removes the item.

## See also

- [World Events](/docs/oop/world-events) - The world-side counterpart: tick, weather, day/night, and interval triggers.
- [Cooldowns](/docs/oop/cooldowns) - Gate event-driven abilities or interactions so players cannot spam them.
- [Entities & Players](/docs/oop/entities-and-players) - Build the selectors and entity handles that receive these event
  helpers.
- [Items](/docs/oop/items) - React to item usage or rewards with reusable item stacks.
