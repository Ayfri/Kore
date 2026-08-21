---
root: .components.layouts.MarkdownLayout
title: Dimensions
nav-title: Dimensions
description: Create custom dimensions and dimension types with Kore's DSL.
keywords: minecraft, datapack, kore, worldgen, dimension, dimension type, generator
date-created: 2026-02-03
date-modified: 2026-08-21
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

Every property defaults to its vanilla overworld value, so a dimension type only declares what it changes.

| Property                      | Default                 | Description                                                              |
|-------------------------------|-------------------------|--------------------------------------------------------------------------|
| `ambientLight`                | `0f`                    | Minimum light level everywhere, from `0` to `1`.                         |
| `attributes`                  | none                    | Environment attributes (visual, audio and gameplay rules).               |
| `cardinalLight`               | `DEFAULT`               | `CardinalLight.DEFAULT` or `NETHER`, which flattens the shading.         |
| `coordinateScale`             | `1.0`                   | Coordinate multiplier when travelling to the dimension.                  |
| `defaultClock`                | none                    | World clock driving the day cycle, none freezing the time.               |
| `hasCeiling`                  | `false`                 | Whether the world has a bedrock ceiling.                                 |
| `hasEnderDragonFight`         | omitted                 | Whether the Ender Dragon fight can happen in the world.                  |
| `hasFixedTime`                | `false`                 | Whether the time of day is frozen.                                       |
| `hasSkylight`                 | `true`                  | Whether the sky lights the world.                                        |
| `height`                      | `384`                   | Total height, a multiple of `16`, from `16` to `4064`.                   |
| `infiniburn`                  | `#infiniburn_overworld` | Block tag listing the blocks that burn forever.                          |
| `logicalHeight`               | `384`                   | How high chorus fruits and portals bring a player, at most `height`.     |
| `minY`                        | `-64`                   | Lowest buildable Y, a multiple of `16`, from `-2032` to `2031`.          |
| `monsterSpawnBlockLightLimit` | `0`                     | Block light level at or below which monsters spawn, from `0` to `15`.    |
| `monsterSpawnLightLevel`      | `constant(0)`           | Sky light levels at which monsters spawn, from `0` to `15`.              |
| `skybox`                      | `OVERWORLD`             | `SkyboxType.NONE`, `OVERWORLD` or `END`.                                 |
| `timelines`                   | none                    | List of [timelines](/docs/data-driven/timelines) or timeline tags.       |

There is no `natural`, `ultrawarm`, `piglinSafe`, `bedWorks`, `respawnAnchorWorks`, `hasRaids` or `effects` property: everything they used
to control is now an [environment attribute](/docs/data-driven/worldgen/environment-attributes).

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
			layer(Blocks.BEDROCK)
			layer(Blocks.DIRT, height = 2)
			layer(Blocks.GRASS_BLOCK)
		}
		structureOverrides(StructureSets.VILLAGES)
		features = true
		lakes = false
	}
}
```

| Field                | Type                     | Default              | Description                                            |
|----------------------|--------------------------|----------------------|--------------------------------------------------------|
| `biome`              | Biome                    | mandatory            | The biome every chunk of the world uses.               |
| `lakes`              | `Boolean?`               | `false`              | Whether lava lakes generate.                           |
| `features`           | `Boolean?`               | `false`              | Whether the biome placed features generate.            |
| `layers`             | `List<Layer>`            | empty                | Block layers, read from the bottom of the world up.    |
| `structureOverrides` | Structure sets or a tag  | every structure set  | The only structure sets allowed to generate.           |

Layers are declared in three interchangeable ways: `layer(block, height)` appends one layer to whatever is already there, `layers(...)`
takes them as varargs, and `layers { }` builds the whole stack at once. `height` defaults to `1` and goes up to `4064`.
`structureOverrides` takes structure sets as varargs, a builder block, or a single structure set tag.

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
