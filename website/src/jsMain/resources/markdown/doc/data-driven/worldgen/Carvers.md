---
root: .components.layouts.MarkdownLayout
title: Carvers
nav-title: Carvers
description: Carve caves, nether caves and canyons into Minecraft terrain with Kore - probability, height, radius and debug settings.
keywords: minecraft, datapack, kore, worldgen, carver, cave, canyon, ravine
date-created: 2026-08-17
date-modified: 2026-08-21
routeOverride: /docs/data-driven/worldgen/carvers
---

# Carvers

Carvers hollow out terrain after the noise and surface steps but before features, which is why caves never destroy trees or ores placed
later. A configured carver pairs a carver type with its configuration and lives in
`data/<ns>/worldgen/configured_carver/<name>.json`. Biomes reference configured carvers by ID.

Three carver types exist:

- `cave` - Winding tunnel systems, sometimes branching out of a circular room.
- `nether_cave` - Same algorithm, wider tunnels, and aquifers do not apply: everything carved below `bottom_y + 32` fills with lava.
- `canyon` - Deep ravines with steep walls.

References: [Carver definition](https://minecraft.wiki/w/Carver_definition), [Carver](https://minecraft.wiki/w/Carver)

## Declaring Carvers

`configuredCarvers` opens a scope where `cave`, `netherCave`, and `canyon` each create exactly one file, so a configured carver can only
ever hold the config it was declared with. Every field has a neutral default, so a carver only needs the values that differ from it.

```kotlin
fun DataPack.myCarvers() {
	configuredCarvers {
		cave("my_cave") {
			probability = 0.15
			y = uniformHeightProvider(aboveBottom(8), absolute(180))
			yScale = uniform(0.1f, 0.9f)
			replaceable(Blocks.STONE, Blocks.DIRT, Tags.Block.BASE_STONE_OVERWORLD)
			horizontalRadiusMultiplier = uniform(0.7f, 1.4f)
			verticalRadiusMultiplier = uniform(0.8f, 1.3f)
			floorLevel = uniform(-1.0f, -0.4f)
		}
	}
}
```

```json
{
	"type": "minecraft:cave",
	"config": {
		"probability": 0.15,
		"y": {
			"type": "minecraft:uniform",
			"min_inclusive": { "above_bottom": 8 },
			"max_inclusive": { "absolute": 180 }
		},
		"yScale": { "type": "minecraft:uniform", "min_inclusive": 0.1, "max_exclusive": 0.9 },
		"lava_level": { "absolute": -54 },
		"replaceable": ["minecraft:stone", "minecraft:dirt", "#minecraft:base_stone_overworld"],
		"horizontal_radius_multiplier": { "type": "minecraft:uniform", "min_inclusive": 0.7, "max_exclusive": 1.4 },
		"vertical_radius_multiplier": { "type": "minecraft:uniform", "min_inclusive": 0.8, "max_exclusive": 1.3 },
		"floor_level": { "type": "minecraft:uniform", "min_inclusive": -1.0, "max_exclusive": -0.4 }
	}
}
```

Each function returns a `ConfiguredCarverArgument`, which is what a biome's `carvers` list takes. To capture that argument outside a
`configuredCarvers` block, call the same function on `configuredCarversBuilder`:

```kotlin
val cave = configuredCarversBuilder.cave("my_cave") { probability = 0.15 }
```

## Shared Configuration

Every carver type shares these fields:

| Field         | Type                                 | Default                               | Meaning                                                                          |
|---------------|--------------------------------------|---------------------------------------|----------------------------------------------------------------------------------|
| `probability` | `Double` in `[0, 1]`                 | `0.1`                                 | Chance for each chunk to attempt a carve.                                        |
| `y`           | [Height provider](#providers)        | `constantAbsolute(0)`                 | Height at which the carve starts.                                                |
| `yScale`      | [Float provider](#providers)         | `constant(1f)`                        | Vertical scaling of the carved shape.                                            |
| `lavaLevel`   | Vertical anchor                      | `absolute(-54)`                       | Y level at or below which carved areas fill with lava. Ignored by `nether_cave`. |
| `replaceable` | Block IDs and block tags             | the carver's vanilla replaceables tag | Blocks the carver is allowed to remove.                                          |

`replaceable` defaults to `Tags.Block.OVERWORLD_CARVER_REPLACEABLES` for `cave` and `canyon`, and to
`Tags.Block.NETHER_CARVER_REPLACEABLES` for `netherCave`. It serializes as a single string when it holds one entry and as an array
otherwise, and the `replaceable` helper replaces the whole list:

```kotlin
cave("my_cave") {
	replaceable(Blocks.STONE, Blocks.DIRT, Tags.Block.BASE_STONE_OVERWORLD)
}
```

Aquifers also fill carved areas, and always place lava below Y -54 regardless of `lavaLevel`.

## Cave And Nether Cave

`cave` and `netherCave` add three fields on top of the shared ones:

| Field                        | Type                            | Default        | Meaning                                                                       |
|------------------------------|---------------------------------|----------------|-------------------------------------------------------------------------------|
| `horizontalRadiusMultiplier` | Float provider                  | `constant(1f)` | Horizontal scaling of tunnels. Does not change their length.                  |
| `verticalRadiusMultiplier`   | Float provider                  | `constant(1f)` | Vertical scaling of tunnels. Does not change their length.                    |
| `floorLevel`                 | Float provider in `[-1.0, 1.0]` | `constant(0f)` | `0.0` carves ellipsoids, `1.0` carves upper half-ellipsoids for a flat floor. |

```kotlin
netherCave("my_nether_cave") {
	probability = 0.2
	y = uniformHeightProvider(aboveBottom(1), belowTop(1))
	lavaLevel = aboveBottom(31)
	floorLevel = constant(-0.7f)
}
```

`netherCave` produces `"type": "minecraft:nether_cave"`.

## Canyon

`canyon` adds a vertical rotation and a `shape` object, configured with the `shape` block:

| Field                         | Type                    | Default        | Meaning                                                          |
|-------------------------------|-------------------------|----------------|------------------------------------------------------------------|
| `verticalRotation`            | Float provider          | `constant(0f)` | Vertical rotation applied as the canyon extends.                 |
| `distanceFactor`              | Float provider          | `constant(1f)` | Length of the canyon. Higher is longer.                          |
| `thickness`                   | Float provider          | `constant(1f)` | Breadth and height of the canyon.                                |
| `widthSmoothness`             | `Int`, greater than `0` | `1`            | Smoothing of the walls along the vertical axis.                  |
| `horizontalRadiusFactor`      | Float provider          | `constant(1f)` | Breadth of the canyon. Higher is wider.                          |
| `verticalRadiusDefaultFactor` | `Float`                 | `1f`           | Depth of the canyon. Higher is deeper.                           |
| `verticalRadiusCenterFactor`  | `Float`                 | `0f`           | Extra depth based on horizontal distance from the canyon center. |

```kotlin
canyon("my_canyon") {
	probability = 0.02
	y = constantAboveBottom(65)
	yScale = constant(3.0f)
	lavaLevel = belowTop(10)
	verticalRotation = clampedNormal(0.0f, 1.0f, -1.0f, 1.0f)

	shape {
		distanceFactor = constant(0.5f)
		thickness = trapezoid(0.0f, 6.0f, 2.0f)
		widthSmoothness = 3
		horizontalRadiusFactor = uniform(0.75f, 1.0f)
		verticalRadiusDefaultFactor = 1.0f
		verticalRadiusCenterFactor = 0.0f
	}
}
```

## Debug Settings

`debugSettings` replaces the blocks a carver would place, which makes the carved volume visible without exploring it. It is the only part
of a carver config Minecraft treats as optional, and every state inside it defaults to acacia button.

```kotlin
canyon("my_canyon") {
	debugSettings {
		debugMode = true
		airState = blockState(Blocks.ACACIA_BUTTON, "face" to "floor")
		barrierState = blockState(Blocks.BARRIER)
		lavaState = blockState(Blocks.ORANGE_STAINED_GLASS)
		waterState = blockState(Blocks.BLUE_STAINED_GLASS)
	}
}
```

`airState` replaces carved air, `waterState` replaces water and waterlogs the block, `lavaState` replaces lava, and `barrierState`
replaces the barrier blocks aquifers generate.

## Providers

`lavaLevel` takes a vertical anchor (`absolute(y)`, `aboveBottom(n)`, `belowTop(n)`), `y` takes a height provider wrapping anchors into a
distribution, and every float field takes a float provider. All three families are scoped to the carver block, so they resolve inside
`cave { }`, `netherCave { }` and `canyon { }`:

```kotlin
cave("my_cave") {
	y = uniformHeightProvider(aboveBottom(8), absolute(180))
	lavaLevel = belowTop(10)
	yScale = uniform(0.1f, 0.9f)
	floorLevel = constant(-0.2f)
}
```

See [Providers](/docs/data-driven/worldgen/providers) for the full list of distributions.

## Using Carvers In A Biome

A biome's `carvers` field is a flat list of configured carver IDs, or a single carver tag:

```kotlin
val cave = configuredCarversBuilder.cave("highlands_cave") { probability = 0.08 }

biome("highlands") {
	carvers(cave, ConfiguredCarvers.CANYON)
}
```

```json
{
	"carvers": ["mypack:highlands_cave", "minecraft:canyon"]
}
```

`ConfiguredCarvers` lists the vanilla configured carvers: `CANYON`, `CAVE`, `CAVE_EXTRA_UNDERGROUND` and `NETHER_CAVE`. The field also
accepts a `ConfiguredCarverTagArgument`, though vanilla ships no carver tag.

## See Also

- [Biomes](/docs/data-driven/worldgen/biomes) - listing the carvers a biome runs
- [Noise & Terrain](/docs/data-driven/worldgen/noise) - the terrain shape the carvers cut through
- [Providers](/docs/data-driven/worldgen/providers) - height and float providers
- [World Generation](/docs/data-driven/worldgen) - overview of the worldgen system
