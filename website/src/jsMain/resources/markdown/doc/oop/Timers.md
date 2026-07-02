---
root: .components.layouts.MarkdownLayout
title: Minecraft Timer Datapack - Scoreboard Timers in Kore OOP
nav-title: Timers
description: Scoreboard-based timers with Kore's OOP module. Countdown timers, boss bar timers, tick-based schedules, and reusable handles. Build cooldowns and game timers without raw command blocks.
keywords: timer datapack, minecraft timer, datapack timer, scoreboard timer, countdown timer, minecraft cooldown, boss bar timer, tick timer, kore timer, datapack schedule
date-created: 2026-03-03
date-modified: 2026-07-02
routeOverride: /docs/oop/timers
---

# Timers

Timers use a [scoreboard objective](https://minecraft.wiki/w/Scoreboard) that increments
every [tick](/docs/concepts/time).
When the score reaches the configured duration, completion handlers fire.

This is a strong fit for round timers, capture phases, delayed rewards, warmups, or any mechanic that should complete
after a predictable amount of [ticks](/docs/concepts/time). Durations are expressed as [
`TimeNumber`](/docs/concepts/time) values - `5.seconds`, `200.ticks`, etc.

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
