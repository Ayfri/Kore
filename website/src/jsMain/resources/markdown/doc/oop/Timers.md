---
root: .components.layouts.MarkdownLayout
title: Minecraft Timers and Cooldowns on Scoreboards
nav-title: Timers & Cooldowns
description: Scoreboard-based timers and cooldowns with Kore's OOP module. Countdown and boss bar timers, tick schedules and per-entity cooldowns.
keywords: timer datapack, minecraft timer, datapack timer, scoreboard timer, countdown timer, minecraft cooldown, cooldown datapack, ability cooldown, boss bar timer, tick timer, kore timer, kore cooldown, datapack schedule
date-created: 2026-03-03
date-modified: 2026-11-11
routeOverride: /docs/oop/timers
---

# Timers and Cooldowns

Timers and cooldowns are both built on a [scoreboard objective](https://minecraft.wiki/w/Scoreboard) that changes by one
every [tick](/docs/concepts/time), and both are reusable for several entities without hand-writing the tick logic.

- A **timer** counts up from 0 to its duration and fires its `onComplete` handlers when it gets there. Pick it for round timers,
  capture phases, delayed rewards, warmups, or any mechanic that should complete after a predictable amount of ticks.
- A **cooldown** counts down from its duration to 0 and answers "ready or not" for each entity. Pick it for abilities,
  interactions, item usage limits, or any mechanic that should be reusable once the delay has passed.

Durations are expressed as [`TimeNumber`](/docs/concepts/time) values - `5.seconds`, `200.ticks`, `2.seconds`, `40.ticks`, etc.

## Timers

Timers use a scoreboard objective that increments every tick.
When the score reaches the configured duration, completion handlers fire.

> [!FOOTPRINT]
> - **Objectives:** one per timer, named after it.
> - **Storage:** none.
> - **Entities:** none.
> - **Forceloaded chunks:** none.
> - **Every tick:** `timer_<name>_tick` adds 1 to every entity at 0 or more, and `registerTimerWithBossBar` adds `timer_<name>_bar_tick`, which copies the timer into the boss bar.
> - **Load and files:** `timer_<name>_init` creates the objective, `onComplete` blocks become functions listed in `#kore:timer_complete_handlers`, and `registerTimerWithBossBar` adds the boss bar `<pack>:<name>_bar`.

### Registering a timer

```kotlin
val timer = registerTimer("round_timer", 5.seconds)
```

This generates:

- A **load function** that creates the scoreboard objective.
- A **tick function** that increments the score of every entity with a score ≥ 0, players or not.

### Typical lifecycle

```kotlin
function("round_start") {
	timer.start(player)
}

function("round_cancel") {
	timer.stop(player)
}
```

Using dedicated start and stop points keeps it clear which gameplay events begin or cancel the countdown.

### Using a timer

```kotlin
function("round") {
	with(timer) {
		start(player)
		onComplete(player) {
			say("Time's up!")
		}
		stop(player)
	}
}
```

| Function     | Description                                            |
|--------------|--------------------------------------------------------|
| `start`      | Set the score to 0, beginning the countdown            |
| `stop`       | Set the score to −1, halting the timer                 |
| `onComplete` | Register a handler that fires when duration is reached |

### Timer with Boss Bar

Combines a timer with an auto-managed boss bar:

```kotlin
val timedBar = registerTimerWithBossBar("boss_timer", 10.seconds) {
	color = BossBarColor.GREEN
	style = BossBarStyle.NOTCHED_20
}

function("boss_round") {
	with(timedBar) {
		start(player)
		onComplete(player) { say("Boss round over!") }
		stop(player)
	}
}
```

The boss-bar variant is useful when you want a countdown that is both mechanical and visible to players without having
to manually synchronize a separate UI layer. The bar fills from empty to full over the duration. Its max is the duration
in ticks, and every tick copies the timer score into the bar:

```mcfunction
execute as @e[limit=1,scores={boss_timer=0..}] store result bossbar my_pack:boss_timer_bar value run scoreboard players get @s boss_timer
```

One bar shows one timer: when several entities run the same timer at once, the bar follows one of them. Register a
timer per entity, or per team, to show each its own bar.

Here is a complete pack, the [timer boss bar example](/playground#example=timer-boss-bar) of the playground, with a round timer shown on a boss bar, and the files Kore generates for it:

{{{ .components.doc.TimerBossBarExample }}}

## Cooldowns

Cooldowns use a scoreboard objective that decrements every tick.
When the score reaches 0 the cooldown is ready.

> [!FOOTPRINT]
> - **Objectives:** one per cooldown, named after it.
> - **Storage:** none.
> - **Entities:** none.
> - **Forceloaded chunks:** none.
> - **Every tick:** `cooldown_<name>_tick` removes 1 from every entity above 0.
> - **Load and files:** `cooldown_<name>_init` creates the objective. Each `ifReady` block becomes a `cooldown_<name>_ready_<hash>` function, listed in `#kore:cooldown_ready_handlers`.

### Registering a cooldown

```kotlin
val cd = registerCooldown("attack_cd", 2.seconds)
```

This generates:

- A **load function** that creates the scoreboard objective.
- A **tick function** that decrements the score of every entity with a score ≥ 1, players or not.

### Typical usage pattern

```kotlin
function("dash_skill") {
	with(cd) {
		ifReady(player) {
			say("Dash!")
		}
	}
}

function("dash_hit") {
	cd.start(player)
}
```

This split keeps the "can I use it?" check separate from the event or action that actually starts the cooldown.

Here is a complete pack, the [cooldowns example](/playground#example=cooldowns) of the playground, with a dash ability on a cooldown, and the files Kore generates for it:

{{{ .components.doc.CooldownsExample }}}

### Using a cooldown

```kotlin
function("combat") {
	with(cd) {
		start(player)               // sets the score to the duration
		ifReady(player) {           // runs only when score == 0, then restarts
			say("Attack ready!")
		}
		reset(player)               // forces score to 0
	}
}
```

### Function reference

| Function  | Description                                                    |
|-----------|----------------------------------------------------------------|
| `start`   | Set the entity score to the configured duration                |
| `ifReady` | Run a block only when the cooldown is ready                    |
| `reset`   | Force the cooldown back to `0`                                 |
| tick hook | Auto-generated function that decrements active cooldown scores |

## See also

- [Time Units](/docs/concepts/time) - The `TimeNumber` values used for durations.
- [Events](/docs/oop/events) - Start or reset cooldowns and timers from gameplay triggers such as clicks, kills, or item use.
- [State Delegates](/docs/helpers/state-delegates) - Reduce scoreboard boilerplate in adjacent stateful systems.
