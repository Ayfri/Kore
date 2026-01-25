---
root: .components.layouts.MarkdownLayout
title: Mannequins
nav-title: Mannequins
description: A guide for creating Mannequins in the world.
keywords: minecraft, datapack, kore, guide, mannequins
date-created: 2026-01-25
date-modified: 2026-01-25
routeOverride: /docs/helpers/mannequins
---

# Mannequins

Mannequins are special entities that can display player skins and textures. They are highly customizable, allowing you to change their profile, hidden layers, and main hand.

## Creating a Mannequin

You can create a mannequin using the `mannequin` DSL.

```kotlin
val myMannequin = mannequin {
	hiddenLayers(MannequinLayer.CAPE, MannequinLayer.HAT)
	mainHand = MannequinHand.LEFT
	playerProfile("Ayfri")
}

summon(myMannequin.entityType, vec3(), myMannequin.toNbt())
```

## Profiles

Mannequins can have two types of profiles: `PlayerProfile` and `TextureProfile`.

### Player Profile

A player profile uses a player's name or UUID to fetch their skin and properties.

```kotlin
mannequin {
	playerProfile(name = "Steve", id = uuid("8667ba71-b85a-4004-af54-457a9734eed7"))
}
```

### Texture Profile

A texture profile allows you to specify a direct texture, and optionally a cape, elytra, and model type.

```kotlin
mannequin {
	textureProfile(texture = "tex") {
		cape = model("cape")
		model = MannequinModel.SLIM
	}
}
```

## Customization

Mannequins support additional fields for further customization:

```kotlin
mannequin {
	description = textComponent("Test")
	hideDescription = false
	immovable = true
	pose = MannequinPose.CROUCHING
}
```

### Pose

The `pose` field allows you to set the mannequin's pose. Available poses are:

- `STANDING`
- `CROUCHING`
- `SWIMMING`
- `FALL_FLYING`
- `SLEEPING`

## Hidden Layers

You can hide specific layers of the mannequin's skin using the `hiddenLayers` function.

Available layers:

- `CAPE`
- `HAT`
- `JACKET`
- `LEFT_PANTS_LEG`
- `LEFT_SLEEVE`
- `RIGHT_PANTS_LEG`
- `RIGHT_SLEEVE`

```kotlin
mannequin {
	hiddenLayers(MannequinLayer.JACKET, MannequinLayer.HAT)
}
```

## Main Hand

You can set which hand is the main hand of the mannequin.

```kotlin
mannequin {
	mainHand = MannequinHand.RIGHT
}
```
