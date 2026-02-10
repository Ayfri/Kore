---
root: .components.layouts.MarkdownLayout
title: World Generation
nav-title: Worldgen
description: Overview of custom world generation (1.21+) with Kore's Kotlin DSL.
keywords: minecraft, datapack, kore, worldgen, dimension, biome, features, structures, noise
date-created: 2025-08-11
date-modified: 2026-02-04
routeOverride: /docs/data-driven/worldgen
---

# World Generation

This guide covers custom world generation using Kore's Kotlin DSL. It maps common datapack JSON files to concise Kotlin builders.

## What is World Generation?

World generation (worldgen) is the procedural generation process Minecraft uses to algorithmically generate terrain, biomes, features, and
structures. Because there are over 18 quintillion (2⁶⁴) possible worlds, the game generates them using randomness, algorithms, and some
manually built decorations.

The generation process uses **gradient noise algorithms** (like Perlin noise) to ensure terrain has both continuity and randomness. Multiple
noise functions with different frequencies and amplitudes (called **octaves**) are combined to create natural-looking variation with hills,
valleys, and other terrain features.

See [World generation](https://minecraft.wiki/w/World_generation) for the full technical breakdown.

## Generation Steps

Minecraft generates chunks through multiple sequential steps. Incomplete chunks are called **proto-chunks**, while fully generated chunks
accessible to players are **level chunks**:

1. **structures_starts** - Calculate starting points for structure pieces
2. **structures_references** - Store references to nearby structure starts
3. **biomes** - Determine and store biomes (no terrain yet)
4. **noise** - Generate base terrain shape and liquid bodies
5. **surface** - Replace terrain surface with biome-dependent blocks
6. **carvers** - Carve caves and canyons
7. **features** - Place features, structures, and generate heightmaps
8. **light** - Calculate light levels for all blocks
9. **spawn** - Spawn initial mobs
10. **full** - Generation complete, proto-chunk becomes level chunk

Reference: [World generation steps](https://minecraft.wiki/w/World_generation#Steps)

## Documentation Structure

World generation is split into focused pages:

- [**Biomes**](/docs/data-driven/worldgen/biomes) - Climate, visuals, mob spawns, carvers, and feature lists
- [**Dimensions**](/docs/data-driven/worldgen/dimensions) - Dimensions and dimension types
- [**Environment Attributes**](/docs/data-driven/worldgen/environment-attributes) - Visual, audio, and gameplay attributes for biomes and
  dimensions
- [**Features**](/docs/data-driven/worldgen/features) - Configured and placed features (trees, ores, vegetation)
- [**Noise & Terrain**](/docs/data-driven/worldgen/noise) - Density functions, noise definitions, and noise settings
- [**Structures**](/docs/data-driven/worldgen/structures) - Structures, template pools, processors, and structure sets
- [**World Presets**](/docs/data-driven/worldgen/world-presets) - World presets and flat level generator presets

## Decoration Steps

Minecraft runs 11 decoration steps in order for each chunk; structures of a step place before features in that step.

| Step | Name                     | Examples                                    |
|------|--------------------------|---------------------------------------------|
| 1    | `raw_generation`         | Small end islands                           |
| 2    | `lakes`                  | Lava lakes                                  |
| 3    | `local_modifications`    | Geodes, icebergs                            |
| 4    | `underground_structures` | Trial chambers, mineshafts                  |
| 5    | `surface_structures`     | Desert wells, blue ice patches              |
| 6    | `strongholds`            | Unused (strongholds use surface_structures) |
| 7    | `underground_ores`       | Ore blobs, sand/gravel/clay disks           |
| 8    | `underground_decoration` | Infested blobs, nether gravel/blackstone    |
| 9    | `fluid_springs`          | Water/lava springs                          |
| 10   | `vegetal_decoration`     | Trees, cacti, kelp, vegetation              |
| 11   | `top_layer_modification` | Freeze top layer                            |

Reference: [Decoration steps](https://minecraft.wiki/w/World_Generation#Decoration_steps)

## Output Paths

Kore APIs generate JSON under standard datapack directories (replace `<ns>` with your namespace):

| API                             | Output Path                                                  |
|---------------------------------|--------------------------------------------------------------|
| `biome(...)`                    | `data/<ns>/worldgen/biome/<name>.json`                       |
| `configuredCarver(...)`         | `data/<ns>/worldgen/configured_carver/<name>.json`           |
| `configuredFeature(...)`        | `data/<ns>/worldgen/configured_feature/<name>.json`          |
| `densityFunction(...)`          | `data/<ns>/worldgen/density_function/<name>.json`            |
| `dimension(...)`                | `data/<ns>/dimension/<name>.json`                            |
| `dimensionType(...)`            | `data/<ns>/dimension_type/<name>.json`                       |
| `flatLevelGeneratorPreset(...)` | `data/<ns>/worldgen/flat_level_generator_preset/<name>.json` |
| `noise(...)`                    | `data/<ns>/worldgen/noise/<name>.json`                       |
| `noiseSettings(...)`            | `data/<ns>/worldgen/noise_settings/<name>.json`              |
| `processorList(...)`            | `data/<ns>/worldgen/processor_list/<name>.json`              |
| `structureSet(...)`             | `data/<ns>/worldgen/structure_set/<name>.json`               |
| `structures { ... }`            | `data/<ns>/worldgen/structure/<name>.json`                   |
| `templatePool(...)`             | `data/<ns>/worldgen/template_pool/<name>.json`               |
| `worldPreset(...)`              | `data/<ns>/worldgen/world_preset/<name>.json`                |

## Environment Attributes

Biomes and dimension types support a flexible `attributes` map for visuals, audio, and gameplay rules shared across environments.

```kotlin
import io.github.ayfri.kore.features.worldgen.AttributeModifier
import io.github.ayfri.kore.features.worldgen.environmentattributes.*

attributes {
	skyColor(0x78A7FF)

	// When a modifier is set, the value expands to { "argument": ..., "modifier": ... }
	fogColor(0xC0D8FF, AttributeModifier.ADD)
}
```

## Quick Start Example

```kotlin
val dp = DataPack("my_pack")

// 1) Dimension type
val dimType = dp.dimensionType("example_type") {
	minY = -64
	height = 384
	hasSkylight = true
}

// 2) Noise settings
val terrain = dp.noiseSettings("example_noise") {
	noiseOptions(-64, 384, 1, 2)
}

// 3) Biome with features
val plains = dp.biome("example_plains") {
	temperature = 0.8f
	downfall = 0.4f
	hasPrecipitation = true

	attributes {
		skyColor(0x78A7FF)
		fogColor(0xC0D8FF)
		waterFogColor(0x050533)
	}

	effects {
		waterColor = color(0x3F76E4)
	}
}

// 4) Dimension
val dim = dp.dimension("example_dimension", type = dimType) {
	// noiseGenerator(settings = terrain, biomeSource = ...)
}

// 5) World preset
dp.worldPreset("example_preset") {
	dimension(DimensionTypes.OVERWORLD) {
		type = dimType
	}
}
```

## Tips & Testing

- **In-game commands:**
	- Teleport: `/execute in <ns>:<dimension> run tp @s 0 200 0`
	- Locate: `/locate structure <ns>:<structure_name>`
	- Reload: `/reload`

- **Validation:** Check your configs against the [Minecraft Wiki](https://minecraft.wiki/w/World_generation) JSON schemas.

- **Performance:** Keep feature counts and structure spacing reasonable.

- **Testing:** Use GameTest for deterministic validation; see [Test Features](../advanced/test-features).

## Cross-References

- [Colors](/docs/concepts/colors) - RGB and ARGB color formats used in environment attributes
- [Environment Attributes](/docs/data-driven/worldgen/environment-attributes) - Full reference for visual, audio, and gameplay attributes
- [Predicates](/docs/data-driven/predicates) - Condition logic for features
- [Test Features](/docs/advanced/test-features) - Automated validation with GameTest
- [Timelines](/docs/data-driven/timelines) - Animate environment attributes over time using keyframes and easing functions
