---
root: .components.layouts.MarkdownLayout
title: State Delegates
nav-title: State Delegates
description: Kotlin property delegates that map scoreboard objectives or NBT storage to simple var properties with the Kore helpers module.
keywords: minecraft, datapack, kore, helpers, state, delegate, scoreboard, storage, nbt, property
date-created: 2026-03-03
date-modified: 2026-03-31
routeOverride: /docs/helpers/state-delegates
position: 15
---

# State Delegates

Kotlin property delegates that map scoreboard objectives or [NBT storage](https://minecraft.wiki/w/Commands/data)
paths to simple `var` properties. Writing to the property emits the corresponding Minecraft command.

## Scoreboard delegate

```kotlin
function("mana_system") {
	var mana by player.scoreboard("mana_obj", default = 100)
	mana = 80  // emits: /scoreboard players set <player> mana_obj 80
}
```

## Storage delegate

```kotlin
function("storage_demo") {
	var customTag by player.storage("my_namespace:data", "customTag", default = "hello")
	customTag = "world"  // emits: /data modify storage my_namespace:data customTag set value "world"
}
```
