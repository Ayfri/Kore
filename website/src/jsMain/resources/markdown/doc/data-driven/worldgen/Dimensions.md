---
root: .components.layouts.MarkdownLayout
title: Dimensions
nav-title: Dimensions
description: Create custom dimensions and dimension types with Kore's DSL.
keywords: minecraft, datapack, kore, worldgen, dimension, dimension type, generator
date-created: 2026-02-03
date-modified: 2026-02-04
routeOverride: /docs/data-driven/worldgen/dimensions
---

# Dimensions

Dimensions are complete, separate worlds within Minecraft. Each dimension combines a **dimension type** (world rules like height, lighting,
and behavior) with a **generator** (how terrain is created). Vanilla Minecraft has three dimensions: Overworld, Nether, and End.

With datapacks, you can create unlimited custom dimensions with unique terrain, rules, and atmosphere. Players can travel between dimensions
using portals or commands.

References: [Dimension](https://minecraft.wiki/w/Dimension), [Dimension definition](https://minecraft.wiki/w/Dimension_definition), [Custom dimension](https://minecraft.wiki/w/Custom_dimension)

---

## Dimension Type

Dimension types define the fundamental rules of a world: vertical bounds, lighting behavior, time flow, and special mechanics. These
settings affect gameplay significantly-for example, use environment attributes like `waterEvaporates` and `fastLava` to make a Nether-like
environment. See [Environment Attributes](/docs/data-driven/worldgen/environment-attributes) for the full list of attributes and modifiers.

Reference: [Dimension type](https://minecraft.wiki/w/Dimension_type)

```kotlin
val myDimType = dp.dimensionType("my_dim_type") {
	ambientLight = 0f
	hasCeiling = false
	hasSkylight = true
	height = 384
	logicalHeight = 384
	minY = -64
	natural = true

	attributes {
		canStartRaid(true)
		respawnAnchorWorks(false)
		piglinsZombify(true)
		waterEvaporates(false)
		fastLava(false)
		increasedFireBurnout(false)
		bedRule(
			BedRule(
				canSleep = BedSleepRule.ALWAYS,
				canSetSpawn = BedSleepRule.ALWAYS,
				explodes = false,
			)
		)
	}
}
```

### Dimension Type Properties

| Property        | Description                                          |
|-----------------|------------------------------------------------------|
| `ambientLight`  | Base light level (0.0 to 1.0)                        |
| `attributes`    | Environment attributes (visual/audio/gameplay rules) |
| `effects`       | Visual effects (overworld/nether/end)                |
| `hasCeiling`    | Whether dimension has bedrock ceiling                |
| `hasSkylight`   | Whether sky provides light                           |
| `height`        | Total height (multiple of 16, max 4064)              |
| `infiniburn`    | Block tag for infinite burning                       |
| `logicalHeight` | Max height for teleportation/portals                 |
| `minY`          | Minimum Y coordinate (multiple of 16)                |
| `natural`       | Compasses/clocks work normally                       |

---

## Dimension

A dimension combines a dimension type with a generator that produces terrain. The generator determines the terrain algorithm and biome
distribution.

```kotlin
val dim = dp.dimension("my_dimension", type = myDimType) {
	// Choose a generator (see below)
}
```

### Noise Generator

The noise generator is the standard terrain generator used by vanilla dimensions. It combines **noise settings** (terrain shape algorithm)
with a **biome source** (which biomes appear where).

Reference: [Noise generator](https://minecraft.wiki/w/Dimension_definition#Noise_generator)

```kotlin
dimension("my_dimension", type = myDimType) {
	noiseGenerator(
		settings = myNoiseSettings,
		biomeSource = /* BiomeSource */
	)
}
```

#### Biome Sources

Biome sources determine how biomes are distributed across the dimension. Different sources suit different use cases:

Reference: [Biome source](https://minecraft.wiki/w/Biome_source)

```kotlin
// Single biome everywhere (simplest option)
noiseGenerator(
	settings = terrain,
	biomeSource = fixed(myBiome)
)

// Checkerboard pattern
noiseGenerator(
	settings = terrain,
	biomeSource = checkerboard(scale = 3, biome1, biome2, biome3)
)

// Multi-noise (vanilla-like biome distribution)
noiseGenerator(
	settings = terrain,
	biomeSource = multiNoise {
		// biome entries with climate parameters
	}
)

// The End biome source
noiseGenerator(
	settings = terrain,
	biomeSource = theEnd()
)
```

### Flat Generator

The flat generator creates superflat worlds with user-defined block layers. Useful for testing, creative building, or specialized gameplay.

Reference: [Superflat](https://minecraft.wiki/w/Superflat)

```kotlin
dimension("flat_world", type = myDimType) {
	flatGenerator(biome = Biomes.PLAINS) {
		layers {
			layer(Blocks.BEDROCK, height = 1)
			layer(Blocks.DIRT, height = 2)
			layer(Blocks.GRASS_BLOCK, height = 1)
		}
		// structureOverrides = ...
	}
}
```

### Debug Generator

The debug generator creates a world showing every block state in a grid pattern. Primarily used for development and testing.

Reference: [Debug mode](https://minecraft.wiki/w/Debug_mode)

```kotlin
dimension("debug_world", type = myDimType) {
	debugGenerator()
}
```

---

## Complete Example

```kotlin
fun DataPack.createSkyDimension() {
	// 1) Dimension type with high ambient light
	val skyType = dimensionType("sky_type") {
		ambientLight = 0.5f
		hasCeiling = false
		hasSkylight = true
		height = 256
		logicalHeight = 256
		minY = 0
		natural = true

		attributes {
			canStartRaid(false)
			respawnAnchorWorks(false)
			piglinsZombify(true)
			waterEvaporates(false)
			fastLava(false)
			increasedFireBurnout(false)
			bedRule(
				BedRule(
					canSleep = BedSleepRule.ALWAYS,
					canSetSpawn = BedSleepRule.ALWAYS,
					explodes = false,
				)
			)
		}
	}

	// 2) Simple noise settings
	val skyTerrain = noiseSettings("sky_terrain") {
		noiseOptions(minY = 0, height = 256, sizeHorizontal = 1, sizeVertical = 2)
		defaultBlock(Blocks.STONE) {}
		defaultFluid(Blocks.WATER) { this["level"] = "0" }
	}

	// 3) Create a biome
	val skyBiome = biome("sky_biome") {
		temperature = 0.5f
		downfall = 0.0f
		hasPrecipitation = false

		attributes {
			skyColor(0xFFFFFF)
			fogColor(0xFFFFFF)
			waterFogColor(0x050533)
		}

		effects {
			waterColor = color(0x3F76E4)
		}
	}

	// 4) Create the dimension
	dimension("sky", type = skyType) {
		noiseGenerator(
			biomeSource = fixed(skyBiome),
			settings = skyTerrain,
		)
	}
}
```

## See Also

- [Biomes](/docs/data-driven/worldgen/biomes) - Climate, visuals, mob spawns, and features
- [Environment Attributes](/docs/data-driven/worldgen/environment-attributes) - Full reference for visual, audio, and gameplay attributes
- [World Presets](/docs/data-driven/worldgen/world-presets) - World presets and flat level generator presets
- [World Generation](/docs/data-driven/worldgen) - Overview of the worldgen system
