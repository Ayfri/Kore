---
root: .components.layouts.MarkdownLayout
title: Items
nav-title: Items
description: Object-oriented item creation and spawning with the Kore OOP module.
keywords: minecraft, datapack, kore, oop, items, item stack, summon, give
date-created: 2026-03-03
date-modified: 2026-03-31
routeOverride: /docs/oop/items
---

# Items

The OOP module keeps item usage concise by letting you build an item stack once and then reuse it for giving,
spawning, or embedding it in wider entity workflows.

## Basic usage

```kotlin
function("item_demo") {
	val sword = itemStack(Items.DIAMOND_SWORD) {
		enchantments {
			sharpness(5)
			unbreaking(3)
		}
	}
	player.giveItem(sword)

	sword.summon()                                    // summon as item entity at 0 0 0
	sword.summon(textComponent("My Sword", Color.GOLD)) // summon with custom name
}
```

## Practical example

```kotlin
function("reward_drop") {
	val reward = itemStack(Items.NETHERITE_INGOT) {
		lore(textComponent("A rare reward dropped by the champion", Color.GRAY))
	}

	player.executeAt {
		run {
			reward.summon(textComponent("Champion Reward", Color.GOLD))
		}
	}
}
```

This pattern is useful when the same item should be given directly in one context and spawned as a visible reward in
another.

## See also

- [Entities & Players](/docs/oop/entities-and-players) – Give, replace, or spawn item stacks from entity-scoped helpers.
- [Events](/docs/oop/events) – React to item use or consumption with event-driven logic.
- [Components](/docs/concepts/components) – Define richer custom names, lore, and metadata for the items you build.
