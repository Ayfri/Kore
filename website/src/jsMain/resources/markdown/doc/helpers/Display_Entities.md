---
root: .components.layouts.MarkdownLayout
title: Display Entities
nav-title: Display Entities
description: A guide for creating Display Entities in the world.
keywords: minecraft, datapack, kore, guide, display-entities
date-created: 2024-04-06
date-modified: 2026-10-07
routeOverride: /docs/helpers/display-entities
---

# Display Entities

Display entities share a common set of world-rendering options and then add a few type-specific fields for blocks,
items, or text.

> [!FOOTPRINT]
> Only the entities you summon: each `summon` creates one display entity. No objective, storage, forceloaded chunk, tick or load function.

## Shared display settings

All display entities inherit these properties from the shared `DisplayEntity` base type:

- `billboardMode` - how the display faces the camera (`FIXED`, `VERTICAL`, `HORIZONTAL`, `CENTER`).
- `brightness` - overrides the block and sky light levels used for rendering (`Brightness(block = 15, sky = 15)`, 0 to 15).
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
```

This summons a snowy grass block at `0 0 0`, scaled by 2 and translated 2 blocks up, facing the camera on both axes.

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
		customName(textComponent("test"))

		enchantments {
			enchantment(Enchantments.SHARPNESS, 1)
			enchantment(Enchantments.UNBREAKING, 3)
		}

		attributeModifiers {
			modifier(
				type = Attributes.ATTACK_DAMAGE,
				amount = 1.0,
				name = "bonus_damage",
				operation = AttributeModifierOperation.ADD_VALUE,
			)
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

`Quaternion` and `Matrix` operators (`+`, `-`, `*`, `/`) return a new value and leave both operands untouched, while
named methods like `normalize()`, `slerp()` or `multiply()` modify the receiver. `Quaternion.IDENTITY` and
`Matrix.IDENTITY` give a new instance on each access, so modifying one never affects the others.

Transformations can also be combined:

- `a.interpolate(b, t)` returns the transformation `t` of the way from `a` (`0`) to `b` (`1`).
- `a.invert()` returns the transformation undoing `a`, as a matrix.
- `a.decompose()` splits a matrix transformation back into translation, rotations and scale.

## Interpolations

You can convert your display entity into an "interpolable" display entity by calling
`interpolable()` on it inside a function. This will allow you to interpolate between the current transformation and the target transformation in a given time.
The interpolable gets a UUID hashed from the pack name, the display and its position, so it stays the same across
builds and two identical displays still get different ones.

```kotlin
val interpolableEntityDisplay = blockDisplay {
	blockState(Blocks.STONE)
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

## OOP Entity Handles

After creating an interpolable, call `toEntity()` to get a typed OOP entity handle (`BlockDisplayEntity`,
`ItemDisplayEntity`, or `TextDisplayEntity`). This gives access to all `Entity` extension functions such as `kill`,
`teleportTo`, `addTag`, and more.

```kotlin
val display = blockDisplay {
	blockState(Blocks.STONE)
}.interpolable(vec3(0, 64, 0))

display.summon()

// toEntity() is typed as Entity, so cast when you want the specific subclass
val entity: BlockDisplayEntity = display.toEntity() as BlockDisplayEntity

// every Entity OOP extension works on the handle
entity.addTag("my_display")
entity.teleportTo(0, 65, 0)
entity.kill()
```

You can also construct the typed entity handles directly when you already have a UUID:

```kotlin
val uuid = uuid("12345678-1234-1234-1234-123456789012")
val block = BlockDisplayEntity(uuid)
val item  = ItemDisplayEntity(uuid)
val text  = TextDisplayEntity(uuid)
```

All three target their entity with `@e[type=minecraft:<type>,nbt={UUID:[I;...]}]`.

These handle classes live in the `oop` module and extend `Entity`, so every entity-scoped extension applies to them -
see [Entities & Players](/docs/oop/entities-and-players) for the full list.
