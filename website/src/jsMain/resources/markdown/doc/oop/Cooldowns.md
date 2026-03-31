---
root: .components.layouts.MarkdownLayout
title: Cooldowns
nav-title: Cooldowns
description: Scoreboard-based cooldown system with the Kore OOP module - register, start, check, and reset cooldowns.
keywords: minecraft, datapack, kore, oop, cooldown, scoreboard, timer, tick
date-created: 2026-03-03
date-modified: 2026-03-03
routeOverride: /docs/oop/cooldowns
position: 4
---

# Cooldowns

Cooldowns use a [scoreboard objective](https://minecraft.wiki/w/Scoreboard) that decrements every tick.
When the score reaches 0 the cooldown is ready.

## Registering a cooldown

```kotlin
val cd = registerCooldown("attack_cd", 2.seconds)
```

This generates:

- A **load function** that creates the scoreboard objective.
- A **tick function** that decrements the score for all players with score ≥ 1.

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
