---
root: .components.layouts.MarkdownLayout
title: Dimensions
nav-title: Dimensions
description: Create custom Minecraft dimensions and dimension types with Kore - world bounds, lighting rules, terrain generators and biome sources.
keywords: minecraft, datapack, kore, worldgen, dimension, dimension type, generator, biome source, multi noise, superflat
date-created: 2026-02-03
date-modified: 2026-08-21
routeOverride: /docs/data-driven/worldgen/dimensions
---

# Dimensions

A dimension is a separate world, reachable with `/execute in <namespace>:<id>` or through a portal. It is always two things put together:

- A **dimension type** - the rules of the world: vertical bounds, lighting, time flow, and the environment attributes applying everywhere in
  it. Written to `data/<ns>/dimension_type/<name>.json`.
- A **generator** - how the terrain is built, and which biomes go where. Written inline in the dimension file itself.

```kotlin
val myDimType = dp.dimensionType("my_dim_type") {
	minY = -64
	height = 384
	hasSkylight = true
}

val myDim = dp.dimension("my_dimension", type = myDimType) {
	noiseGenerator(settings = myNoiseSettings, biomeSource = fixed(myBiome))
}
```

A datapack loaded into an existing world adds its dimensions on the next load; to replace the Overworld, the Nether or the End instead, use
a [world preset](/docs/data-driven/worldgen/world-presets).

