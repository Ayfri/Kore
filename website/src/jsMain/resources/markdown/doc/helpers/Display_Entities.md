---
root: .components.layouts.MarkdownLayout
title: Display Entities
nav-title: Display Entities
description: A guide for creating Display Entities in the world.
keywords: minecraft, datapack, kore, guide, display-entities
date-created: 2024-04-06
date-modified: 2026-04-01
routeOverride: /docs/helpers/display-entities
---

# Display Entities

Display entities share a common set of world-rendering options and then add a few type-specific fields for blocks,
items, or text.

## Shared display settings

All display entities inherit these properties from the shared `DisplayEntity` base type:

- `billboardMode` - how the display faces the camera (`FIXED`, `VERTICAL`, `HORIZONTAL`, `CENTER`).
- `brightness` - optional block and light overrides for rendering.
- `glowColorOverride` - replace the outline color with a custom RGB value.
- `height` / `width` - resize the display bounds.
- `interpolationDuration` / `startInterpolation` - animate transformation changes over time.
- `shadowRadius` / `shadowStrength` - control the projected shadow.
- `transformation` - combine translation, rotation, scale, or custom matrices.
- `viewRange` - control when the entity is rendered from a distance.

## Entity Displays

Entity displays are used to display blocks/items/text in the world. You can define multiple properties for the display, such as transformation, billboard mode, shadow etc.

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

Block displays are used to display blocks in the world. They are created by calling the `blockDisplay()` DSL.

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

The optional `displayMode` property uses `ItemDisplayModelMode`, which serializes to the lowercase values Minecraft
expects:

- `FIRSTPERSON_LEFTHAND`
- `FIRSTPERSON_RIGHTHAND`
- `FIXED`
- `GROUND`
- `GUI`
- `HEAD`
- `NONE`
- `ON_SHELF`
- `THIRDPERSON_LEFTHAND`
- `THIRDPERSON_RIGHTHAND`

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

`alignment` uses `TextAlignment`, which currently supports:

- `LEFT`
- `CENTER`
- `RIGHT`

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

You can convert your display entity into an "interpolable" display entity by calling
`interpolable()` on it. This will allow you to interpolate between the current transformation and the target transformation in a given time.

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

Interpolation is especially useful when you want display entities to move or morph smoothly between ticks without
rebuilding the entity from scratch.
