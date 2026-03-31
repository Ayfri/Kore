---
root: .components.layouts.MarkdownLayout
title: Cooldowns
nav-title: Cooldowns
description: Scoreboard-based cooldown system with the Kore OOP module - register, start, check, and reset cooldowns.
keywords: minecraft, datapack, kore, oop, cooldown, scoreboard, timer, tick
date-created: 2026-03-03
date-modified: 2026-03-31
routeOverride: /docs/oop/cooldowns
---

# Cooldowns

Cooldowns use a [scoreboard objective](https://minecraft.wiki/w/Scoreboard) that decrements every tick.
When the score reaches 0 the cooldown is ready.

They are a good fit for abilities, interactions, item usage limits, or any mechanic that should be reusable for several
players without hand-writing the decrement logic every time.

## Registering a cooldown

```kotlin
val cd = registerCooldown("attack_cd", 2.seconds)
```

This generates:

- A **load function** that creates the scoreboard objective.
- A **tick function** that decrements the score for all players with score ≥ 1.

## Typical usage pattern

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

This split keeps the “can I use it?” check separate from the event or action that actually starts the cooldown.

## Using a cooldown

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

## Function reference

| Function  | Description                                                    |
|-----------|----------------------------------------------------------------|
| `start`   | Set the player score to the configured duration                |
| `ifReady` | Run a block only when the cooldown is ready                    |
| `reset`   | Force the cooldown back to `0`                                 |
| tick hook | Auto-generated function that decrements active cooldown scores |

## See also

- [Timers](/docs/oop/timers) – Use timers when you need scheduled callbacks rather than per-player readiness checks.
- [Events](/docs/oop/events) – Start or reset cooldowns from gameplay triggers such as clicks, kills, or item use.
- [State Delegates](/docs/helpers/state-delegates) – Reduce scoreboard boilerplate in adjacent stateful systems.
