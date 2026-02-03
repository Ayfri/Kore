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

```kotlin
val df = dp.densityFunction("my_density", type = /* DensityFunctionType */) {}
```

### Common Density Function Types

| Type                                          | Description                    |
|-----------------------------------------------|--------------------------------|
| `constant`                                    | Fixed value everywhere         |
| `noise`                                       | Sample from a noise definition |
| `yClampedGradient`                            | Gradient based on Y coordinate |
| `add`, `mul`                                  | Combine two density functions  |
| `min`, `max`                                  | Take min/max of two functions  |
| `blend_density`                               | Blend between functions        |
| `cache_2d`, `cache_once`, `cache_all_in_cell` | Caching wrappers               |
| `interpolated`                                | Smooth interpolation           |
| `flat_cache`                                  | 2D cache for flat operations   |
| `spline`                                      | Cubic spline interpolation     |

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
surfaceRule = sequence(
	condition(
		biome(Biomes.DESERT),
		block(Blocks.SAND)
	),
	condition(
		stoneDepthFloor(offset = 0, addSurfaceDepth = false, secondaryDepthRange = 0),
		block(Blocks.GRASS_BLOCK)
	),
	block(Blocks.STONE)
)
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
