---
root: .components.layouts.MarkdownLayout
title: Scoreboards
nav-title: Scoreboards
description: A guide for managing scoreboards in a Minecraft datapack using Kore.
keywords: minecraft, datapack, kore, guide, scoreboards
date-created: 2024-04-06
date-modified: 2024-04-06
routeOverride: /docs/concepts/scoreboards
---

# Scoreboards

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

## Scoreboard Displays

Scoreboard Displays are a new helper that let you manage right sidebar displays, like on servers.

You can create a display with the `scoreboardDisplay` function:

```kotlin
scoreboardDisplay("my_display") {
	displayName = textComponent("My Display", Color.GOLD)

	setLine(0, textComponent("Line 1", Color.AQUA))
	appendLine(textComponent("Line 2", Color.AQUA))
	emptyLine()
	appendLine("Line 4", Color.AQUA)

	appendLine(textComponent("Line 2", Color.AQUA)) {
		createIf { // this line will only be created if the condition is true, this is executed in an `execute if` block
			predicate("stonks")
		}
	}
}
```

You can also change the line numbers display:

```kotlin
scoreboardDisplay("my_display") {
	decreasing = false
	startingScore = 0
}
```

#### New since 1.20.3

You can now hide the values of the lines:

```kotlin
scoreboardDisplay("my_display") {
	appendLine("a") {
		hideValue = true // this will hide the value of the line
	}

	appendLine("b")

	hideValues() // this will hide the values of all lines
	// you can also provide a range of indices for the lines to hide
}
```

Feel free to add feedback if you have any idea to improve this or to use other features from the new
`scoreboard players display numberformat` subcommand.

### Resetting Scoreboard Displays

You can reset all scoreboards with the `resetAll` function:

```kotlin
ScoreboardDisplay.resetAll()
```

### Limitations

Scoreboard Displays are displayed the same way to everyone, so you can't have different displays for different players. You can at least have different displays for different team colors, but that's all (so there's a property to set the display slot). The sidebar is limited to 15 lines, so you can't have more than 15 lines in a display.

### How it works

Scoreboard Displays are generated using fake players and teams, it will create teams with randomized numbers as name to avoid conflicts. Each line = 1 team, and each team has a suffix with the line text, then a fake player is added to the team with the score of the line number. For dynamic animations of displays, there aren't any solution for that currently. The only way to do that is to use a binary tree of functions, checking the score of the player between 0 and the middle of the maximum score, then between the middle and the maximum, and split the function in two, and so on. Then, when you arrive to the last function, you can call the
`setLine` function to set the line text. And you repeat this for each line.

If you have a better solution, maybe using macros, feel free to create functions for that and create a pull request.

#### New since 1.20.3

Now scoreboard displays can be created more easily as you can now customize the display of each player as a text component. No teams are created anymore, and the display is generated using the
`scoreboard players display name` command, achieving the same result.
