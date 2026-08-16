---
root: .components.layouts.MarkdownLayout
title: Noise & Terrain
nav-title: Noise
description: Define terrain shaping with density functions, noise definitions, and noise settings.
keywords: minecraft, datapack, kore, worldgen, noise, density function, noise settings, terrain
date-created: 2026-02-03
date-modified: 2026-02-03
routeOverride: /docs/data-driven/worldgen/noise
---

# Noise & Terrain

Noise and density functions are the mathematical foundation of Minecraft's terrain generation. They control everything from mountain heights
to cave shapes, creating the continuous, natural-looking landscapes players explore.

## How Terrain Generation Works

Minecraft uses **density functions** to determine whether each position in 3D space should be solid or air. Positive density = solid block,
negative density = air. These functions sample from **noise definitions** (Perlin noise with configurable octaves) to create smooth, natural
variation.

The **noise router** connects density functions to specific terrain aspects: base terrain shape, cave carving, aquifer placement, ore vein
distribution, and biome parameters.

References: [Density function](https://minecraft.wiki/w/Density_function), [Noise](https://minecraft.wiki/w/Noise), [Noise settings](https://minecraft.wiki/w/Noise_settings)

---

## Density Functions

Density functions are composable mathematical operations that output a density value for any 3D position. They can be combined, transformed,
and cached to build complex terrain shapes from simple primitives.

Reference: [Density function](https://minecraft.wiki/w/Density_function)

Every density function file holds exactly one node type. Declare one or more inside a `densityFunctions { ... }` block, one builder call per file:

```kotlin
dp.densityFunctions {
	val base = constant("base", 1.0)
	abs("my_density", base)
	add("terrain", 1.0, 2.0)
	noise("hills", hillsNoise, xzScale = 0.5, yScale = 0.25)
}
```

Each call returns a `DensityFunctionArgument` you can pass into another density function, a noise router field, or a [noise settings](#noise-settings) block.

### Density Function Types

| Type                    | Builder                          | Description                                                                  |
|-------------------------|-----------------------------------|-------------------------------------------------------------------------------|
| `abs`                   | `abs(...)`                        | Absolute value of the input.                                                  |
| `add`                   | `add(...)`                        | Sums two inputs.                                                              |
| `beardifier`            | `beardifier(...)`                 | Blends nearby terrain into structures. No parameters.                         |
| `blend_alpha`           | `blendAlpha(...)`                 | Smooths transitions between chunk generation versions. No parameters.         |
| `blend_density`         | `blendDensity(...)`               | Blends the input across chunk generation version transitions.                 |
| `blend_offset`          | `blendOffset(...)`                | Supports legacy chunk compatibility blending. No parameters.                  |
| `cache_2d`              | `cache2D(...)`                    | Caches the input once per horizontal (X/Z) position.                          |
| `cache_all_in_cell`     | `cacheAllInCell(...)`             | Caches the input for the duration of its interpolation cell.                  |
| `cache_once`            | `cacheOnce(...)`                  | Caches the input once per block position, even if referenced multiple times.  |
| `clamp`                 | `clamp(...)`                      | Restricts the input between `min` and `max`.                                  |
| `constant`              | `constant(...)`                   | A fixed value, ignoring the input position.                                   |
| `cube`                  | `cube(...)`                       | Raises the input to the power of 3 (x^3).                                     |
| `end_islands`           | `endIslands(...)`                 | Samples the End's island noise. No parameters.                                |
| `find_top_surface`      | `findTopSurface(...)`             | Scans a column for the topmost position where a density is above zero.        |
| `flat_cache`            | `flatCache(...)`                  | Caches the input per 4x4 column, computed once at Y=0.                        |
| `half_negative`         | `halfNegative(...)`               | Halves the input when negative, otherwise leaves it unchanged.                |
| `interpolated`          | `interpolated(...)`               | Interpolates the input across the surrounding grid cells.                     |
| `invert`                | `invert(...)`                     | Reciprocal (1 / x) of the input.                                              |
| `max`                   | `max(...)`                        | Larger of two inputs.                                                         |
| `min`                   | `min(...)`                        | Smaller of two inputs.                                                        |
| `mul`                   | `mul(...)`                        | Multiplies two inputs.                                                        |
| `noise`                 | `noise(...)`                      | Samples a noise, scaled horizontally and vertically.                          |
| `old_blended_noise`     | `oldBlendedNoise(...)`            | Legacy blended noise used before the 1.18 terrain rewrite.                    |
| `quarter_negative`      | `quarterNegative(...)`            | Quarters the input when negative, otherwise leaves it unchanged.              |
| `range_choice`          | `rangeChoice(...)`                | Picks between two inputs based on whether a value falls within a range.       |
| `shift`                 | `shift(...)`                      | Samples a noise at the input position scaled down by 4.                       |
| `shift_a`               | `shiftA(...)`                     | Samples a noise at `(x/4, 0, z/4)`, scaled back up by 4.                      |
| `shift_b`               | `shiftB(...)`                     | Samples a noise at `(z/4, x/4, 0)`, scaled back up by 4.                      |
| `shifted_noise`         | `shiftedNoise(...)`               | Like `noise`, but with the sampled coordinates shifted.                       |
| `spline`                | `spline(...)`                     | Cubic spline over the input.                                                  |
| `square`                | `square(...)`                     | Raises the input to the power of 2 (x^2).                                     |
| `squeeze`               | `squeeze(...)`                    | Clamps the input to [-1, 1], then applies `x/2 - x^3/24`.                    |
| `weird_scaled_sampler`  | `weirdScaledSampler(...)`         | Samples a noise and remaps it to bias cave/ravine rarity.                     |
| `y_clamped_gradient`    | `yClampedGradient(...)`           | Linear gradient between two values as Y goes from one bound to another.       |

All builders live in `io.github.ayfri.kore.features.worldgen.densityfunction.types` and take the file's `fileName` as their first argument.

---

## Noise Definitions

Noise definitions configure Perlin noise parameters. Perlin noise creates smooth, continuous random values that look natural. **Octaves**
layer multiple noise samples at different scales-lower octaves create large features (continents), higher octaves add fine detail (small
hills).

Reference: [Noise](https://minecraft.wiki/w/Noise)

```kotlin
val noise = dp.noise("my_noise") {
	firstOctave = -7
	amplitudes = listOf(1.0, 1.0, 0.5)
}
```

### Parameters

| Parameter     | Description                               |
|---------------|-------------------------------------------|
| `firstOctave` | Starting octave (negative = larger scale) |
| `amplitudes`  | List of amplitude weights per octave      |

**Understanding octaves:** `firstOctave = -7` means the first octave operates at 2⁷ = 128 block scale. Each subsequent octave doubles in
frequency (halves in scale). Amplitudes weight each octave's contribution-typically decreasing for higher octaves to add detail without
overwhelming the base shape.

---

## Noise Settings

Noise settings define the complete terrain generation configuration for a dimension. They specify world bounds, default blocks, the noise
router (which density functions control which terrain aspects), and surface rules.

Reference: [Noise settings](https://minecraft.wiki/w/Noise_settings)

```kotlin
val terrain = dp.noiseSettings("my_terrain") {
	// Vertical bounds
	noiseOptions(minY = -64, height = 384, sizeHorizontal = 1, sizeVertical = 2)

	// Default blocks
	defaultBlock(Blocks.STONE) {}
	defaultFluid(Blocks.WATER) { this["level"] = "0" }

	// Noise router (terrain shaping)
	// noiseRouter { ... }

	// Surface rules
	// surfaceRule = ...

	// Spawn target
	// spawnTarget = ...
}
```

### Noise Options

```kotlin
noiseOptions(
	minY = -64,        // Minimum Y level
	height = 384,      // Total height (must be multiple of 16)
	sizeHorizontal = 1, // Horizontal noise size (1, 2, or 4)
	sizeVertical = 2    // Vertical noise size (1, 2, or 4)
)
```

### Default Blocks

```kotlin
// Solid terrain block
defaultBlock(Blocks.STONE) {}

// Fluid block with properties
defaultFluid(Blocks.WATER) { this["level"] = "0" }
```

### Noise Router

The noise router maps density functions to specific terrain generation roles. Each field controls a different aspect of world generation:

Reference: [Noise router](https://minecraft.wiki/w/Noise_settings#Noise_router)

```kotlin
noiseRouter {
	// Core terrain
	finalDensity = /* density function */
    initialDensity = /* density function */

    // Aquifers and ore veins
    barrier = /* density function */
    fluidLevelFloodedness = /* density function */
    fluidLevelSpread = /* density function */
    lava = /* density function */
    veinToggle = /* density function */
    veinRidged = /* density function */
    veinGap = /* density function */

    // Biome and erosion
    continents = /* density function */
    erosion = /* density function */
    depth = /* density function */
    ridges = /* density function */
    temperature = /* density function */
    vegetation = /* density function */
}
```

### Surface Rules

Surface rules determine which blocks appear on the terrain surface. They're evaluated top-to-bottom, with the first matching rule winning.
Conditions can check biome, depth, noise values, and more.

Reference: [Surface rule](https://minecraft.wiki/w/Surface_rule)

```kotlin
surfaceRules {
	condition(biomes(Biomes.DESERT)) {
		block(Blocks.SAND)
	}
	condition(stoneDepth(Surface.FLOOR, addSurfaceDepth = false, secondaryDepthRange = 0)) {
		block(Blocks.GRASS_BLOCK)
	}
	block(Blocks.STONE)
}
```

Use `noiseGradient` to pick a block state from a list of entries, indexed by the value of a noise:

```kotlin
surfaceRules {
	noiseGradient(hillsNoise) {
		entry(BlockState(Blocks.STONE))
		entry(BlockState(Blocks.DEEPSLATE))
	}
}
```

---

## Complete Example

```kotlin
fun DataPack.createCustomTerrain() {
	// 1) Custom noise definition
	val hillsNoise = noise("hills_noise") {
		firstOctave = -5
		amplitudes = listOf(1.0, 0.5, 0.25)
	}

	// 2) Noise settings for terrain
	val terrain = noiseSettings("custom_terrain") {
		noiseOptions(minY = -64, height = 384, sizeHorizontal = 1, sizeVertical = 2)
		defaultBlock(Blocks.STONE) {}
		defaultFluid(Blocks.WATER) { this["level"] = "0" }
	}

	// 3) Use in dimension
	val dimType = dimensionType("custom_type") {
		minY = -64
		height = 384
		hasSkylight = true
	}

	dimension("custom_world", type = dimType) {
		noiseGenerator(
			settings = terrain,
			biomeSource = /* your biome source */
		)
	}
}
```
