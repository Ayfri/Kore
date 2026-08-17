---
root: .components.layouts.MarkdownLayout
title: Minecraft Noise Settings, Density Functions & Surface Rules with Kore
nav-title: Noise
description: Shape Minecraft terrain from Kotlin - noise definitions, density functions, noise routers and surface rules, fully type-safe with Kore.
keywords: minecraft datapack, kore, worldgen, noise settings, density function, noise router, surface rule, terrain generation, perlin noise, octaves
date-created: 2026-02-03
date-modified: 2026-08-17
routeOverride: /docs/data-driven/worldgen/noise
---

# Noise & Terrain

Noise, density functions and noise settings are the three layers that shape Minecraft terrain. They build on each other in that order:

1. **Noise definitions** (`worldgen/noise`) - raw Perlin noise, described by octaves and amplitudes.
2. **Density functions** (`worldgen/density_function`) - composable math nodes that sample noises and combine them into a density value for
   any 3D position.
3. **Noise settings** (`worldgen/noise_settings`) - the terrain configuration a dimension points at: world bounds, default blocks, the noise
   router wiring density functions to generation roles, and the surface rules painting the top layers.

Minecraft decides whether a position is solid from the density value the router's `finalDensity` produces: positive density is a block,
negative density is air (or fluid, below sea level).

