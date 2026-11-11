---
root: .components.layouts.MarkdownLayout
title: "Datapack Scoreboards: Objectives, Players, Sidebars"
nav-title: Scoreboards
description: Create scoreboard objectives, set and operate on player scores and show them in a sidebar in a Minecraft datapack, all in Kotlin.
keywords: minecraft, datapack, kore, guide, scoreboards
date-created: 2024-04-06
date-modified: 2026-11-11
routeOverride: /docs/concepts/scoreboards
---

# Scoreboards

Scoreboards track numeric values for players and entities. They're one of the two runtime variable containers in a
datapack (the other being [data storage](/docs/concepts/data-storage)), and are essential for game mechanics, timers,
and
counters. If you're unsure when to use a scoreboard versus a Kotlin variable,
read [Runtime Logic](/docs/concepts/runtime-logic)
first. For the full scoreboard command reference, see [Commands](/docs/commands/commands#scoreboard-command).

You can manage scoreboards with the `scoreboard` command:

```kotlin
scoreboard.objectives.add("my_objective", ScoreboardCriteria.DUMMY)
```

## Creating objectives

You have multiple forms of the `scoreboard` command:

```kotlin
scoreboard {
	objectives {
		add("my_objective", ScoreboardCriteria.DUMMY)
		// this form lets you manage multiple objectives at once
	}
}

scoreboard {
	objective("my_objective") {
		add(ScoreboardCriteria.DUMMY)
		// this form lets you manage a single objective
	}
}
```

## Managing objectives

You can add, remove, set display name, set display slot, set render type of objectives:

```kotlin
scoreboard {
	objective("my_objective") {
		add(ScoreboardCriteria.DUMMY, displayName = textComponent("My Objective", Color.GOLD))
		setDisplaySlot(DisplaySlots.sidebar)
		setRenderType(RenderType.INTEGER)
	}
}
```

Here is a complete pack, the [scoreboards example](/playground#example=scoreboards) of the playground, with an objective, its display and score operations, and the commands Kore generates for it:

{{{ .components.doc.ScoreboardsExample }}}

## Manage players

You can manage players with the `players` block:

```kotlin
scoreboard {
	players {
		add(allPlayers(), "my_objective", 1)
		remove(self(), "my_objective", 5)
		reset(self(), "my_objective")
		set(self(), "my_objective", 10)

		operation(self(), "my_objective", Operation.ADD, self(), "my_objective")
	}

	player(self()) {
		add("my_objective", 1)
		remove("my_objective", 5)
		reset("my_objective")
		set("my_objective", 10)

		operation("my_objective", Operation.ADD, self(), "my_objective")
	}
}
```

You can also manage an objective for multiple selectors at once:

```kotlin
scoreboard {
	players {
		objective("my_objective") {
			add(self(), 1)
			remove(self(), 5)
			reset(self())
			set(self(), 10)

			operation(self(), Operation.ADD, self(), objective)
		}
	}
}
```

Or also manage an objective for a single selector:

```kotlin
scoreboard {
	player(self()) {
		objective("my_objective") {
			add(1)
			remove(5)
			reset()
			set(10)

			operation(Operation.ADD, self(), objective)
		}
	}
}
```

These methods offer a more readable way to manage objectives, and avoid repetition operations invoking multiple times the same selector/objective.

## Sidebars

To show custom text lines on the right of the screen, like on servers, use the
[Sidebars helper](/docs/helpers/sidebars) from the `helpers` module.
