---
root: .components.layouts.MarkdownLayout
title: World Presets
nav-title: World Presets
description: Create world presets and flat level generator presets with Kore's DSL.
keywords: minecraft, datapack, kore, worldgen, world preset, flat, superflat
date-created: 2026-02-03
date-modified: 2026-02-04
routeOverride: /docs/data-driven/worldgen/world-presets
---

# World Presets

World presets define complete world configurations that appear in the "World Type" dropdown during world creation. They specify which
dimensions exist and how each generates terrain. Vanilla presets include Default, Superflat, Large Biomes, Amplified, and Single Biome.

Custom world presets let you offer players pre-configured world types with your custom dimensions, terrain, and biomes.

Reference: [World preset](https://minecraft.wiki/w/World_preset)

---

## World Preset

A world preset defines the complete dimension configuration for a world. At minimum, it should include an Overworld dimension, but can also
customize or replace the Nether and End, or add entirely new dimensions.

```kotlin
dp.worldPreset("my_preset") {
	dimension(DimensionTypes.OVERWORLD) {
		type = myDimType
		// Generator configuration
	}
	// Optionally add NETHER, END, or custom dimensions
}
```

### Basic Example

```kotlin
dp.worldPreset("custom_world") {
	// Overworld with custom terrain
	dimension(DimensionTypes.OVERWORLD) {
		type = myOverworldType
		noiseGenerator(
			settings = myNoiseSettings,
			biomeSource = multiNoise { /* ... */ }
		)
	}

	// Standard Nether
	dimension(DimensionTypes.THE_NETHER) {
		type = DimensionTypes.THE_NETHER
		noiseGenerator(
			settings = NoiseSettings.NETHER,
			biomeSource = multiNoise { /* ... */ }
		)
	}

	// Standard End
	dimension(DimensionTypes.THE_END) {
		type = DimensionTypes.THE_END
		noiseGenerator(
			settings = NoiseSettings.END,
			biomeSource = theEnd()
		)
	}
}
```

### Custom Dimension in Preset

```kotlin
dp.worldPreset("aether_world") {
	// Replace Overworld with custom dimension
	dimension(DimensionTypes.OVERWORLD) {
		type = aetherDimType
		noiseGenerator(
			settings = aetherNoise,
			biomeSource = checkerboard(scale = 3, highlands, forest, shores)
		)
	}
}
```

---

## Flat Level Generator Preset

Flat level generator presets appear in the Superflat customization screen, offering quick-select layer configurations. Vanilla presets
include Classic Flat, Tunnelers' Dream, Water World, and Redstone Ready.

Reference: [Superflat - Presets](https://minecraft.wiki/w/Superflat#Presets)

```kotlin
val flatPreset = dp.flatLevelGeneratorPreset("classic_flat") {
	// Display item in UI
	// displayItem = Items.GRASS_BLOCK

	// Flat world settings
	// layers, biome, structure overrides
}
```

### Flat Generator Layers

When using a flat generator in a dimension:

```kotlin
dimension("flat_world", type = myDimType) {
	flatGenerator(biome = Biomes.PLAINS) {
		layers {
			layer(Blocks.BEDROCK, height = 1)
			layer(Blocks.STONE, height = 3)
			layer(Blocks.DIRT, height = 3)
			layer(Blocks.GRASS_BLOCK, height = 1)
		}
		// structureOverrides = ...
	}
}
```

---

## Complete Example

```kotlin
fun DataPack.createSkylandsPreset() {
	// 1) Custom dimension type
	val skyType = dimensionType("skylands_type") {
		minY = 0
		height = 256
		hasSkylight = true
		hasCeiling = false
		natural = true
		ambientLight = 0.1f

		attributes {
			canStartRaid(true)
			bedRule(
				BedRule(
					canSleep = BedSleepRule.ALWAYS,
					canSetSpawn = BedSleepRule.ALWAYS,
					explodes = false,
				)
			)
		}
	}

	// 2) Noise settings
	val skyNoise = noiseSettings("skylands_noise") {
		noiseOptions(minY = 0, height = 256, sizeHorizontal = 2, sizeVertical = 1)
		defaultBlock(Blocks.STONE) {}
		defaultFluid(Blocks.WATER) { this["level"] = "0" }
	}

	// 3) Biome
	val skyBiome = biome("skylands_biome") {
		temperature = 0.5f
		downfall = 0.5f
		hasPrecipitation = true

		attributes {
			skyColor(0x87CEEB)
			fogColor(0xC0D8FF)
			waterFogColor(0x050533)
		}

		effects {
			waterColor = color(0x3F76E4)
		}
	}

	// 4) World preset
	worldPreset("skylands") {
		dimension(DimensionTypes.OVERWORLD) {
			type = skyType
			noiseGenerator(
				settings = skyNoise,
				biomeSource = fixed(skyBiome)
			)
		}
	}
}
```

## See Also

- [Biomes](/docs/data-driven/worldgen/biomes) - Climate, visuals, mob spawns, and features
- [Dimensions](/docs/data-driven/worldgen/dimensions) - Dimension types and generators
- [Environment Attributes](/docs/data-driven/worldgen/environment-attributes) - Visual, audio, and gameplay attributes for biomes and
  dimensions
- [World Generation](/docs/data-driven/worldgen) - Overview of the worldgen system