References: [Dimension](https://minecraft.wiki/w/Dimension), [Custom dimension](https://minecraft.wiki/w/Custom_dimension)

---

## Dimension Type

Every property defaults to its vanilla Overworld value, so a dimension type only declares what it changes.

| Property                      | Default                 | Description                                                           |
|-------------------------------|-------------------------|-----------------------------------------------------------------------|
| `ambientLight`                | `0f`                    | Minimum light level everywhere, from `0` to `1`.                      |
| `attributes`                  | none                    | Environment attributes: visual, audio and gameplay rules.             |
| `cardinalLight`               | `DEFAULT`               | `CardinalLight.DEFAULT` or `NETHER`, which flattens the shading.      |
| `coordinateScale`             | `1.0`                   | Coordinate multiplier when travelling to the dimension.               |
| `defaultClock`                | none                    | World clock driving the day cycle, none freezing the time.            |
| `hasCeiling`                  | `false`                 | Whether the world has a bedrock ceiling.                              |
| `hasEnderDragonFight`         | omitted                 | Whether the Ender Dragon fight can happen in the world.               |
| `hasFixedTime`                | `false`                 | Whether the time of day is frozen.                                    |
| `hasSkylight`                 | `true`                  | Whether the sky lights the world.                                     |
| `height`                      | `384`                   | Total height, a multiple of `16`, from `16` to `4064`.                |
| `infiniburn`                  | `#infiniburn_overworld` | Block tag listing the blocks that burn forever.                       |
| `logicalHeight`               | `384`                   | How high chorus fruits and portals bring a player, at most `height`.  |
| `minY`                        | `-64`                   | Lowest buildable Y, a multiple of `16`, from `-2032` to `2031`.       |
| `monsterSpawnBlockLightLimit` | `0`                     | Block light level at or below which monsters spawn, from `0` to `15`. |
| `monsterSpawnLightLevel`      | `constant(0)`           | Sky light levels at which monsters spawn, from `0` to `15`.           |
| `skybox`                      | `OVERWORLD`             | `SkyboxType.NONE`, `OVERWORLD` or `END`.                              |
| `timelines`                   | none                    | List of [timelines](/docs/data-driven/timelines) or timeline tags.    |

Everything else about how a world feels is an [environment attribute](/docs/data-driven/worldgen/environment-attributes): lava spreading
fast, water evaporating, piglins zombifying, beds exploding, raids starting.

```kotlin
val netherLike = dp.dimensionType("nether_like") {
	minY = 0
	height = 128
	logicalHeight = 128
	hasCeiling = true
	hasSkylight = false
	hasFixedTime = true
	ambientLight = 0.1f
	cardinalLight = CardinalLight.NETHER
	infiniburn = listOf(Tags.Block.INFINIBURN_NETHER)

	attributes {
		fastLava(true)
		waterEvaporates(true)
		piglinsZombify(false)
		respawnAnchorWorks(true)
		bedRule(canSleep = BedSleepRule.NEVER, canSetSpawn = BedSleepRule.NEVER, explodes = true)
	}
}
```

Reference: [Dimension type](https://minecraft.wiki/w/Dimension_type)

---

## Generators

A dimension declares exactly one generator, picked with one of the three builders below. Without a call to any of them, the dimension
generates the debug world.

### Noise Generator

The standard terrain generator, used by all three vanilla dimensions. It pairs **noise settings** (the terrain algorithm, see
[Noise & Terrain](/docs/data-driven/worldgen/noise)) with a **biome source** (which biome is where).

```kotlin
dimension("my_dimension", type = myDimType) {
	noiseGenerator(settings = NoiseSettings.OVERWORLD, biomeSource = multiNoise(BiomePresets.OVERWORLD))
}
```

#### Biome Sources

| Builder                              | Places biomes by                                                         |
|--------------------------------------|--------------------------------------------------------------------------|
| `fixed(biome)`                       | One biome everywhere, the simplest option.                               |
| `checkerboard(scale, vararg biomes)` | A square grid, each square `2^scale` chunks wide (`scale` defaults `2`). |
| `multiNoise(preset)`                 | A vanilla parameter list: `BiomePresets.OVERWORLD` or `NETHER`.          |
| `multiNoise { }`                     | Your own biomes, each claiming a range in the 6D climate space.          |
| `theEnd()`                           | The hardcoded End layout: central island, void, outer islands.           |

`multiNoise` entries carry the six climate parameters (plus an `offset` acting as a distance penalty). Each takes either a single value or a
`min to max` pair; unset parameters default to `0.0`, so an entry only declares the axes it actually cares about.

```kotlin
noiseGenerator(
	settings = myNoiseSettings,
	biomeSource = multiNoise {
		add(multiNoiseEntry(coldPeaks) {
			temperature = doubleOrPair(-1.0, -0.4)
			continentalness = doubleOrPair(0.3, 1.0)
			erosion = doubleOrPair(-1.0, -0.4)
		})

		add(multiNoiseEntry(warmPlains) {
			temperature = doubleOrPair(0.2, 1.0)
			humidity = doubleOrPair(-0.3, 0.3)
		})
	},
)
```

Reference: [Biome source](https://minecraft.wiki/w/Biome_source)

### Flat Generator

Builds a superflat world out of block layers. Useful for test worlds, creative builds and minigame dimensions.

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

| Field                | Type                    | Default             | Description                                         |
|----------------------|-------------------------|---------------------|-----------------------------------------------------|
| `biome`              | Biome                   | mandatory           | The biome every chunk of the world uses.            |
| `features`           | `Boolean?`              | `false`             | Whether the placed features of the biome generate.  |
| `lakes`              | `Boolean?`              | `false`             | Whether lava lakes generate.                        |
| `layers`             | `List<Layer>`           | empty               | Block layers, read from the bottom of the world up. |
| `structureOverrides` | Structure sets or a tag | every structure set | The only structure sets allowed to generate.        |

Layers can be declared three interchangeable ways: `layer(block, height)` appends one to whatever is already there, `layers(...)` takes them
as varargs, and `layers { }` builds the whole stack at once. `height` defaults to `1` and goes up to `4064`. `structureOverrides` takes
structure sets as varargs, a builder block, or a single structure set tag.

The same settings drive the [flat level generator presets](/docs/data-driven/worldgen/world-presets#flat-level-generator-preset) offered in
the superflat customization screen.

Reference: [Superflat](https://minecraft.wiki/w/Superflat)

### Debug Generator

A flat grid of every block state in the game, at Y 70, on a barrier plane. A development tool, not a playable world.

```kotlin
dimension("debug_world", type = myDimType) {
	debugGenerator()
}
```

Reference: [Debug mode](https://minecraft.wiki/w/Debug_mode)

## See Also

- [Biomes](/docs/data-driven/worldgen/biomes) - what a biome source distributes
- [Environment Attributes](/docs/data-driven/worldgen/environment-attributes) - the rules a dimension type sets for the whole world
- [Noise & Terrain](/docs/data-driven/worldgen/noise) - the noise settings a noise generator points at
- [World Generation](/docs/data-driven/worldgen) - a complete custom dimension, end to end
- [World Presets](/docs/data-driven/worldgen/world-presets) - offering a set of dimensions as a world type
