---
root: .components.layouts.MarkdownLayout
title: Teams
nav-title: Teams
description: Object-oriented team management with the Kore OOP module - create, configure, and manage Minecraft teams.
keywords: minecraft, datapack, kore, oop, teams, scoreboard, collision, nametag, friendly fire
date-created: 2026-03-03
date-modified: 2026-03-03
routeOverride: /docs/oop/teams
position: 16
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

## Team functions

| Function                    | Description                         |
|-----------------------------|-------------------------------------|
| `ensureExists`              | Create the team if it doesn't exist |
| `delete`                    | Delete the team                     |
| `setDisplayName`            | Set the team display name           |
| `setPrefix` / `setSuffix`   | Set name prefix/suffix              |
| `setColor`                  | Set team color                      |
| `setFriendlyFire`           | Toggle friendly fire                |
| `setSeeFriendlyInvisibles`  | Toggle seeing invisible teammates   |
| `setCollisionRule`          | Set collision rule                  |
| `setNametagVisibility`      | Set nametag visibility              |
| `setDeathMessageVisibility` | Set death message visibility        |
| `addMembers`                | Add entities to the team            |
