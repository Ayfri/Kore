---
root: .components.layouts.MarkdownLayout
title: Timers
nav-title: Timers
description: Scoreboard-based timers and timer-with-boss-bar combos with the Kore OOP module.
keywords: minecraft, datapack, kore, oop, timer, scoreboard, bossbar, countdown, tick
date-created: 2026-03-03
date-modified: 2026-03-31
routeOverride: /docs/oop/timers
---

# Timers

Timers use a [scoreboard objective](https://minecraft.wiki/w/Scoreboard) that increments every tick.
When the score reaches the configured duration, completion handlers fire.

This is a strong fit for round timers, capture phases, delayed rewards, warmups, or any mechanic that should complete
after a predictable amount of ticks.

## Registering a timer

```kotlin
val timer = registerTimer("round_timer", 5.seconds)
```

This generates:

- A **load function** that creates the scoreboard objective.
- A **tick function** that increments the score for all players with score ≥ 0.

## Typical lifecycle

```kotlin
function("round_start") {
	timer.start(player)
}

function("round_cancel") {
	timer.stop(player)
}
```

Using dedicated start and stop points keeps it clear which gameplay events begin or cancel the countdown.

## Using a timer

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

## Timer with Boss Bar

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
to manually synchronize a separate UI layer.
