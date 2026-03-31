---
root: .components.layouts.MarkdownLayout
title: OOP Utilities
nav-title: OOP Utilities
description: Overview of object-oriented gameplay utilities in the Kore OOP module — entities, teams, scoreboards, items, events, timers, spawners, and state machines.
keywords: minecraft, datapack, kore, oop, entity, player, commands, teams, scoreboard, items, events, cooldown, bossbar, effects, timer, spawner, gamestate
date-created: 2026-02-21
date-modified: 2026-03-31
routeOverride: /docs/oop/utilities
position: 0
---

# OOP Utilities

The OOP module provides high-level, object-oriented wrappers around Minecraft systems such as entities, teams,
scoreboards, timers, and gameplay state.
Each feature is documented on its own page — see the links below.

Generated resource names for OOP-specific features are centralized in `OopConstants` so they're easy to find and
override.
Helper-specific generated names now live in `HelpersConstants` inside the `helpers` module.

Utility-style features such as renderers, math helpers, raycasts, areas, state delegates, and VFX now live in the
[`helpers` module](/docs/helpers/utilities).

## All Features

- **[Boss Bars](/docs/oop/boss-bars)** — Register, configure, and manage boss bars.
- **[Cooldowns](/docs/oop/cooldowns)** — Scoreboard-based cooldown system that decrements every tick.
- **[Entities & Players](/docs/oop/entities-and-players)** — Create entities and players, execute helpers, batch
  commands, entity commands, and entity effects.
- **[Events](/docs/oop/events)** — Advancement-based event system for player and entity actions.
- **[Game State Machine](/docs/oop/game-state-machine)** — Scoreboard-based state machine with transition helpers.
- **[Items](/docs/oop/items)** — Object-oriented item creation and spawning.
- **[Scoreboards](/docs/oop/scoreboards)** — Objective management and per-entity score operations.
- **[Spawners](/docs/oop/spawners)** — Reusable entity spawner handles for summoning mobs.
- **[Teams](/docs/oop/teams)** — Object-oriented team management with colors, collision rules, and nametag visibility.
- **[Timers](/docs/oop/timers)** — Scoreboard-based timers with optional boss bar integration.

## Vanilla Kore vs OOP Kore

The OOP module wraps the low-level command DSL into object-oriented abstractions.
Here's a complete side-by-side comparison showing how the same mini-game setup looks with each approach.

### Vanilla Kore

```kotlin
dataPack("arena") {
	val namespace = "arena"

	// --- Scoreboard objectives ---
	function("setup") {
		scoreboard.objectives.add("kills", "dummy", textComponent("Kills"))
		scoreboard.objectives.add("game_state", "dummy")
		scoreboard.objectives.add("cooldown_dash", "dummy")
		teams {
			team("red") {
				color = FormattingColor.RED
				collisionRule = CollisionRule.PUSH_OTHER_TEAMS
			}
			team("blue") {
				color = FormattingColor.BLUE
				collisionRule = CollisionRule.PUSH_OTHER_TEAMS
			}
		}
	}

	// --- Player setup (manual selectors) ---
	function("join_red") {
		val player = allPlayers {
			limit = 1
			sort = Sort.NEAREST
		}
		teams.join("red", player)
		gamemode(Gamemode.SURVIVAL, player)
		effect.give(player, Effects.SPEED, 999999, 1)
		scoreboard.players.set(player, "kills", 0)
		scoreboard.players.set(player, "cooldown_dash", 0)
	}

	function("join_blue") {
		val player = allPlayers {
			limit = 1
			sort = Sort.NEAREST
		}
		teams.join("blue", player)
		gamemode(Gamemode.SURVIVAL, player)
		effect.give(player, Effects.SPEED, 999999, 1)
		scoreboard.players.set(player, "kills", 0)
		scoreboard.players.set(player, "cooldown_dash", 0)
	}

	// --- State transitions (manual scoreboard) ---
	function("start_game") {
		scoreboard.players.set(literal("#game_state"), "game_state", 1)
		execute {
			asTarget(allPlayers())
			run {
				title(self()) {
					title(textComponent("Game Started!") {
						color = Color.GREEN
						bold = true
					})
				}
			}
		}
	}

	// --- Cooldown tick (manual decrement) ---
	function("tick_cooldowns") {
		execute {
			ifCondition {
				score(allPlayers(), "cooldown_dash", 1..Int.MAX_VALUE)
			}
			run {
				scoreboard.players.remove(allPlayers(), "cooldown_dash", 1)
			}
		}
	}

	// --- Spawning a mob (manual summon) ---
	function("spawn_guardian") {
		summon(EntityTypes.IRON_GOLEM, vec3(0, 64, 0))
	}
}
```

