---
root: .components.layouts.MarkdownLayout
title: Game State Machine
nav-title: Game State Machine
description: Scoreboard-based game state machine with the Kore OOP module - register states, transition between them, and react to state changes.
keywords: minecraft, datapack, kore, oop, game state, state machine, scoreboard, transition, lobby
date-created: 2026-03-03
date-modified: 2026-03-31
routeOverride: /docs/oop/game-state-machine
---

# Game State Machine

The game state system uses a [scoreboard](https://minecraft.wiki/w/Scoreboard) to track the current state
and dispatches handlers when the state matches.

It is a good fit for lobbies, round systems, multi-phase boss fights, tutorials, or any flow that repeatedly switches
between a known set of named states.

## Registering states

```kotlin
val states = registerGameStates {
	state("lobby")
	state("playing")
	state("ended")
}
```

This generates a **load function** that creates the `kore_state` objective and sets the initial state.

## Typical lifecycle

```kotlin
function("prepare_game") {
	states.transitionTo("lobby")
}

function("start_game") {
	states.transitionTo("playing")
}

function("finish_game") {
	states.transitionTo("ended")
}
```

Keeping transitions explicit like this makes it easier to reason about which setup code belongs to each phase.

## Transitioning and reacting

```kotlin
function("game_loop") {
	states.transitionTo("playing")

	states.whenState("playing") {
		say("The game is in progress!")
	}

	states.whenState("ended") {
		say("Game over!")
	}
}
```

## Game state actions

Helper functions integrate states with other OOP systems:

```kotlin
// Transition to a state only when a cooldown is ready
states.transitionWithCooldown("playing", cooldown, player)

// Spawn entities when entering a state
states.whenStateSpawn("playing", zombieSpawner, count = 3)

// Start a timer when entering a state
states.whenStateStartTimer("playing", timer, player)
```

## Best practices

- Keep the state list small and meaningful (`lobby`, `playing`, `ended`, etc.).
- Reserve `whenState(...)` for behavior that should only run in one phase.
- Use the integration helpers when a state change should also start a timer, consume a cooldown, or trigger a spawn.