References: [Noise](https://minecraft.wiki/w/Noise), [Density function](https://minecraft.wiki/w/Density_function),
[Noise settings](https://minecraft.wiki/w/Noise_settings)

---

## Noise Definitions

A noise definition configures one Perlin noise instance. **Octaves** layer noise samples at different scales - low octaves make large
features (continents), high octaves add fine detail (small bumps).

`firstOctave = -7` starts at the 2⁷ = 128 block scale. Each following octave doubles in frequency, and `amplitudes` weights each one, usually
decreasing so detail never overwhelms the base shape.

```kotlin
val hills = dp.noise("hills") {
	firstOctave = -5
	amplitudes = listOf(1.0, 0.5, 0.25)
}
```

Two shorthands take the values inline:

```kotlin
dp.noise("hills", firstOctave = -5, amplitudes = listOf(1.0, 0.5, 0.25))
dp.noise("hills", -5, 1.0, 0.5, 0.25)
```

| Parameter     | Type           | Description                                       |
|---------------|----------------|---------------------------------------------------|
| `firstOctave` | `Int`          | Starting octave, negative values are larger scale |
| `amplitudes`  | `List<Double>` | Amplitude weight per octave                       |

Every call returns a `NoiseArgument`. Vanilla noises are available as `Noises.AquiferBarrier`, `Noises.Continentalness` and so on, and can be
passed anywhere a `NoiseArgument` is expected.

---

## Density Functions

Density functions are math nodes evaluated per position. Each one lives in its own file and holds exactly one node type, so composing them
means referencing other density function files.

Declare them inside a `densityFunctions { ... }` block, one builder call per file:

```kotlin
dp.densityFunctions {
	val base = constant("base", 1.0)
	val hills = noise("hills", Noises.Continentalness, xzScale = 0.25, yScale = 0.0)

	abs("abs_hills", hills)
	add("terrain", base, hills)
}
```

Each call returns a `DensityFunctionArgument` usable as an input to another node, as a [noise router](#noise-router) field, or anywhere else
a density function is expected. Vanilla nodes come from the generated `DensityFunctions` object, e.g.
`DensityFunctions.Overworld.BASE_3D_NOISE`.

The block itself returns the builder scope, not the last node, so arguments you need later are captured in `val`s inside it. For a single
node, or to return one node out of a group, `dp.densityFunctionsBuilder` exposes the same builders:

```kotlin
dp.densityFunctionsBuilder.abs("abs_base_3d_noise", DensityFunctions.Overworld.BASE_3D_NOISE)

val terrain = with(dp.densityFunctionsBuilder) {
	val gradient = yClampedGradient("gradient") { fromY = 0; toY = 128; fromValue = 1.0; toValue = -1.0 }
	add("terrain", DensityFunctions.Overworld.BASE_3D_NOISE, gradient)
}
```

Most builders accept either a `Double` or a `DensityFunctionArgument` for their inputs, in every combination. `noise`, `shift`, `shiftA`,
`shiftB`, `shiftedNoise` and `weirdScaledSampler` take a `NoiseArgument` instead, since they sample a noise definition directly.

### Density Function Types

| Type                   | Builder                   | Description                                                                   |
|------------------------|---------------------------|-------------------------------------------------------------------------------|
| `abs`                  | `abs(...)`                | Absolute value of the input.                                                  |
| `add`                  | `add(...)`                | Sums two inputs.                                                              |
| `beardifier`           | `beardifier(...)`         | Blends nearby terrain into structures. No parameters.                         |
| `blend_alpha`          | `blendAlpha(...)`         | Smooths transitions between chunk generation versions. No parameters.         |
| `blend_density`        | `blendDensity(...)`       | Blends the input across chunk generation version transitions.                 |
| `blend_offset`         | `blendOffset(...)`        | Supports legacy chunk compatibility blending. No parameters.                  |
| `cache_2d`             | `cache2D(...)`            | Caches the input once per horizontal (X/Z) position.                          |
| `cache_all_in_cell`    | `cacheAllInCell(...)`     | Caches the input for the duration of its interpolation cell.                  |
| `cache_once`           | `cacheOnce(...)`          | Caches the input once per block position, even if referenced multiple times.  |
| `clamp`                | `clamp(...)`              | Restricts the input between `min` and `max`.                                  |
| `constant`             | `constant(...)`           | A fixed value, ignoring the input position.                                   |
| `cube`                 | `cube(...)`               | Raises the input to the power of 3 (x³).                                      |
| `end_islands`          | `endIslands(...)`         | Samples the End's island noise. No parameters.                                |
| `find_top_surface`     | `findTopSurface(...)`     | Scans a column for the topmost position where a density is above zero.        |
| `flat_cache`           | `flatCache(...)`          | Caches the input per 4x4 column, computed once at Y=0.                        |
| `half_negative`        | `halfNegative(...)`       | Halves the input when negative, otherwise leaves it unchanged.                |
| `interpolated`         | `interpolated(...)`       | Interpolates the input across the surrounding grid cells.                     |
| `invert`               | `invert(...)`             | Reciprocal (1 / x) of the input.                                              |
| `max`                  | `max(...)`                | Larger of two inputs.                                                         |
| `min`                  | `min(...)`                | Smaller of two inputs.                                                        |
| `mul`                  | `mul(...)`                | Multiplies two inputs.                                                        |
| `noise`                | `noise(...)`              | Samples a noise definition, scaled horizontally and vertically.               |
| `old_blended_noise`    | `oldBlendedNoise(...)`    | Legacy blended noise used before the 1.18 terrain rewrite.                    |
| `quarter_negative`     | `quarterNegative(...)`    | Quarters the input when negative, otherwise leaves it unchanged.              |
| `range_choice`         | `rangeChoice(...)`        | Picks between two inputs based on whether a value falls within a range.       |
| `shift`                | `shift(...)`              | Samples a noise at `(x/4, y/4, z/4)`, scaled back up by 4.                    |
| `shift_a`              | `shiftA(...)`             | Samples a noise at `(x/4, 0, z/4)`, scaled back up by 4.                      |
| `shift_b`              | `shiftB(...)`             | Samples a noise at `(z/4, x/4, 0)`, scaled back up by 4.                      |
| `shifted_noise`        | `shiftedNoise(...)`       | Like `noise`, but with the sampled coordinates shifted.                       |
| `spline`               | `spline(...)`             | Cubic spline interpolating control points over a coordinate.                  |
| `square`               | `square(...)`             | Raises the input to the power of 2 (x²).                                      |
| `squeeze`              | `squeeze(...)`            | Clamps the input to [-1, 1], then applies `x/2 - x³/24`.                      |
| `weird_scaled_sampler` | `weirdScaledSampler(...)` | Samples a noise and remaps it to bias cave/ravine rarity.                     |
| `y_clamped_gradient`   | `yClampedGradient(...)`   | Linear gradient between two values as Y goes from one bound to another.       |

All builders live in `io.github.ayfri.kore.features.worldgen.densityfunction.types` and take the file's name as their first argument.

`beardifier`, `blend_alpha`, `blend_offset`, `blend_density` and `cache_all_in_cell` are internal to vanilla generation and are not meant to
be referenced from a datapack, even though the builders exist.

### Multi-Parameter Nodes

Nodes with more than a couple of inputs take a builder block instead of a long positional list:

```kotlin
dp.densityFunctions {
	rangeChoice("caves") {
		input(DensityFunctions.Overworld.BASE_3D_NOISE)
		minInclusive = -0.3
		maxExclusive = 0.3
		whenInRange(-1.0)
		whenOutOfRange(1.0)
	}

	yClampedGradient("depth_bias") {
		fromY = -64
		toY = 320
		fromValue = 1.0
		toValue = -1.0
	}

	shiftedNoise("shifted_hills", Noises.Continentalness) {
		xzScale = 0.25
		yScale = 0.0
		shiftX(DensityFunctions.ShiftX)
		shiftZ(DensityFunctions.ShiftZ)
	}

	weirdScaledSampler("ravines", RarityValueMapper.TYPE_1, Noises.CaveEntrance, DensityFunctions.Overworld.BASE_3D_NOISE)

	findTopSurface("top_surface", DensityFunctions.Overworld.BASE_3D_NOISE, DensityFunctions.Y, lowerBound = -64, cellHeight = 8)
}
```

### Splines

A `spline` interpolates control points over a coordinate density function. Points hold either a constant value or a nested spline, which is
how vanilla layers continentalness, erosion and ridges into a single terrain offset:

```kotlin
dp.densityFunctions {
	spline("offset", DensityFunctions.Overworld.CONTINENTS) {
		point(-1.1f, 0.044f)
		point(-0.51f, DensityFunctions.Overworld.EROSION, derivative = 0.5f) {
			point(-0.6f, 1.0f)
			point(0.5f, -1.0f)
		}
	}

	spline("flat", 0.5f)
}
```

Points are ordered by increasing `location`, and `derivative` sets the slope of the curve at that point.

Fields typed `DensityFunctionOrDouble` are set through their matching setter function (`input(...)`, `whenInRange(...)`, `shiftX(...)`),
overloaded for both a `Double` and a `DensityFunctionArgument`. Assigning the field directly needs an explicit
`densityFunctionOrDouble(...)` wrapper.

---

## Noise Settings

Noise settings are the complete terrain configuration for a dimension. A dimension references one through its noise generator.

```kotlin
val terrain = dp.noiseSettings("custom_terrain") {
	seaLevel = 63
	aquifersEnabled = true
	oreVeinsEnabled = true

	noiseOptions(minY = -64, height = 384, sizeHorizontal = 1, sizeVertical = 2)

	defaultBlock(Blocks.STONE)
	defaultFluid(Blocks.WATER) { this["level"] = "0" }

	noiseRouter {
		finalDensity(DensityFunctions.Overworld.SLOPED_CHEESE)
	}

	surfaceRules {
		block(Blocks.STONE)
	}
}
```

### Properties

Defaults below are Kore's, which are not always vanilla's - the Overworld for instance ships with `aquifersEnabled` and `oreVeinsEnabled`
set to `true`.

| Property               | Type                                    | Kore default       | Description                                             |
|------------------------|-----------------------------------------|--------------------|---------------------------------------------------------|
| `aquifersEnabled`      | `Boolean`                               | `false`            | Generates local water/lava tables instead of a flat sea |
| `defaultBlock`         | `BlockState`                            | `stone`            | Block placed where density is positive                  |
| `defaultFluid`         | `BlockState`                            | `water[level=0]`   | Fluid placed below `seaLevel` where density is negative |
| `disableMobGeneration` | `Boolean`                               | `false`            | Skips mob spawning during chunk generation              |
| `legacyRandomSource`   | `Boolean`                               | `false`            | Uses the pre-1.18 random source                         |
| `noise`                | `NoiseOptions`                          | `(-64, 384, 1, 2)` | Vertical range and sampling resolution                  |
| `noiseRouter`          | `NoiseRouter`                           | all zeroes         | Density functions wired to generation roles             |
| `oreVeinsEnabled`      | `Boolean`                               | `false`            | Enables copper and iron ore veins                       |
| `seaLevel`             | `Int`                                   | `63`               | Y level the default fluid fills up to                   |
| `spawnTarget`          | `List<MultiNoiseBiomeSourceParameters>` | empty              | Climate parameters the world spawn point searches for   |
| `surfaceRule`          | `SurfaceRule`                           | `bandlands`        | Rule painting surface blocks                            |

### Noise Options

```kotlin
noiseOptions(
	minY = -64,         // Lowest generated Y level, -2032 to 2031, multiple of 16
	height = 384,       // Total height, 0 to 4064, multiple of 16
	sizeHorizontal = 1, // Horizontal cell size, 1 to 4
	sizeVertical = 2    // Vertical cell size, 1 to 4
)
```

`minY + height` must stay within the dimension type's own vertical range, and larger `size` values mean coarser, cheaper terrain.

### Default Blocks

```kotlin
defaultBlock(Blocks.DEEPSLATE)
defaultFluid(Blocks.LAVA) { this["level"] = "0" }
```

Both take an optional block-state property block, and both accept a plain `Map<String, String>` instead.

### Noise Router

The noise router maps density functions to generation roles. Every field is a `DensityFunctionOrDouble`, so set it with the matching
function, which is overloaded for both a `Double` and a `DensityFunctionArgument`:

```kotlin
noiseRouter {
	barrier(0.5)
	lava(DensityFunctions.End.BASE_3D_NOISE)
	finalDensity(myTerrainDensity)
}
```

| Field                     | Role                                                           |
|---------------------------|----------------------------------------------------------------|
| `barrier`                 | Aquifer barrier noise, separating fluid pockets from stone     |
| `continents`              | Continentalness climate parameter, ocean vs inland             |
| `depth`                   | Depth climate parameter, distance below the surface            |
| `erosion`                 | Erosion climate parameter, flat vs mountainous                 |
| `finalDensity`            | Final solid/air decision for every position                    |
| `fluidLevelFloodedness`   | How often aquifers are filled                                  |
| `fluidLevelSpread`        | How much aquifer fluid levels vary                             |
| `lava`                    | Whether an aquifer holds lava instead of water                 |
| `preliminarySurfaceLevel` | Estimated surface height, used by surface rules and structures |
| `ridges`                  | Weirdness climate parameter, driving ridged terrain            |
| `temperature`             | Temperature climate parameter for biome placement              |
| `vegetation`              | Humidity climate parameter for biome placement                 |
| `veinGap`                 | Gaps punched through ore veins                                 |
| `veinRidged`              | Ore vein shape                                                 |
| `veinToggle`              | Whether ore veins generate at a position                       |

Unset fields serialize as `0.0`, which means flat, featureless terrain - so a hand-written router usually starts from the vanilla density
functions in the generated `DensityFunctions` object.

Reference: [Noise router](https://minecraft.wiki/w/Noise_settings#Noise_router)

### Surface Rules

Surface rules decide which blocks replace the top layers of terrain. They are evaluated in order, and the first rule producing a block wins.

```kotlin
surfaceRules {
	condition(biomes(Biomes.DESERT)) {
		block(Blocks.SAND)
	}

	condition(stoneDepth(Surface.FLOOR, addSurfaceDepth = true)) {
		condition(water(offset = -1, surfaceDepthMultiplier = 0)) {
			block(Blocks.GRASS_BLOCK)
		}
		block(Blocks.DIRT)
	}

	block(Blocks.STONE)
}
```

Every builder below lives on the `surfaceRules` scope, so nothing leaks into the global namespace and the IDE completes the whole rule set
from inside the block.

| Rule             | Builder                    | Description                                                |
|------------------|----------------------------|------------------------------------------------------------|
| `bandlands`      | `bandlands()`              | Vanilla badlands terracotta banding.                       |
| `block`          | `block(block) { }`         | Places a block state.                                      |
| `condition`      | `condition(condition) { }` | Runs nested rules when the condition passes.               |
| `noise_gradient` | `noiseGradient(noise) { }` | Picks a block state from a list, indexed by a noise value. |
| `sequence`       | `sequence { }`             | Groups rules, first match wins.                            |

A `condition` block holding a single rule serializes as that rule, several rules are wrapped in a `sequence`.

`noiseGradient` entries are block states, and `empty()` leaves the position untouched:

```kotlin
surfaceRules {
	noiseGradient(hills) {
		state(Blocks.STONE)
		state(Blocks.DEEPSLATE)
		empty()
	}
}
```

`state` takes an optional block-state property block, like `block`.

#### Conditions

Condition builders live on the `surfaceRules` scope too, so they resolve without extra imports inside the block.

| Condition                   | Builder                                                   | Description                                         |
|-----------------------------|-----------------------------------------------------------|-----------------------------------------------------|
| `above_preliminary_surface` | `AbovePreliminarySurface`                                 | Position is above the router's preliminary surface. |
| `biome`                     | `biomes(...)`                                             | Position is in one of the listed biomes.            |
| `hole`                      | `Hole`                                                    | Column has a surface depth of 0.                    |
| `noise_threshold`           | `noiseThreshold(noise, minThreshold, maxThreshold)`       | Noise value falls within a range.                   |
| `not`                       | `not(condition)`                                          | Inverts another condition.                          |
| `steep`                     | `Steep`                                                   | Position is on a steep north or east facing slope.  |
| `stone_depth`               | `stoneDepth(surfaceType, offset, ...)`                    | Depth below the floor or ceiling of the terrain.    |
| `temperature`               | `Temperature`                                             | Biome is cold enough for snowfall.                  |
| `vertical_gradient`         | `verticalGradient(name, trueAtAndBelow, falseAtAndAbove)` | Random blend between two Y anchors.                 |
| `water`                     | `water(offset, surfaceDepthMultiplier, addStoneDepth)`    | Position is above the local water level.            |
| `y_above`                   | `yAbove(anchor, surfaceDepthMultiplier, addStoneDepth)`   | Position is above a Y anchor, exclusive.            |

`AbovePreliminarySurface`, `Hole`, `Steep` and `Temperature` take no arguments, so they are passed directly as objects:

```kotlin
condition(AbovePreliminarySurface) {
	block(Blocks.GRASS_BLOCK)
}

condition(not(Steep)) {
	block(Blocks.GRAVEL)
}
```

Y anchors used by `yAbove` and `verticalGradient` are built with `absolute(y)`, `aboveBottom(offset)` or `belowTop(offset)`:

```kotlin
condition(verticalGradient("bedrock_floor", aboveBottom(0), aboveBottom(5))) {
	block(Blocks.BEDROCK)
}
```

Reference: [Surface rule](https://minecraft.wiki/w/Surface_rule)

---

## Complete Example

A minimal but working custom dimension, from noise to playable world:

```kotlin
fun DataPack.createCustomTerrain() {
	// 1) Noise definition, sampled by the density function below
	val hills = noise("hills", firstOctave = -5, amplitudes = listOf(1.0, 0.5, 0.25))

	// 2) Density functions shaping the terrain
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

	// 3) Noise settings wiring everything together
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

	// 4) Dimension type and dimension
	val dimType = dimensionType("custom_type") {
		minY = -64
		height = 384
		hasSkylight = true
	}

	val plains = biome("custom_plains") {
		temperature = 0.8f
		downfall = 0.4f
		hasPrecipitation = true
	}

	dimension("custom_world", type = dimType) {
		noiseGenerator(
			biomeSource = fixed(plains),
			settings = terrain,
		)
	}
}
```

Test it in game with `/execute in <namespace>:custom_world run tp @s 0 200 0`.

## See Also

- [Biomes](/docs/data-driven/worldgen/biomes) - Climate, visuals, mob spawns, carvers, and feature lists
- [Dimensions](/docs/data-driven/worldgen/dimensions) - Dimensions and dimension types referencing noise settings
- [World Generation](/docs/data-driven/worldgen) - Overview of the worldgen system
- [World Presets](/docs/data-driven/worldgen/world-presets) - Bundling dimensions into a selectable world type
