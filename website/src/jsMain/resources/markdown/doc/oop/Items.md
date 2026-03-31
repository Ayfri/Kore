---
root: .components.layouts.MarkdownLayout
title: Items
nav-title: Items
description: Object-oriented item creation and spawning with the Kore OOP module.
keywords: minecraft, datapack, kore, oop, items, item stack, summon, give
date-created: 2026-03-03
date-modified: 2026-03-03
routeOverride: /docs/oop/items
position: 8
---

# Items

Object-oriented item creation and spawning:

```kotlin
function("item_demo") {
	val sword = itemStack("diamond_sword")
	player.giveItem(sword)

	sword.summon()                                    // summon as item entity at 0 0 0
	sword.summon(textComponent("My Sword", Color.GOLD)) // summon with custom name
}
```
