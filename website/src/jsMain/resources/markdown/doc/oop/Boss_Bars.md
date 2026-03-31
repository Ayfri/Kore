---
root: .components.layouts.MarkdownLayout
title: Boss Bars
nav-title: Boss Bars
description: Object-oriented boss bar management with the Kore OOP module - register, configure, show, hide, and update boss bars.
keywords: minecraft, datapack, kore, oop, bossbar, boss bar, color, style, notched
date-created: 2026-03-03
date-modified: 2026-03-31
routeOverride: /docs/oop/boss-bars
---

# Boss Bars

The OOP module wraps [Minecraft boss bars](https://minecraft.wiki/w/Commands/bossbar) into a simple
config + handle pattern.

This is useful when you want one place to configure a bar and then reuse it across multiple gameplay functions.

## Registering a boss bar

```kotlin
val bar = registerBossBar("my_bar", name) {
	color = BossBarColor.RED
	max = 200
	style = BossBarStyle.NOTCHED_10
	value = 50
}
```

A load function is generated that creates the bar and applies all initial settings.

## Typical workflow

```kotlin
function("show_phase_bar") {
	bar.apply {
		setPlayers(player)
		setValue(150)
		show()
	}
}

function("hide_phase_bar") {
	bar.hide()
}
```

In practice, boss bars often pair well with timers, boss fights, or round-based activities where the bar is configured
once and then updated from several different functions.

## Manipulating a boss bar

```kotlin
function("boss_fight") {
	bar.apply {
		setValue(100)
		setColor(BossBarColor.BLUE)
		setPlayers(player)
		show()
		hide()
		remove()
	}
}
```

## Function reference

| Function     | Description                                     |
|--------------|-------------------------------------------------|
| `setValue`   | Update the current boss bar value               |
| `setColor`   | Change the boss bar color                       |
| `setPlayers` | Control which players currently see the bar     |
| `show`       | Make the bar visible to the assigned players    |
| `hide`       | Hide the bar without deleting its configuration |
| `remove`     | Remove the boss bar entirely                    |
