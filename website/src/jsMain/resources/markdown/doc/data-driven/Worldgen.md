---
root: .components.layouts.MarkdownLayout
title: Minecraft World Generation - Custom Dimensions, Biomes & Noise with Kore
nav-title: Worldgen
description: Build custom Minecraft world generation in Kotlin. Dimensions, biomes, noise, density functions, structures and features, type-safe, no hand-written JSON.
keywords: minecraft worldgen, datapack worldgen, custom dimension minecraft, custom biome, noise settings minecraft, minecraft noise router, density function, datapack terrain generation, minecraft world preset, custom world generation
date-created: 2025-08-11
date-modified: 2026-08-21
routeOverride: /docs/data-driven/worldgen
---

# World Generation

World generation is how Minecraft turns a seed into terrain: noise decides where blocks are solid, biomes decide what they look like and
what spawns on them, features and structures decorate the result. All of it is data-driven, so a datapack can replace any part of it.

Kore maps that data onto Kotlin builders. Everything on these pages produces JSON files under `data/<namespace>/`, and the whole surface is
typed, so `Blocks.STONE` or `Biomes.DESERT` are checked at compile time instead of at world load.

Reference: [World generation](https://minecraft.wiki/w/World_generation)

## Pages

| Page                                                                        | Covers                                                                   |
|-----------------------------------------------------------------------------|--------------------------------------------------------------------------|
| [Biomes](/docs/data-driven/worldgen/biomes)                                 | Climate, colors, mob spawns, carver and feature lists                    |
| [Block Predicates](/docs/data-driven/worldgen/block-predicates)             | Tests on the block at a position, used by features and enchantments      |
| [Carvers](/docs/data-driven/worldgen/carvers)                               | Caves, nether caves and canyons                                          |
| [Dimensions](/docs/data-driven/worldgen/dimensions)                         | Dimension types, dimensions, generators and biome sources                |
| [Environment Attributes](/docs/data-driven/worldgen/environment-attributes) | Visual, audio and gameplay rules shared by biomes and dimension types    |
| [Features](/docs/data-driven/worldgen/features)                             | Configured and placed features: trees, ores, vegetation                  |
| [Noise & Terrain](/docs/data-driven/worldgen/noise)                         | Noises, density functions, noise settings, noise routers, surface rules  |
| [Providers](/docs/data-driven/worldgen/providers)                           | Vertical anchors and the height, int and float providers used everywhere |
| [Structures](/docs/data-driven/worldgen/structures)                         | Configured structures, template pools, processors and structure sets     |
| [World Presets](/docs/data-driven/worldgen/world-presets)                   | World types in the world creation screen, and superflat presets          |

## How A Chunk Is Built

Every chunk goes through the same ordered steps. A chunk that has not reached the last one is a **proto-chunk**, invisible to players; once
it does, it becomes a **level chunk**.

| # | Step                    | What happens                                            |
|---|-------------------------|---------------------------------------------------------|
| 1 | `structures_starts`     | Picks the origin of every structure piece               |
| 2 | `structures_references` | Records the nearby structure starts                     |
| 3 | `biomes`                | Assigns biomes, still without terrain                   |
| 4 | `noise`                 | Builds the terrain shape and the liquid bodies          |
| 5 | `surface`               | Repaints the top layers with biome-dependent blocks     |
| 6 | `carvers`               | Cuts caves and canyons out of the terrain               |
| 7 | `features`              | Places features and structures, computes the heightmaps |
| 8 | `light`                 | Computes light levels                                   |
| 9 | `spawn`                 | Spawns the initial mobs                                 |

This ordering explains most of the behavior you will run into: carvers run before features, which is why a cave never destroys a tree, and
structures start before terrain, which is why terrain can adapt around them.

Reference: [Generation steps](https://minecraft.wiki/w/World_generation#Steps)

### Decoration Steps

The `features` step is itself split into 11 decoration steps, run in order. A biome lists its placed features per step, and the structures
of a step are placed before the features of that same step.

| #  | Step                     | Examples                                     |
|----|--------------------------|----------------------------------------------|
| 1  | `raw_generation`         | Small end islands                            |
| 2  | `lakes`                  | Lava lakes                                   |
| 3  | `local_modifications`    | Geodes, icebergs                             |
| 4  | `underground_structures` | Trial chambers, mineshafts                   |
| 5  | `surface_structures`     | Desert wells, blue ice patches               |
| 6  | `strongholds`            | Unused, strongholds use `surface_structures` |
| 7  | `underground_ores`       | Ore blobs, sand/gravel/clay disks            |
| 8  | `underground_decoration` | Infested blobs, nether gravel/blackstone     |
| 9  | `fluid_springs`          | Water and lava springs                       |
| 10 | `vegetal_decoration`     | Trees, cacti, kelp, vegetation               |
| 11 | `top_layer_modification` | Freeze top layer                             |

Reference: [Decoration steps](https://minecraft.wiki/w/World_Generation#Decoration_steps)

## Files Kore Writes

Replace `<ns>` with your namespace.

| Kore API                        | Output path                                                  |
|---------------------------------|--------------------------------------------------------------|
| `biome(...)`                    | `data/<ns>/worldgen/biome/<name>.json`                       |
| `configuredCarvers { ... }`     | `data/<ns>/worldgen/configured_carver/<name>.json`           |
| `configuredFeatures { ... }`    | `data/<ns>/worldgen/configured_feature/<name>.json`          |
| `densityFunctions { ... }`      | `data/<ns>/worldgen/density_function/<name>.json`            |
| `dimension(...)`                | `data/<ns>/dimension/<name>.json`                            |
| `dimensionType(...)`            | `data/<ns>/dimension_type/<name>.json`                       |
| `flatLevelGeneratorPreset(...)` | `data/<ns>/worldgen/flat_level_generator_preset/<name>.json` |
| `noise(...)`                    | `data/<ns>/worldgen/noise/<name>.json`                       |
| `noiseSettings(...)`            | `data/<ns>/worldgen/noise_settings/<name>.json`              |
| `placedFeature(...)`            | `data/<ns>/worldgen/placed_feature/<name>.json`              |
| `processorList(...)`            | `data/<ns>/worldgen/processor_list/<name>.json`              |
| `structures { ... }`            | `data/<ns>/worldgen/structure/<name>.json`                   |
| `structureSet(...)`             | `data/<ns>/worldgen/structure_set/<name>.json`               |
| `templatePool(...)`             | `data/<ns>/worldgen/template_pool/<name>.json`               |
| `worldPreset(...)`              | `data/<ns>/worldgen/world_preset/<name>.json`                |

A file name containing slashes lands in subfolders, so `noise("cave/entrance")` writes `worldgen/noise/cave/entrance.json`.

## A Complete Custom Dimension

The smallest set of files producing a playable world of your own: a noise, the density functions shaping it, the noise settings wiring them
together, a biome, a dimension type and the dimension itself.

```kotlin
fun DataPack.createCustomWorld() {
	// 1) A Perlin noise, sampled by the density function below.
	val hills = noise("hills") {
		firstOctave = -5
		amplitudes(1.0, 0.5, 0.25)
	}

	// 2) Density functions: hills on top of a gradient turning solid into air with height.
	val terrainDensity = with(densityFunctionsBuilder) {
		val hillsNoise = noise("hills_noise", hills, xzScale = 0.35, yScale = 0.0)

		val gradient = yClampedGradient("height_gradient") {
			fromY = 0
			toY = 128
			fromValue = 1.0
			toValue = -1.0
		}

		add("final_density", hillsNoise, gradient)
	}

	// 3) Noise settings: world bounds, blocks, and the router feeding the density to the game.
	val terrain = noiseSettings("custom_terrain") {
		seaLevel = 63
		noiseOptions(minY = -64, height = 384, sizeHorizontal = 1, sizeVertical = 2)
		defaultBlock(Blocks.STONE)
		defaultFluid(Blocks.WATER) { this["level"] = "0" }

		noiseRouter {
			finalDensity(terrainDensity)
		}

		surfaceRules {
			condition(stoneDepth(Surface.FLOOR)) {
				block(Blocks.GRASS_BLOCK)
			}
			block(Blocks.STONE)
		}
	}

	// 4) A biome, used everywhere in this world.
	val plains = biome("custom_plains") {
		temperature = 0.8f
		downfall = 0.4f
		hasPrecipitation = true

		attributes {
			skyColor(0x78A7FF)
			fogColor(0xC0D8FF)
		}
	}

	// 5) The rules of the world.
	val dimType = dimensionType("custom_type") {
		minY = -64
		height = 384
		hasSkylight = true
	}

	// 6) The dimension itself.
	dimension("custom_world", type = dimType) {
		noiseGenerator(settings = terrain, biomeSource = fixed(plains))
	}
}
```

Go there in game with `/execute in <namespace>:custom_world run tp @s 0 200 0`. To offer the world as a world type in the creation screen
instead, wrap the same generator in a [world preset](/docs/data-driven/worldgen/world-presets).

## Testing

- `/execute in <ns>:<dimension> run tp @s 0 200 0` teleports into a dimension.
- `/locate structure <ns>:<structure>` finds the nearest instance of a structure.
- Worldgen files are read when the world is created, so `/reload` does not reshape the chunks already generated. Test terrain changes on a
  fresh world.
- [Test Features](/docs/advanced/test-features) runs GameTest assertions for deterministic validation.

## See Also

- [Colors](/docs/concepts/colors) - the RGB and ARGB formats used by biome effects and color attributes
- [Predicates](/docs/data-driven/predicates) - condition logic reused across the datapack
- [Tags](/docs/data-driven/tags) - grouping biomes, structures and presets so vanilla picks them up
- [Timelines](/docs/data-driven/timelines) - animating environment attributes over time
- [World Clocks](/docs/data-driven/world-clocks) - the clocks driving `DimensionType.defaultClock`
