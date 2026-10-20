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

Which blocks a carver removes isn't configurable because Minecraft 26.3 dropped `replaceable`.

References: [Carver definition](https://minecraft.wiki/w/Carver_definition), [Carver](https://minecraft.wiki/w/Carver)

## Declaring Carvers

`carvers` opens a scope where `cave` and `canyon` each create exactly one file. Every field has a neutral default, so a carver only needs
the values that differ from it.

```kotlin
fun DataPack.myCarvers() {
	carvers {
		cave("my_cave") {
			count = uniform(0, 14)
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
	"count": { "type": "minecraft:uniform", "min_inclusive": 0, "max_inclusive": 14 },
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

| Field         | Type                                                    | Default               | Meaning                                   |
|---------------|---------------------------------------------------------|-----------------------|-------------------------------------------|
| `probability` | `Double` in `[0, 1]`                                    | `0.1`                 | Chance for each chunk to attempt a carve. |
| `y`           | [Height provider](/docs/data-driven/worldgen/providers) | `constantAbsolute(0)` | Height at which the carve starts.         |

## Cave

| Field                           | Type                            | Default        | Meaning                                                                       |
|---------------------------------|---------------------------------|----------------|-------------------------------------------------------------------------------|
| `count`                         | Int provider, `0` or more       | `constant(1)`  | Tunnels per chunk attempt.                                                    |
| `floorLevel`                    | Float provider in `[-1.0, 1.0]` | `constant(0f)` | `0.0` carves ellipsoids, `1.0` carves upper half-ellipsoids for a flat floor. |
| `horizontalRadiusMultiplier`    | Float provider                  | `constant(1f)` | Horizontal scaling of tunnels. Does not change their length.                  |
| `roomVerticalRadiusMultiplier`  | Float provider                  | `constant(1f)` | Vertical scaling of the rooms tunnels start from.                             |
| `startVerticalRadiusMultiplier` | Float provider, optional        | `null`         | Vertical scaling of the first tunnel segment. Vanilla uses `1.0` when absent. |
| `thickness`                     | Float provider, `0` or more     | `constant(1f)` | Breadth and height of tunnels.                                                |
| `verticalRadiusMultiplier`      | Float provider                  | `constant(1f)` | Vertical scaling of tunnels. Does not change their length.                    |
| `weirdThicknessBias`            | `Boolean`, optional             | `null`         | Irregular tunnel thickness. Vanilla uses `false` when absent.                 |

## Canyon

`canyon` adds a vertical rotation and a `shape` object, configured with the `shape` block:

| Field                         | Type                    | Default        | Meaning                                                          |
|-------------------------------|-------------------------|----------------|------------------------------------------------------------------|
| `verticalRotation`            | Float provider          | `constant(0f)` | Vertical rotation applied as the canyon extends.                 |
| `distanceFactor`              | Float provider          | `constant(1f)` | Length of the canyon. Higher is longer.                          |
| `horizontalRadiusFactor`      | Float provider          | `constant(1f)` | Breadth of the canyon. Higher is wider.                          |
| `thickness`                   | Float provider          | `constant(1f)` | Breadth and height of the canyon.                                |
| `verticalRadiusCenterFactor`  | `Float`                 | `0f`           | Extra depth based on horizontal distance from the canyon center. |
| `verticalRadiusDefaultFactor` | `Float`                 | `1f`           | Depth of the canyon. Higher is deeper.                           |
| `widthSmoothness`             | `Int`, greater than `0` | `1`            | Smoothing of the walls along the vertical axis.                  |
| `yScale`                      | Float provider          | `constant(1f)` | Vertical scaling of the carved shape.                            |

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
