---
root: .components.layouts.MarkdownLayout
title: Display Entities
nav-title: Display Entities
description: A guide for creating Dispaly Entities in the world.
keywords: minecraft, datapack, kore, guide, display-entities
date-created: 2024-04-06
date-modified: 2024-04-06
routeOverride: /docs/helpers/display-entities
---

# Display Entities

## Entity Displays

Entity displays are used to display blocks/items/text in the world. You can define multiple properties for the display, such as
transformation, billboard mode, shadow etc.

```kotlin
val entityDisplay = blockDisplay {
	blockState(Blocks.GRASS_BLOCK) {
		properties {
			this["snowy"] = true
		}
	}

	transformation {
		leftRotation {
			quaternionNormalized(0.0, 0.0, 0.0, 1.0)
		}

		scale = vec3(2.0)

		translation {
			y = 2.0
		}
	}

	billboardMode = BillboardMode.CENTER
	shadowRadius = 0.5f
}

summon(entity = entityDisplay.entityType, pos = vec3(0, 0, 0), nbt = entityDisplay.toNbt())
// will summon a grass block with snow on top, scaled by 2, rotated by 0 degrees and translated by 2 blocks on the y axis at the position 0, 0, 0
```

## Block Displays

Block displays are used to display blocks in the world. They are created by calling `blockDiplay()` DSL.

```kotlin
val blockDisplay = blockDisplay {
	blockState(Blocks.GRASS_BLOCK) {
		properties {
			this["snowy"] = true
		}
	}
}
```

## Item Displays

Item displays are used to display items in the world. They are created by calling `itemDisplay()` DSL.

```kotlin
val itemDisplay = itemDisplay {
	item(Items.DIAMOND_SWORD) {
		name = textComponent("test")

		enchantments {
			Enchantments.SHARPNESS at 1
			Enchantments.UNBREAKING at 3
		}

		modifiers {
			modifier(Attributes.ATTACK_DAMAGE, 1.0, AttributeModifierOperation.ADD)
		}
	}
}
```

## Text Displays

Text displays are used to display text in the world. They are created by calling `textDisplay()` DSL.

```kotlin
val textDisplay = textDisplay {
	text("test", Color.RED) {
		bold = true
	}
}
```

## Transformations

Transformations are used to modify the translation, left/right rotations and scale of displays. They are created by calling `transformation`
DSL. You can also apply directly matrix transformations and use quaternions, axis angles or use Euler angles for rotations.

```kotlin
transformation {
	leftRotation {
		quaternionNormalized(0.0, 0.0, 0.0, 1.0)
	}

	scale = vec3(2.0)

	translation {
		y = 2.0
	}
}
```

## Interpolations

You can convert your display entity into an "interpolable" display entity by calling `interpolable()` on it. This will allow you to
interpolate between the current transformation and the target transformation in a given time.

```kotlin
val interpolableEntityDisplay = blockDisplay {
	blockState(Blocks.STONE_BLOCK)
}.interpolable(position = vec3(0, 0, 0))

interpolableEntityDisplay.summon()

interpolableEntityDisplay.interpolateTo(duration = 2.seconds) {
	translation {
		y = 2.0
	}
}
```

I will later add more methods and maybe a complete DSL for making full animations.
