---
root: .components.layouts.MarkdownLayout
title: Scoreboards
nav-title: Scoreboards
description: Object-oriented scoreboard management with the Kore OOP module - objectives, display slots, and per-entity score operations.
keywords: minecraft, datapack, kore, oop, scoreboard, objective, score, display slot
date-created: 2026-03-03
date-modified: 2026-03-31
routeOverride: /docs/oop/scoreboards
---

# Scoreboards

Wraps [Minecraft scoreboards](https://minecraft.wiki/w/Scoreboard) with objective management and per-entity score
operations.

This split between objective handles and per-entity score handles maps well to how vanilla scoreboards already work,
but with a more readable API.

## Objectives

```kotlin
function("scoreboard_setup") {
	scoreboard("kills") {
		create()
		setDisplaySlot(DisplaySlots.sidebar)
		setDisplayName("Kill Count")
	}
}
```

| Function         | Description           |
|------------------|-----------------------|
| `create`         | Create the objective  |
| `remove`         | Remove the objective  |
| `setDisplaySlot` | Assign a display slot |
| `setDisplayName` | Set the display name  |
| `setRenderType`  | Set the render type   |

## Practical pattern

```kotlin
function("match_setup") {
	val kills = scoreboard("kills")
	kills.create()
	kills.setDisplaySlot(DisplaySlots.sidebar)

	val playerKills = player.getScoreEntity("kills")
	playerKills.set(0)
}
```

The usual pattern is to configure the objective once, then retrieve entity-specific score handles wherever gameplay code
needs to increment, reset, or copy values.

## Per-entity scores

```kotlin
function("score_ops") {
	val score = player.getScoreEntity("kills")
	score.set(10)
	score.add(5)
	score.remove(2)
	score.reset()
	score.copyFrom(player.asSelector(), "deaths")
}
```

| Function   | Description                                 |
|------------|---------------------------------------------|
| `set`      | Set score to a value                        |
| `add`      | Add to the score                            |
| `remove`   | Subtract from the score                     |
| `reset`    | Reset the score                             |
| `copyTo`   | Copy this score to another holder/objective |
| `copyFrom` | Copy from another holder/objective          |
