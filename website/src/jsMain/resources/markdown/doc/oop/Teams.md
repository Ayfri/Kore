---
root: .components.layouts.MarkdownLayout
title: Teams
nav-title: Teams
description: Object-oriented team management with the Kore OOP module - create, configure, and manage Minecraft teams.
keywords: minecraft, datapack, kore, oop, teams, scoreboard, collision, nametag, friendly fire
date-created: 2026-03-03
date-modified: 2026-03-31
routeOverride: /docs/oop/teams
---

# Teams

Wraps [Minecraft teams](https://minecraft.wiki/w/Scoreboard#Teams) into an object-oriented API:

```kotlin
function("team_setup") {
	val red = team("red") {
		ensureExists()
		setColor(Color.DARK_RED)
		setPrefix(textComponent { text = "RED "; color = Color.DARK_RED; bold = true })
		setFriendlyFire(false)
		setCollisionRule(CollisionRule.NEVER)
		setNametagVisibility(Visibility.HIDE_FOR_OTHER_TEAMS)
		addMembers(player)
	}

	player.joinTeam(red)
	player.leaveAnyTeam()
}
```

The main benefit is that configuration stays grouped by team, which makes lobby setup, role assignment, and PvP rules
much easier to read than a long list of raw `team modify` commands.

Teams can also act as a bridge to other OOP features. Once you have a `Team` handle, you can reuse its members as an
entity selector for scoreboards, boss bars, or any API that already works with `Entity`.

They also integrate nicely with `execute` conditions and score tracking helpers, which makes it easy to express checks
like “does this team still have this player?” or “how many players are still alive in this team?” without rebuilding
selectors by hand.

## Reusing a team in other OOP features

```kotlin
val red = team("red")
val phaseBar = registerBossBar("phase", name)
val points = scoreboard("phase.points")

function("sync_red_team") {
	phaseBar.setPlayers(red)
	points.getScore(red).set(10)
	red.clearMembers()
}
```

This keeps team-centric logic in one place: the same `Team` wrapper can drive visibility, score updates, and member
management without rebuilding selectors manually.

## Conditions and counters

```kotlin
val red = team("red")
val tracker = player("Tracker")
val membersLeft = tracker.getScoreEntity("red.members")

function("check_red_team") {
	execute {
		ifCondition {
			hasMembers(red)
			hasPlayer(red, "Ayfri")
			hasScore(red, "round.points", rangeOrInt(3))
		}

		run {
			say("Red team is still in the round")
		}
	}

	membersLeft.copyMemberCountFrom(red)
	membersLeft.copyTo(storage("match", namespace = name), "state.red_members")
}
```

This pattern is useful when several OOP systems need to exchange state. A `Team` can expose conditions for `execute`,
while a `ScoreboardEntity` can cache the current member count or mirror that count into entity/storage NBT for later
reuse by timers, events, or custom game rules.

## Team functions

| Function                    | Description                                         |
|-----------------------------|-----------------------------------------------------|
| `ensureExists`              | Create the team if it doesn't exist                 |
| `delete`                    | Delete the team                                     |
| `setDisplayName`            | Set the team display name                           |
| `setPrefix` / `setSuffix`   | Set name prefix/suffix                              |
| `setColor`                  | Set team color                                      |
| `setFriendlyFire`           | Toggle friendly fire                                |
| `setSeeFriendlyInvisibles`  | Toggle seeing invisible teammates                   |
| `setCollisionRule`          | Set collision rule                                  |
| `setNametagVisibility`      | Set nametag visibility                              |
| `setDeathMessageVisibility` | Set death message visibility                        |
| `addMembers`                | Add entities to the team                            |
| `hasMembers`                | Check if the team still has at least one member     |
| `hasPlayer` / `hasMember`   | Check if a specific player or entity is in the team |
| `hasScore`                  | Check a score range directly on the team selector   |
| `members`                   | Reuse the team as an `Entity` selector              |
| `clearMembers`              | Remove every current team member                    |

## Practical workflow

- Define the team once with its visual identity and gameplay rules.
- Add members when players join a role or side.
- Reuse `members()` or higher-level helpers like `bossBar.setPlayers(team)` and `scoreboard.getScore(team)` when other
  features need to target that whole team.
- Reuse `player.joinTeam(...)` and `player.leaveAnyTeam()` in gameplay functions instead of raw selectors.

## See also

- [Boss Bars](/docs/oop/boss-bars)
- [Scoreboards](/docs/oop/scoreboards)
- [Entities & Players](/docs/oop/entities-and-players)
