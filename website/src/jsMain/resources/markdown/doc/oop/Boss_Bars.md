---
root: .components.layouts.MarkdownLayout
title: Boss Bars
nav-title: Boss Bars
description: Object-oriented boss bar management with the Kore OOP module - register, configure, show, hide, and update boss bars.
keywords: minecraft, datapack, kore, oop, bossbar, boss bar, color, style, notched
date-created: 2026-03-03
date-modified: 2026-03-03
routeOverride: /docs/oop/boss-bars
position: 3
---

# Boss Bars

The OOP module wraps [Minecraft boss bars](https://minecraft.wiki/w/Commands/bossbar) into a simple
config + handle pattern.

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
