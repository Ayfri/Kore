---
root: .components.layouts.MarkdownLayout
title: World Events
nav-title: World Events
description: World-level event system for tick, weather, day/night, and interval triggers with the Kore OOP module.
keywords: minecraft, datapack, kore, oop, events, world, tick, load, interval, weather, rain, thunder, day, night, noon, midnight, time of day, dimension
date-created: 2026-06-23
date-modified: 2026-06-23
routeOverride: /docs/oop/world-events
---

# World Events

World events are the world-side counterpart of the [entity/player events](/docs/oop/events): instead of reacting to an
entity action, they react to the world itself ticking, the weather changing, the day/night cycle, or a fixed interval
elapsing. Everything stays **100% datapack-side** - no in-world command blocks. Dynamic data is read with predicates and
scoreboards, and where a value has to be read at runtime you should reach for
[macros](/docs/commands/macros) rather than command blocks.

Like entity events, multiple handlers can be registered for the same event and they all fire together. Each event is
backed by a function registered in the vanilla `tick`/`load` tag that dispatches to a per-event
[function tag](https://minecraft.wiki/w/Tag#Function_tags).

## The world handle

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

## Available events

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

## How edge events work

`onRainStart`, `onThunderStart`, `onDayStart` and friends are **edge-triggered**: they fire only on the single tick the
condition flips, not every tick the condition is true. The dispatcher stores the current state into a `kore_world`
scoreboard, compares it to the previous tick, runs the handlers on a transition, then saves the new state:

```mcfunction
# dispatch_on_rain_start
execute store result score #on_rain_start.now kore_world if predicate {condition:"minecraft:weather_check",raining:1b}
execute if score #on_rain_start.now kore_world matches 1 if score #on_rain_start.prev kore_world matches 0 run function #my_pack:on_rain_start
scoreboard players operation #on_rain_start.prev kore_world = #on_rain_start.now kore_world
```

Because the previous state is unset on the first tick after a reload, edge events never fire spuriously on load.

`onInterval` instead keeps a counter on the same objective, so it stays accurate even when `doDaylightCycle` is off:

```mcfunction
# dispatch_on_interval
scoreboard players add #on_interval.counter kore_world 1
execute if score #on_interval.counter kore_world matches 100.. run function #my_pack:on_interval
execute if score #on_interval.counter kore_world matches 100.. run scoreboard players set #on_interval.counter kore_world 0
```

## Notes

- `onRainStart`/`onRainStop` track precipitation in general (a thunderstorm also counts as raining). Use the thunder
  events when you specifically care about storms.
- Weather and time are global in vanilla; binding an event to a dimension changes **where the handler runs**, not which
  world's weather is read.
- The shared `kore_world` objective is created once per datapack in the generated `kore_world_init` load function.
- `onNoon` and `onMidnight` are thin wrappers over `onTimeOfDay`. Use `onTimeOfDay` directly for sunset (`12000`),
  sunrise (`23000`), or any custom moment. Moon-phase logic can be layered on top: react in `onNightStart`, then read
  the game time with a macro to compute `(day % 8)`.

## See also

- [Events](/docs/oop/events) - the entity/player event system this mirrors.
- [Timers](/docs/oop/timers) - count up to a duration per entity, with an optional boss bar.
- [Cooldowns](/docs/oop/cooldowns) - gate abilities so they cannot be spammed.
