---
root: .components.layouts.MarkdownLayout
title: "Minecraft Datapack Events: Player, Entity and World"
nav-title: Events
description: React to player, entity and world events with Kore's OOP module. Advancement-based triggers, death events, tick, weather, day/night and interval events.
keywords: minecraft, datapack, kore, oop, events, advancement, player, entity, death, click, consume, kill, recipe, dimension, riding, tame, bed, target, fishing, crossbow, totem, tick, bucket, potion, minecraft world events, datapack tick event, minecraft weather event, day night cycle datapack, kore world events, datapack interval timer, rain thunder event minecraft, world load event, datapack time of day
date-created: 2026-03-03
date-modified: 2026-11-11
routeOverride: /docs/oop/events
---

# Events

Events turn vanilla triggers into handlers you register once. Each event is backed by a
[function tag](https://minecraft.wiki/w/Tag#Function_tags) that a generated dispatcher calls, and multiple handlers can be
registered for the same event - they all fire together.

- **Player and entity events** react to something an entity does, such as a click, a kill or a death. Player events are
  detected with [advancements](https://minecraft.wiki/w/Advancement/JSON_format).
- **World events** react to the world itself: the tick, the weather, the day/night cycle or a fixed interval. A dispatcher
  registered in the vanilla `tick`/`load` tag calls each event's function tag.

You register interest once, then let the generated dispatchers call your handlers when Minecraft reports the action.

## Player and entity events

Player events use advancements to detect player/entity actions and dispatch them to function tags.

> [!FOOTPRINT]
> - **Objectives:** none.
> - **Storage:** none.
> - **Entities:** none.
> - **Forceloaded chunks:** none.
> - **Every tick:** no for player events, each one is an advancement. `onDeath` adds a `kore_oop_death_<hash>_tracker` function per selector, which tags the new matching entities, and `kore_oop_death_dispatcher`, which checks every item entity.
> - **Load and files:** per event, the advancement `kore_oop/<event>` and a `dispatch_<event>` function that revokes it and calls the `#<pack>:<event>` tag, plus one function per handler. `onDeath` adds the entity tag `kore_oop_death_<hash>` per selector and the loot table `kore_oop/death_trigger_<type>`.

### Registering events

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

Here is a complete pack, the [events example](/playground#example=events) of the playground, with handlers for a stick right click, a golden apple, a kill and a boss death, and the files Kore generates for it:

{{{ .components.doc.EventsExample }}}

#### Handler receives the entity

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

### Player events

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

### Typical workflow

1. Declare the entity or player handle that should receive the event helpers.
2. Register one or more event handlers, either directly in the datapack or inside a function.
3. Keep the handler bodies focused on gameplay reactions such as score updates, messaging, or spawning.

This pattern works well for mini-games where multiple systems need to react to the same player action.

### Entity events

```kotlin
val zombie = entity(EntityTypes.ZOMBIE) {
  tag = "my_tag"
}

zombie.onDeath { self -> say("A ${self.type?.name} died!") }
```

The death event uses a loot-table trigger. Every tick, the entities matching the selector for the first time get a
`kore_oop_death_<hash>` tag and a `DeathLootTable` that drops their usual loot plus a hidden structure void carrying
their tags. A tick dispatcher finds that item, runs the handlers of every selector whose tag it carries, then removes
it. Two selectors on the same type stay apart: here a zombie without `my_tag` runs no handler.

The trigger relies on the death loot table, which only mobs have, so:

- `onDeath` throws on a player or an entity without a type. A player's death is a `deathCount` objective instead.
- Nothing fires while the `doMobLoot` game rule is off, and the tracker replaces a `DeathLootTable` set by another pack.
- The handlers run as the dropped item, at the death position.

## World events

World events are the world-side counterpart of the player and entity events above: instead of reacting to an entity
action, they react to the world itself ticking, the weather changing, the day/night cycle, or a fixed interval
elapsing. Everything stays **100% datapack-side** - no in-world command blocks. Dynamic data is read with predicates and
scoreboards, and where a value has to be read at runtime you should reach for
[macros](/docs/commands/macros) rather than command blocks.

> [!FOOTPRINT]
> - **Objectives:** `kore_world`, with `#<event>.now`, `.prev` and `.counter` holders for edge and interval events.
> - **Storage:** none.
> - **Entities:** none.
> - **Forceloaded chunks:** none.
> - **Every tick:** one `dispatch_<event>` function per tick, weather, day/night, time of day or interval event, and per dimension.
> - **Load and files:** `kore_world_init` creates the objective, `onLoad` handlers run from `dispatch_on_world_load`. Each event gets a `#<pack>:<event>` tag and one function per handler.

### The world handle

Register events on a `world()` handle. Pass a dimension to scope a handler so it runs with the execution context set to
that dimension (`execute in <dimension> run ...`):

```kotlin
datapack("my_pack") {
	val world = world()
	val nether = world(Dimensions.THE_NETHER)

	world.onTick { say("Runs every tick, globally.") }
	nether.onTick { say("Runs every tick, executed in the Nether.") }
}
```

World event helpers only need a `DataPack` in scope, so you can register them straight in the `datapack { }` block, or
inside a `function { }` when you want to mix them with other commands.

### Available events

| Function             | Trigger                                                               |
|----------------------|-----------------------------------------------------------------------|
| `onDayStart`         | The tick `daytime` enters `0..11999` (dawn)                           |
| `onInterval(period)` | Every `period` ticks (scoreboard counter, immune to a frozen daytime) |
| `onLoad`             | Once on datapack load / `/reload`                                     |
| `onMidnight`         | The tick `daytime` reaches `18000`                                    |
| `onNightStart`       | The tick `daytime` enters `13000..23999`                              |
| `onNoon`             | The tick `daytime` reaches `6000`                                     |
| `onRainStart`        | The tick precipitation starts (rain or thunder)                       |
| `onRainStop`         | The tick precipitation stops                                          |
| `onThunderStart`     | The tick a thunderstorm starts                                        |
| `onThunderStop`      | The tick a thunderstorm stops                                         |
| `onTick`             | Every tick (20 times per second)                                      |
| `onTimeOfDay(time)`  | The tick `daytime` reaches `time` (`0..23999`)                        |

```kotlin
datapack("my_pack") {
	val world = world()

	world.onLoad { say("Pack loaded!") }
	world.onInterval(5.seconds) { say("Tick every 5 seconds.") }
	world.onRainStart { say("It started raining!") }
	world.onThunderStop { say("The storm passed.") }
	world.onDayStart { say("Good morning!") }
	world.onNoon { say("High noon.") }
	world.onNightStart { say("Watch out for mobs!") }
	world.onTimeOfDay(23000) { say("Sunrise is near.") }
}
```

### How edge events work

`onRainStart`, `onThunderStart`, `onDayStart` and friends are **edge-triggered**: they fire only on the single tick the
condition flips, not every tick the condition is true. The dispatcher stores the current state into a `kore_world`
scoreboard, compares it to the previous tick, runs the handlers on a transition, then saves the new state:

```mcfunction
# dispatch_on_rain_start
execute store result score #on_rain_start.now kore_world if predicate {type:"minecraft:weather_check",raining:1b}
execute if score #on_rain_start.now kore_world matches 1 if score #on_rain_start.prev kore_world matches 0 run function #my_pack:on_rain_start
scoreboard players operation #on_rain_start.prev kore_world = #on_rain_start.now kore_world
```

Because the previous state is unset on the first tick after a reload, edge events never fire spuriously on load.

`onInterval` instead keeps a counter on the same objective, so it stays accurate even when `advance_time` is off:

```mcfunction
# dispatch_on_interval
scoreboard players add #on_interval.counter kore_world 1
execute if score #on_interval.counter kore_world matches 100.. run function #my_pack:on_interval
execute if score #on_interval.counter kore_world matches 100.. run scoreboard players set #on_interval.counter kore_world 0
```

### World event notes

- `onRainStart`/`onRainStop` track precipitation in general (a thunderstorm also counts as raining). Use the thunder
  events when you specifically care about storms.
- Weather and time are global in vanilla; binding an event to a dimension changes **where the handler runs**, not which
  world's weather is read.
- The shared `kore_world` objective is created once per datapack in the generated `kore_world_init` load function.
- `onNoon` and `onMidnight` are thin wrappers over `onTimeOfDay`. Use `onTimeOfDay` directly for sunset (`12000`),
  sunrise (`23000`), or any custom moment. Moon-phase logic can be layered on top: react in `onNightStart`, then read
  the game time with a macro to compute `(day % 8)`.

## See also

- [Timers & Cooldowns](/docs/oop/timers) - Count up to a duration per entity with an optional boss bar, or gate event-driven abilities so players cannot spam them.
- [Entities & Players](/docs/oop/entities-and-players) - Build the selectors and entity handles that receive these event
  helpers.
- [Items](/docs/oop/items) - React to item usage or rewards with reusable item stacks.
