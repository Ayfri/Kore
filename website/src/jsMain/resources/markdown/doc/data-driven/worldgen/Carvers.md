---
root: .components.layouts.MarkdownLayout
title: Carvers
nav-title: Carvers
description: Carve caves and canyons into Minecraft terrain with Kore - probability, height, tunnel count and radius multipliers.
keywords: minecraft, datapack, kore, worldgen, carver, cave, canyon, ravine
date-created: 2026-08-17
date-modified: 2026-10-20
routeOverride: /docs/data-driven/worldgen/carvers
---

# Carvers

Carvers hollow out terrain after the noise and surface steps but before features, which is why caves never destroy trees or ores placed
later. A carver pairs a carver type with its configuration and lives in `data/<ns>/worldgen/carver/<name>.json`. Biomes reference carvers
by ID.

Two carver types exist:

- `cave` - Winding tunnel systems, sometimes branching out of a circular room.
- `canyon` - Deep ravines with steep walls.

A carver replaces every block it carves with air, or with water or lava below the aquifer fluid level. It never carves the bottom layer or
the top 7 blocks of the generation range.

References: [Carver definition](https://minecraft.wiki/w/Carver_definition), [Carver](https://minecraft.wiki/w/Carver), misode's
[carver generator](https://misode.github.io/worldgen/carver/)

## Declaring Carvers

`carvers` opens a scope where `cave` and `canyon` each create exactly one file. Every field has a neutral default, so a carver only needs
the values that differ from it.

```kotlin
fun DataPack.myCarvers() {
	carvers {
		cave("my_cave") {
			count = veryBiasedToBottom(0, 14)
			floorLevel = uniform(-1.0f, -0.4f)
			horizontalRadiusMultiplier = uniform(0.7f, 1.4f)
			probability = 0.15
			roomVerticalRadiusMultiplier = uniform(0.1f, 0.9f)
			thickness = trapezoid(0.0f, 3.0f, 1.0f)
			verticalRadiusMultiplier = uniform(0.8f, 1.3f)
			weirdThicknessBias = true
			y = uniformHeightProvider(aboveBottom(8), absolute(180))
		}
	}
}
```

```json
{
	"type": "minecraft:cave",
	"count": { "type": "minecraft:very_biased_to_bottom", "min_inclusive": 0, "max_inclusive": 14 },
	"floor_level": { "type": "minecraft:uniform", "min_inclusive": -1.0, "max_exclusive": -0.4 },
	"horizontal_radius_multiplier": { "type": "minecraft:uniform", "min_inclusive": 0.7, "max_exclusive": 1.4 },
	"probability": 0.15,
	"room_vertical_radius_multiplier": { "type": "minecraft:uniform", "min_inclusive": 0.1, "max_exclusive": 0.9 },
	"thickness": { "type": "minecraft:trapezoid", "min": 0.0, "max": 3.0, "plateau": 1.0 },
	"vertical_radius_multiplier": { "type": "minecraft:uniform", "min_inclusive": 0.8, "max_exclusive": 1.3 },
	"weird_thickness_bias": true,
	"y": {
		"type": "minecraft:uniform",
		"min_inclusive": { "above_bottom": 8 },
		"max_inclusive": { "absolute": 180 }
	}
}
```

Each function returns a `CarverArgument`, which is what a biome's `carvers` list takes. To capture that argument outside a `carvers`
block, call the same function on `carversBuilder`:

```kotlin
val cave = carversBuilder.cave("my_cave") { probability = 0.15 }
```

## Shared Configuration

| Field         | Type                                                                     | Default               | Vanilla                                                                                                                 | Meaning                                                              |
|---------------|--------------------------------------------------------------------------|-----------------------|-------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------|
| `probability` | `Double` in `[0, 1]`                                                     | `0.1`                 | cave `0.15`, canyon `0.01`                                                                                              | Chance for each chunk to attempt a carve. Higher carves more chunks. |
| `y`           | [Height provider](/docs/data-driven/worldgen/providers#vertical-anchors) | `constantAbsolute(0)` | cave `uniformHeightProvider(aboveBottom(8), absolute(180))`, canyon `uniformHeightProvider(absolute(10), absolute(67))` | Height at which the carve starts.                                    |

## Cave

| Field                           | Type                            | Default        | Vanilla                     | Meaning                                                                       |
|---------------------------------|---------------------------------|----------------|-----------------------------|-------------------------------------------------------------------------------|
| `count`                         | Int provider, `0` or more       | `constant(1)`  | `veryBiasedToBottom(0, 14)` | Tunnels per chunk attempt. Higher means more tunnels.                         |
| `floorLevel`                    | Float provider in `[-1.0, 1.0]` | `constant(0f)` | `uniform(-1f, -0.4f)`       | `0.0` carves ellipsoids, `1.0` carves upper half-ellipsoids for a flat floor. |
| `horizontalRadiusMultiplier`    | Float provider                  | `constant(1f)` | `uniform(0.7f, 1.4f)`       | Horizontal scaling of tunnels. Higher means wider tunnels, not longer ones.   |
| `roomVerticalRadiusMultiplier`  | Float provider                  | `constant(1f)` | `uniform(0.1f, 0.9f)`       | Vertical scaling of the rooms tunnels start from. Higher means taller rooms.  |
| `startVerticalRadiusMultiplier` | Float provider, optional        | `null`         | absent                      | Vertical scaling of the first tunnel segment. Vanilla uses `1.0` when absent. |
| `thickness`                     | Float provider, `0` or more     | `constant(1f)` | `trapezoid(0f, 3f, 1f)`     | Breadth and height of tunnels. Higher means bigger tunnels.                   |
| `verticalRadiusMultiplier`      | Float provider                  | `constant(1f)` | `uniform(0.8f, 1.3f)`       | Vertical scaling of tunnels. Higher means taller tunnels, not longer ones.    |
| `weirdThicknessBias`            | `Boolean`, optional             | `null`         | `true`                      | Irregular tunnel thickness. Vanilla uses `false` when absent.                 |

The Vanilla column is the `cave` carver. `cave_extra_underground` is the same, except for a `probability` of `0.07` and a `y` of
`uniformHeightProvider(aboveBottom(8), absolute(47))`.

## Canyon

`canyon` adds a vertical rotation and a `shape` object, configured with the `shape` block:

| Field                         | Type                    | Default        | Vanilla                    | Meaning                                                               |
|-------------------------------|-------------------------|----------------|----------------------------|-----------------------------------------------------------------------|
| `verticalRotation`            | Float provider          | `constant(0f)` | `uniform(-0.125f, 0.125f)` | Vertical rotation applied as the canyon extends.                      |
| `distanceFactor`              | Float provider          | `constant(1f)` | `uniform(0.75f, 1f)`       | Length of the canyon. Higher is longer.                               |
| `horizontalRadiusFactor`      | Float provider          | `constant(1f)` | `uniform(0.75f, 1f)`       | Breadth of the canyon. Higher is wider.                               |
| `thickness`                   | Float provider          | `constant(1f)` | `trapezoid(0f, 6f, 2f)`    | Breadth and height of the canyon. Higher means a bigger canyon.       |
| `verticalRadiusCenterFactor`  | `Float`                 | `0f`           | `0f`                       | Extra depth based on horizontal distance from the canyon center.      |
| `verticalRadiusDefaultFactor` | `Float`                 | `1f`           | `1f`                       | Depth of the canyon. Higher is deeper.                                |
| `widthSmoothness`             | `Int`, greater than `0` | `1`            | `3`                        | Smoothing of the walls along the vertical axis.                       |
| `yScale`                      | Float provider          | `constant(1f)` | `constant(3f)`             | Vertical scaling of the carved shape. Higher stretches it vertically. |

All fields except `verticalRotation` belong to `shape`.

```kotlin
canyon("my_canyon") {
	probability = 0.02
	verticalRotation = clampedNormal(0.0f, 1.0f, -1.0f, 1.0f)
	y = constantAboveBottom(65)

	shape {
		thickness = trapezoid(0.0f, 6.0f, 2.0f)
		widthSmoothness = 3
		yScale = constant(3.0f)
	}
}
```

```json
{
	"type": "minecraft:canyon",
	"probability": 0.02,
	"shape": {
		"distance_factor": 1.0,
		"horizontal_radius_factor": 1.0,
		"thickness": { "type": "minecraft:trapezoid", "min": 0.0, "max": 6.0, "plateau": 2.0 },
		"vertical_radius_center_factor": 0.0,
		"vertical_radius_default_factor": 1.0,
		"width_smoothness": 3,
		"y_scale": 3.0
	},
	"vertical_rotation": { "type": "minecraft:clamped_normal", "mean": 0.0, "deviation": 1.0, "min": -1.0, "max": 1.0 },
	"y": { "above_bottom": 65 }
}
```

## Using Carvers In A Biome

A biome's `carvers` field is a flat list of carver IDs, or a single carver tag:

```kotlin
val cave = carversBuilder.cave("highlands_cave") { probability = 0.08 }

biome("highlands") {
	carvers(cave, Carvers.CANYON)
}
```

```json
{
	"carvers": ["mypack:highlands_cave", "minecraft:canyon"]
}
```

`Carvers` lists the vanilla carvers: `CANYON`, `CAVE`, `CAVE_EXTRA_UNDERGROUND` and `NETHER_CAVE`. The field also accepts a
`CarverTagArgument`, built with `carverTag`.

## See Also

- [Biomes](/docs/data-driven/worldgen/biomes) - listing the carvers a biome runs
- [Noise & Terrain](/docs/data-driven/worldgen/noise) - the terrain shape the carvers cut through
- [Providers](/docs/data-driven/worldgen/providers) - height, int and float providers
- [World Generation](/docs/data-driven/worldgen) - overview of the worldgen system