### OOP Kore

```kotlin
dataPack("arena") {
	// --- Players as objects ---
	val redPlayer = player("RedPlayer")
	val bluePlayer = player("BluePlayer")

	// --- Teams ---
	val redTeam = team("red") {
		color = FormattingColor.RED
		collisionRule = CollisionRule.PUSH_OTHER_TEAMS
	}
	val blueTeam = team("blue") {
		color = FormattingColor.BLUE
		collisionRule = CollisionRule.PUSH_OTHER_TEAMS
	}

	// --- Scoreboard ---
	val kills = scoreboard("kills")

	// --- Cooldown ---
	val dashCooldown = registerCooldown("dash", 3.seconds)

	// --- Game states ---
	val states = registerGameStates {
		state("lobby")
		state("running")
		state("finished")
	}

	// --- Spawner ---
	val guardian = registerSpawner("guardian", EntityTypes.IRON_GOLEM) {
		position = vec3(0, 64, 0)
	}

	// --- Player setup ---
	function("join_red") {
		redPlayer.joinTeam("red")
		redPlayer.setGamemode(Gamemode.SURVIVAL)
		redPlayer.giveEffect(Effects.SPEED, duration = 999999, amplifier = 1)
		kills.set(redPlayer, 0)
	}

	function("join_blue") {
		bluePlayer.joinTeam("blue")
		bluePlayer.setGamemode(Gamemode.SURVIVAL)
		bluePlayer.giveEffect(Effects.SPEED, duration = 999999, amplifier = 1)
		kills.set(bluePlayer, 0)
	}

	// --- State transition ---
	function("start_game") {
		states.transitionTo("running")
		redPlayer.title(textComponent("Game Started!") {
			color = Color.GREEN
			bold = true
		})
		bluePlayer.title(textComponent("Game Started!") {
			color = Color.GREEN
			bold = true
		})
	}

	// --- Spawning ---
	function("spawn_guardian") {
		guardian.spawn()
	}
}
```

### Key Differences

| Aspect                | Vanilla Kore                                                           | OOP Kore                                                                     |
|-----------------------|------------------------------------------------------------------------|------------------------------------------------------------------------------|
| **Entity references** | Manual selectors (`allPlayers { ... }`) repeated everywhere            | Named objects (`player("RedPlayer")`) reused across functions                |
| **Commands**          | Low-level calls like `scoreboard.players.set(...)`, `effect.give(...)` | Method calls on entities: `player.giveEffect(...)`, `player.joinTeam(...)`   |
| **Game state**        | Manual scoreboard objectives and raw `set` calls                       | `registerGameStates { state("running") }` + `states.transitionTo("running")` |
| **Cooldowns**         | Manual scoreboard decrement loops                                      | `registerCooldown("dash", 3.seconds)` — tick function auto-generated         |
| **Spawning**          | Raw `summon(EntityTypes.X, pos)`                                       | `registerSpawner(...)` + `spawner.spawn()`                                   |
| **Boilerplate**       | Selector construction, objective registration, execute blocks          | Handled internally by the OOP abstractions                                   |

The OOP module doesn't replace vanilla Kore — it builds on top of it.
You can freely mix both styles, using OOP utilities where they simplify your code and dropping to vanilla commands when
you need fine-grained control.
