---
root: .components.layouts.MarkdownLayout
title: Biomes
nav-title: Biomes
description: Define Minecraft biomes with climate, colors, mob spawns, carvers and features using Kore's type-safe Kotlin DSL.
keywords: minecraft, datapack, kore, worldgen, biome, carver, spawner, biome effects, climate parameters
date-created: 2026-02-03
date-modified: 2026-10-20
routeOverride: /docs/data-driven/worldgen/biomes
---

# Biomes

A biome describes a region of the world: its climate, its colors and sounds, which mobs spawn there, which carvers dig through it and which
features decorate it. It does not decide where it appears - that is the job of the [biome source](/docs/data-driven/worldgen/dimensions) of
a dimension.

```kotlin
val myBiome = dp.biome("my_biome") {
	temperature = 0.8f
	downfall = 0.4f
	hasPrecipitation = true

	attributes {
		skyColor(0x78A7FF)
		fogColor(0xC0D8FF)
	}

	effects {
		waterColor = color(0x3F76E4)
	}
}
```

References: [Biome](https://minecraft.wiki/w/Biome), [Biome definition](https://minecraft.wiki/w/Biome_definition), misode's
[biome generator](https://misode.github.io/worldgen/biome/)

## Where Biomes Land In The World

The vanilla Overworld places biomes in a 6-dimensional climate space. Each biome claims an interval on every axis, and the game picks the
closest match for a position. These are the same six parameters a `multiNoise` biome source lists per entry:

| Parameter         | Controls                                            | Vanilla range                                                                                                                                                                    |
|-------------------|-----------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `temperature`     | Snow and ice coverage, vegetation types             | 5 bands: `-1.0..-0.45`, `-0.45..-0.15`, `-0.15..0.2`, `0.2..0.55`, `0.55..1.0`                                                                                                   |
| `humidity`        | Vegetation density, arid to humid                   | 5 bands: `-1.0..-0.35`, `-0.35..-0.1`, `-0.1..0.1`, `0.1..0.3`, `0.3..1.0`                                                                                                       |
| `continentalness` | Ocean, beach or inland                              | Mushroom fields `-1.2..-1.05`, deep ocean `-1.05..-0.455`, ocean `-0.455..-0.19`, coast `-0.19..-0.11`, near inland `-0.11..0.03`, mid inland `0.03..0.3`, far inland `0.3..1.0` |
| `erosion`         | Flat versus mountainous terrain                     | 7 bands: `-1.0..-0.78`, `-0.78..-0.375`, `-0.375..-0.2225`, `-0.2225..0.05`, `0.05..0.45`, `0.45..0.55`, `0.55..1.0`                                                             |
| `weirdness`       | Biome variants, for example Jungle to Bamboo Jungle | `-1.0..1.0`, cut into slices at `±0.05`, `±0.2667`, `±0.4`, `±0.5667`, `±0.7667` and `±0.9333`. A slice entirely below `0.0` picks the standard biome, the others its variant    |
| `depth`           | Surface versus cave biome                           | Surface biomes at `0.0` and `1.0`, cave biomes in `0.2..0.9`, `deep_dark` at `1.1`                                                                                               |

The vanilla Overworld uses the built-in `overworld` multi-noise preset, so these bands are fixed in the game and not in a datapack file
(bounds above are rounded to four decimals). In its table of standard middle-terrain biomes, `snowy_plains` sits at temperature
`-1.0..-0.45` with humidity `-1.0..0.1`, `dark_forest` at temperature `-0.15..0.2` with humidity `0.3..1.0`, and `desert` at temperature
`0.55..1.0` with every humidity.

The `temperature` and `downfall` fields on the biome itself are a different thing: they drive in-world behavior such as snow instead of
rain, water freezing, and cauldrons filling.

## Climate Fields

| Field                      | Type      | Default | Vanilla                                              | Effect                                                                   |
|----------------------------|-----------|---------|------------------------------------------------------|--------------------------------------------------------------------------|
| `temperature`              | `Float`   | `0.8`   | `plains` `0.8`, `snowy_plains` `0.0`, `desert` `2.0` | Below `0.15`, precipitation falls as snow and water freezes.             |
| `downfall`                 | `Float`   | `0.4`   | `plains` `0.4`, `snowy_plains` `0.5`, `desert` `0.0` | Wetness, driving foliage tinting and how fast fire spreads.              |
| `hasPrecipitation`         | `Boolean` | `true`  | `desert` `false`                                     | Whether rain or snow falls at all.                                       |
| `temperatureModifier`      | enum      | none    | `frozen_ocean` `FROZEN`                              | `FROZEN` applies the noise-based warm patches of the frozen ocean.       |
| `creatureSpawnProbability` | `Float?`  | none    | `snowy_plains` `0.07`                                | Chance per chunk for the initial creature spawn during world generation. |

## Colors And Atmosphere

Two blocks share this job, and the split matters:

- `attributes { }` covers everything the camera sees or hears - sky, fog, particles, music, ambient sounds - plus gameplay rules. It is the
  same system dimension types use, so a biome value simply overrides the dimension value. See
  [Environment Attributes](/docs/data-driven/worldgen/environment-attributes).
- `effects { }` covers the biome-local block and liquid tints, which are not attributes.

```kotlin
attributes {
	skyColor(0x78A7FF)
	fogColor(0xC0D8FF, EnvironmentAttributeModifier.ADD)
}

effects {
	waterColor = color(0x3F76E4)       // Defaults to the vanilla overworld blue.
	foliageColor = color(0x59AE30)
	dryFoliageColor = color(0xA0A04C)
	grassColor = color(0x79C05A)
	grassColorModifier = GrassColorModifier.SWAMP
}
```

Colors serialize as decimal integers. `grassColorModifier` applies the vanilla `SWAMP` or `DARK_FOREST` post-processing on top of the
computed grass color.

Reference: [Biome definition - Effects](https://minecraft.wiki/w/Biome_definition#Effects)

## Mob Spawns

`spawners` declares what may spawn per mob category. Within a category, `weight` is a relative probability against the other entries, and
`minCount`/`maxCount` bound the size of a spawned group.

```kotlin
spawners {
	creature {
		spawner(EntityTypes.COW, weight = 6, minCount = 2, maxCount = 4)
		spawner(EntityTypes.SHEEP, weight = 8, minCount = 2, maxCount = 4)
	}
	monster {
		spawner(EntityTypes.SKELETON, weight = 80, minCount = 1, maxCount = 2)
		spawner(EntityTypes.ZOMBIE, weight = 80, minCount = 1, maxCount = 2)
	}
}
```

Categories: `ambient`, `axolotls`, `creature`, `misc`, `monster`, `undergroundWaterCreature`, `waterAmbient`, `waterCreature`.

`spawnCosts` caps density with an energy budget instead of a raw count. Each mob spends `charge` from a shared `energyBudget`, so a crowded
area stops spawning before it becomes a mob farm.

```kotlin
spawnCosts {
	spawnCost(EntityTypes.COW, energyBudget = 1.2f, charge = 0.1f)
}
```

References: [Mob spawning](https://minecraft.wiki/w/Biome_definition#Mob_spawning),
[Spawn costs](https://minecraft.wiki/w/Biome_definition#Spawn_costs)

## Carvers

`carvers` is a flat list of carver IDs, or a single carver tag. They run during the `carvers` step, after the terrain noise and before any
feature.

```kotlin
carvers(myCaveCarver, Carvers.CAVE)
```

See [Carvers](/docs/data-driven/worldgen/carvers).

## Features

`features` lists placed features per [decoration step](/docs/data-driven/worldgen#decoration-steps). Every property is a plain list, and
leaving one empty simply means nothing of yours generates at that step.

```kotlin
features {
	fluidSprings = listOf(PlacedFeatures.SPRING_WATER)
	lakes = listOf(PlacedFeatures.LAKE_LAVA_UNDERGROUND)
	localModifications = listOf(PlacedFeatures.AMETHYST_GEODE)
	surfaceStructures = listOf(PlacedFeatures.DESERT_WELL)
	topLayerModification = listOf(PlacedFeatures.FREEZE_TOP_LAYER)
	undergroundDecoration = listOf(PlacedFeatures.GLOW_LICHEN)
	undergroundOres = listOf(orePlaced)
	undergroundStructures = listOf(PlacedFeatures.MONSTER_ROOM)
	vegetalDecoration = listOf(treePlaced, flowerPlaced)
}
```

See [Features](/docs/data-driven/worldgen/features).

## Complete Example

```kotlin
fun DataPack.highlandsBiome(treePlaced: PlacedFeatureArgument, orePlaced: PlacedFeatureArgument) {
	val cave = carversBuilder.cave("highlands_cave") {
		probability = 0.08
		y = uniformHeightProvider(absolute(32), absolute(128))
		floorLevel = constant(-0.2f)
	}

	biome("highlands") {
		temperature = 0.8f
		downfall = 0.3f
		hasPrecipitation = true

		attributes {
			fogColor(0xBFEFFF)
			skyColor(0x99D9FF)
			waterFogColor(0x0A2C4F)
		}

		effects {
			waterColor = color(0x34A7F0)
		}

		spawners {
			creature {
				spawner(EntityTypes.COW, 6, 2, 4)
				spawner(EntityTypes.SHEEP, 8, 2, 4)
			}
			monster {
				spawner(EntityTypes.SKELETON, 80, 1, 2)
			}
		}

		spawnCosts {
			spawnCost(EntityTypes.COW, 1.2f, 0.1f)
		}

		carvers(cave)

		features {
			undergroundOres = listOf(orePlaced)
			vegetalDecoration = listOf(treePlaced)
		}
	}
}
```

## See Also

- [Carvers](/docs/data-driven/worldgen/carvers) - the caves and canyons a biome runs
- [Dimensions](/docs/data-driven/worldgen/dimensions) - biome sources placing biomes in a world
- [Environment Attributes](/docs/data-driven/worldgen/environment-attributes) - the full attribute reference
- [Features](/docs/data-driven/worldgen/features) - the placed features a biome lists
- [World Generation](/docs/data-driven/worldgen) - overview of the worldgen system
