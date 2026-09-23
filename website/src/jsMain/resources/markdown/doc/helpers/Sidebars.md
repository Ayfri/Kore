---
root: .components.layouts.MarkdownLayout
title: Minecraft Datapack Sidebar - Custom Scoreboard Lines with Kore
nav-title: Sidebars
description: "Build server-style Minecraft sidebars with Kore: up to 15 text lines, right-aligned values, live scores, conditional lines and cheap refreshes."
keywords: minecraft sidebar, datapack sidebar, scoreboard display, custom scoreboard, scoreboard lines, numberformat, kore sidebar, minigame scoreboard
date-created: 2026-09-23
date-modified: 2026-09-23
routeOverride: /docs/helpers/sidebars
---

# Sidebars

A `Sidebar` shows up to 15 lines of text on the right of the screen, like a minigame server scoreboard. Each line has a
left text and an optional right-aligned value, and can hold live scores, selectors or NBT values.

```kotlin
import io.github.ayfri.kore.helpers.sidebar.sidebar

val lobby = sidebar("lobby") {
	title("Sky Wars", Color.GOLD)
	line("Map: Floating Isles")
	emptyLine()
	line("Players", value = scoreComponent("players_alive", literal("#game")))
	line("Kills", Color.RED, scoreComponent("kills", literal("#top")))
	emptyLine()
	line("play.example.net", Color.YELLOW)
}

load { lobby.create() }
tick { lobby.refresh() }
```

{{{ .components.mc.SidebarMockup }}}

`sidebar(...)` only declares the sidebar, commands are emitted where you call its functions.

## Lines

`line(text, color, value)` adds a line, `emptyLine()` adds a blank one. Lines appear in declaration order, top to
bottom.

```kotlin
sidebar("stats") {
	line("Coins") { value("1 250", Color.GOLD) }
	line(textComponent("Rank: ") + text("VIP", Color.AQUA))
}
```

Without a value, a line shows nothing on its right: the score numbers are always hidden.

## Live values and refresh

The game resolves score, selector and NBT components once, when a line is written. `refresh()` rewrites only the lines
holding such components, so calling it every tick stays cheap.

```kotlin
val arena = sidebar("arena") {
	line("Time left", value = scoreComponent("timer", literal("#round")))
	line("Arena: Desert")
}

tick { arena.refresh() }
```

Here `refresh()` emits the three commands of the "Time left" line, the static "Arena" line is never rewritten.

Score components read a named score holder, such as a fake player like `#round`. `@s` has no meaning in a sidebar
refreshed from `tick`.

## Conditional lines

`visibleIf` only shows a line while its condition passes. The condition is checked on `create()` and `refresh()`.

```kotlin
sidebar("event") {
	line("Double XP active!", Color.GREEN) {
		visibleIf { score(literal("#event"), "double_xp", rangeOrInt(1)) }
	}
}
```

## Runtime changes

`setLine` replaces a line from any function, without changing its declaration.

```kotlin
function("switch_map") {
	lobby.setLine(0, textComponent("Map: Nether Fortress"))
}
```

`show(slot)`, `hide(slot)` and `remove()` manage the display. `DisplaySlots.sidebarTeam(color)` shows a different sidebar
to each team color.

```kotlin
function("red_team_sidebar") {
	redTeamSidebar.show(DisplaySlots.sidebarTeam(FormattingColor.RED))
}

function("end_game") {
	lobby.hide()
	lobby.remove()
}
```

## How it works

Each sidebar owns its objective, and each line is the fake player `$<index>` scored `-index`, so lines sort top to
bottom and two sidebars never share a line. `create()` recreates the objective, hides score numbers with
`numberformat blank`, writes the text with `scoreboard players display name` and the value with
`numberformat fixed`.

A sidebar looks the same for every player watching the same slot. Per-player sidebars aren't possible in vanilla
because display slots are global, team-colored slots are the closest option.
