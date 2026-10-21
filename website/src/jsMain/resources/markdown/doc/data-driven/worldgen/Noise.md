---
root: .components.layouts.MarkdownLayout
title: Minecraft Noise Settings, Density Functions & Material Rules with Kore
nav-title: Noise
description: Shape Minecraft terrain from Kotlin - noise definitions, density functions, noise routers and material rules, fully type-safe with Kore.
keywords: minecraft datapack, kore, worldgen, noise settings, density function, noise router, material rule, terrain generation, perlin noise, octaves
date-created: 2026-02-03
date-modified: 2026-10-21
routeOverride: /docs/data-driven/worldgen/noise
---

# Noise & Terrain

Noise, density functions and noise settings are the three layers that shape Minecraft terrain. They build on each other in that order:

1. **Noise definitions** (`worldgen/noise`) - raw Perlin noise, described by octaves and amplitudes.
2. **Density functions** (`worldgen/density_function`) - composable math nodes that sample noises and combine them into a density value for
   any 3D position.
3. **Noise settings** (`worldgen/noise_settings`) - the terrain configuration a dimension points at: world bounds, default blocks, the noise
   router wiring density functions to generation roles, and the material rules painting the top layers.

Minecraft decides whether a position is solid from the density value the router's `finalDensity` produces: positive density is a block,
negative density is air (or fluid, below sea level).

References: [Noise](https://minecraft.wiki/w/Noise), [Density function](https://minecraft.wiki/w/Density_function),
[Noise settings](https://minecraft.wiki/w/Noise_settings), misode's [noise](https://misode.github.io/worldgen/noise/),
[density function](https://misode.github.io/worldgen/density-function/) and [noise settings](https://misode.github.io/worldgen/noise-settings/)
generators

---

## Noise Definitions

A noise definition configures one Perlin noise instance. **Octaves** layer noise samples at different scales - low octaves make large
features (continents), high octaves add fine detail (small bumps).

`baseOctave = -7` starts at the 2⁷ = 128 block scale. Each following octave doubles in frequency, and `amplitudeModifiers` weights each one,
usually decreasing so detail never overwhelms the base shape. `octaveCount` is the number of octaves, and when `amplitudeModifiers` is set
its size has to equal it.

```kotlin
val hills = dp.noise("hills") {
	baseOctave = -5
	amplitudeModifiers = listOf(1.0, 0.5, 0.25)
	octaveCount = 3
}
```

`amplitudeModifiers(...)` sets the weights inline and sets `octaveCount` to their number in the same call:

```kotlin
val hills = dp.noise("hills") {
	baseOctave = -5
	amplitudeModifiers(1.0, 0.5, 0.25)
}
```

Only `baseOctave` is always written. Leaving `amplitudeModifiers` out gives every octave a modifier of `1.0`, so
`noise("flat") { baseOctave = -3; octaveCount = 2 }` writes `{"base_octave": -3, "octave_count": 2}`.

A file name holding slashes lands in subfolders, so `noise("cave/entrance")` writes
`data/<namespace>/worldgen/noise/cave/entrance.json`.

| Field                | Type            | Description                                                                                                                   | Vanilla `temperature`                      |
|----------------------|-----------------|-------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------|
| `amplitudeModifiers` | `List<Double>?` | Amplitude multiplier per octave, from `0.0` to `1000000.0`, `0.0` skipping one, at most 32. Absent means `1.0` for every octave. | `listOf(1.5, 0.0, 1.0, 0.0, 0.0, 0.0)`     |
| `baseAmplitude`      | `Double?`       | Amplitude every octave is scaled by, from `0.0` to `1000000.0`. Defaults to `1.0`.                                            | `1.2453007926713473`                       |
| `baseOctave`         | `Int`           | Starting octave from `-32` to `32`, negative values are larger scale. Lower means larger features.                            | `-10`                                      |
| `normalize`          | `Boolean?`      | Whether the sampled value is normalized. Defaults to `true`. The deprecated `"legacy"` normalization isn't expressible.      | not written                                |
| `octaveCount`        | `Int?`          | Number of octaves, from `1` to `32`. Defaults to `1`.                                                                         | `6`                                        |

The block state providers of [features](/docs/data-driven/worldgen/features#noise-based) take the same fields inline through
`NoiseParameters`, built with `noiseParameters(baseOctave, vararg amplitudeModifiers)`.

Every call returns a `NoiseArgument`. Vanilla noises are available as `Noises.AQUIFER_BARRIER`, `Noises.CONTINENTALNESS` and so on, and can be
passed anywhere a `NoiseArgument` is expected.

---

## Density Functions

Density functions are math nodes evaluated per position. Each one lives in its own file and holds exactly one node type, so composing them
means referencing other density function files.

Declare them inside a `densityFunctions { ... }` block, one builder call per file:

```kotlin
dp.densityFunctions {
	val base = constant("base", 1.0)
	val hills = noise("hills", Noises.CONTINENTALNESS, xzScale = 0.25, yScale = 0.0)

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

Most builders accept either a `Double` or a `DensityFunctionArgument` for their inputs, in every combination. `constant` takes a `Double`
only, and `noise`, `shift`, `shiftA`, `shiftB` and `shiftedNoise` take a `NoiseArgument`, since they sample a noise definition directly.

The JSON fields follow the game: single-input nodes (`abs`, `cube`, `negate`, `reciprocal`...) write `input`, two-input nodes (`add`, `div`,
`max`, `min`, `mul`, `sub`) write `left` and `right`, `constant` writes `value` and the shift nodes write `noise`. The same names are the properties of the
node classes.

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
| `ceil`                 | `ceil(...)`               | Rounds the input up to a multiple of `multiple` (1 by default).               |
| `clamp`                | `clamp(...)`              | Restricts the input between `min` and `max`.                                  |
| `constant`             | `constant(...)`           | A fixed value, ignoring the input position.                                   |
| `cube`                 | `cube(...)`               | Raises the input to the power of 3 (x³).                                      |
| `div`                  | `div(...)`                | Divides the first input by the second.                                        |
| `end_islands`          | `endIslands(...)`         | Samples the End's island noise. No parameters.                                |
| `find_top_surface`     | `findTopSurface(...)`     | Scans a column for the topmost position where a density is above zero.        |
| `flat_cache`           | `flatCache(...)`          | Caches the input per 4x4 column, computed once at Y=0.                        |
| `floor`                | `floor(...)`              | Rounds the input down to a multiple of `multiple` (1 by default).             |
| `half_negative`        | `halfNegative(...)`       | Halves the input when negative, otherwise leaves it unchanged.                |
| `interpolated`         | `interpolated(...)`       | Interpolates the input across the surrounding grid cells.                     |
| `interval_select`      | `intervalSelect(...)`     | Picks one of several functions from where the input falls between thresholds. |
| `lerp`                 | `lerp(...)`               | Interpolates from `first` to `second` by `alpha`.                             |
| `max`                  | `max(...)`                | Larger of two inputs.                                                         |
| `min`                  | `min(...)`                | Smaller of two inputs.                                                        |
| `mul`                  | `mul(...)`                | Multiplies two inputs.                                                        |
| `negate`               | `negate(...)`             | Flips the sign of the input.                                                  |
| `noise`                | `noise(...)`              | Samples a noise definition, scaled horizontally and vertically.               |
| `old_blended_noise`    | `oldBlendedNoise(...)`    | Legacy blended noise used before the 1.18 terrain rewrite.                    |
| `quarter_negative`     | `quarterNegative(...)`    | Quarters the input when negative, otherwise leaves it unchanged.              |
| `range_choice`         | `rangeChoice(...)`        | Picks between two inputs based on whether a value falls within a range.       |
| `reciprocal`           | `reciprocal(...)`         | Reciprocal (1 / x) of the input.                                              |
| `round`                | `round(...)`              | Rounds the input to the nearest multiple of `multiple` (1 by default).        |
| `shift`                | `shift(...)`              | Samples a noise at `(x/4, y/4, z/4)`, scaled back up by 4.                    |
| `shift_a`              | `shiftA(...)`             | Samples a noise at `(x/4, 0, z/4)`, scaled back up by 4.                      |
| `shift_b`              | `shiftB(...)`             | Samples a noise at `(z/4, x/4, 0)`, scaled back up by 4.                      |
| `shifted_noise`        | `shiftedNoise(...)`       | Like `noise`, but with the sampled coordinates shifted.                       |
| `spline`               | `spline(...)`             | Cubic spline interpolating control points over a coordinate.                  |
| `square`               | `square(...)`             | Raises the input to the power of 2 (x²).                                      |
| `squeeze`              | `squeeze(...)`            | Clamps the input to [-1, 1], then applies `x/2 - x³/24`.                      |
| `sub`                  | `sub(...)`                | Subtracts the second input from the first.                                    |
| `truncate`             | `truncate(...)`           | Rounds the input towards zero to a multiple of `multiple` (1 by default).     |
| `y_clamped_gradient`   | `yClampedGradient(...)`   | Linear gradient between two values as Y goes from one bound to another.       |

All builders live in `io.github.ayfri.kore.features.worldgen.densityfunction.types` and take the file's name as their first argument.

`lerp` takes the `alpha` density function, then `first` and `second` as a `Double` or a `DensityFunctionArgument`. `floor`, `round`, `ceil`
and `truncate` snap `input / multiple` to an integer and multiply back, so `multiple` is the step. It is optional, defaults to `1` and isn't
written when left out. `round` rounds halves up, and `truncate` rounds towards zero where `floor` rounds down:

```kotlin
dp.densityFunctions {
	val snapped = floor("snapped_depth", DensityFunctions.Overworld.DEPTH, 0.25)
	lerp("blended_depth", DensityFunctions.Overworld.CONTINENTS, snapped, 1.0)
}
```

`blend_alpha`, `blend_offset`, `blend_density` and `cache_all_in_cell` are internal to vanilla generation and are not meant to
be referenced from a datapack, even though the builders exist. `beardifier` is added to the `finalDensity` by hand, see the
[noise router](#noise-router).

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

	shiftedNoise("shifted_hills", Noises.CONTINENTALNESS) {
		xzScale = 0.25
		yScale = 0.0
		shiftX(DensityFunctions.SHIFT_X)
		shiftZ(DensityFunctions.SHIFT_Z)
	}

	intervalSelect("cave_shape") {
		input(DensityFunctions.Overworld.BASE_3D_NOISE)
		thresholds(-0.3f, 0.3f)
		function(DensityFunctions.Nether.BASE_3D_NOISE)
		function(0.0)
		function(DensityFunctions.End.BASE_3D_NOISE)
	}

	findTopSurface("top_surface", DensityFunctions.Overworld.BASE_3D_NOISE, DensityFunctions.Y, lowerBound = -64, cellHeight = 8)
}
```

`intervalSelect` holds one more function than it has thresholds: the first function applies below the first threshold, each following one
applies between two consecutive thresholds, and the last one applies at or above the last threshold. Thresholds must be sorted ascending.

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

	noiseOptions(minY = -64, height = 384, sizeHorizontal = 1, sizeVertical = 2)

	defaultBlock(Blocks.STONE)
	defaultFluid(Blocks.WATER) { this["level"] = "0" }

	aquifers {
		barrier(DensityFunctions.Overworld.BASE_3D_NOISE)
		surfaceLevel(DensityFunctions.Overworld.PRELIMINARY_SURFACE_LEVEL)
	}

	oreVein(Blocks.COPPER_ORE, Blocks.RAW_COPPER_BLOCK, Blocks.GRANITE) {
		density(DensityFunctions.Overworld.OreVein.COPPER_DENSITY)
		fillerGap(DensityFunctions.Overworld.OreVein.GAP)
		richness(DensityFunctions.Overworld.OreVein.RICHNESS)
	}

	noiseRouter {
		finalDensity(DensityFunctions.Overworld.SLOPED_CHEESE)
	}

	materialRules {
		block(Blocks.STONE)
	}
}
```

### Properties

Defaults below are Kore's, which are not always vanilla's - the Overworld for instance ships with `aquifers` and two `oreVeins`.

| Property               | Type                                                     | Kore default       | Description                                             |
|------------------------|----------------------------------------------------------|--------------------|---------------------------------------------------------|
| `aquifers`             | `Aquifers?`                                              | `null`             | Local water/lava pockets instead of a flat sea          |
| `defaultBlock`         | `BlockState`                                             | `stone`            | Block placed where density is positive                  |
| `defaultFluid`         | `BlockState`                                             | `water[level=0]`   | Fluid placed below `seaLevel` where density is negative |
| `disableMobGeneration` | `Boolean`                                                | `false`            | Skips mob spawning during chunk generation              |
| `legacyRandomSource`   | `Boolean`                                                | `false`            | Uses the pre-1.18 random source                         |
| `materialRule`         | `MaterialRule`                                           | `bandlands`        | Rule picking the block of each position, inline or ID   |
| `noise`                | `NoiseOptions`                                           | `(-64, 384, 1, 2)` | Vertical range and sampling resolution                  |
| `noiseRouter`          | `NoiseRouter`                                            | all zeroes         | Density functions wired to generation roles             |
| `oreVeins`             | `List<OreVein>?`                                         | `null`             | Large veins of ore and raw ore blocks                   |
| `seaLevel`             | `Int`                                                    | `63`               | Y level the default fluid fills up to                   |
| `spawnTarget`          | `List<Map<DensityFunctionArgument, DoubleOrDoublePair>>` | empty              | Climate points the world spawn prefers                  |

### Spawn Target

`spawnTargetPoint` adds one point of climate space: the density functions it lists, each set to a value or a `min..max` range. The world
spawn prefers where all of them match, but it is a preference, not a filter: every candidate is scored by how far its climate is outside
the closest point (lower is better), plus its distance from `0, 0`, so the search still ends somewhere when nothing matches. Several points
describe several preferred areas. Narrow a range to steer the spawn, widen it to `-1.0..1.0` to ignore that function. Vanilla's overworld
prefers land (`continents` from `-0.11`) away from the `ridges` values near `0`, so it has one point per side (only the first is shown):

```kotlin
spawnTargetPoint {
	this[DensityFunctions.Overworld.CONTINENTS] = -0.11..1.0
	this[DensityFunctions.Overworld.EROSION] = -1.0..1.0
	this[DensityFunctions.Overworld.RIDGES] = -1.0..-0.16
	this[DensityFunctions.Overworld.TEMPERATURE] = -1.0..1.0
	this[DensityFunctions.Overworld.VEGETATION] = -1.0..1.0
}
```

Each point is a JSON object keyed by density function ID, and a lone number, such as `this[DensityFunctions.Overworld.DEPTH] = 0.5`,
replaces a range:

```json
"spawn_target": [
	{
		"minecraft:overworld/continents": [-0.11, 1.0],
		"minecraft:overworld/ridges": [-1.0, -0.16]
	}
]
```

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
	continents(DensityFunctions.Overworld.CONTINENTS)
	finalDensity(myTerrainDensity)
	preliminarySurfaceLevel(63.0)
}
```

| Field                     | Role                                                            |
|---------------------------|-----------------------------------------------------------------|
| `continents`              | Continentalness climate parameter, ocean vs inland              |
| `depth`                   | Depth climate parameter, distance below the surface             |
| `erosion`                 | Erosion climate parameter, flat vs mountainous                  |
| `finalDensity`            | Final solid/air decision for every position                     |
| `preliminarySurfaceLevel` | Estimated surface height, used by material rules and structures |
| `ridges`                  | Weirdness climate parameter, driving ridged terrain             |
| `temperature`             | Temperature climate parameter for biome placement               |
| `vegetation`              | Humidity climate parameter for biome placement                  |

Unset fields serialize as `0.0`, which means flat, featureless terrain - so a hand-written router usually starts from the vanilla density
functions in the generated `DensityFunctions` object.

`finalDensity` no longer adds the `minecraft:beardifier` on its own, so terrain doesn't blend into structures unless the density function
does it. A pack reusing vanilla terrain adds it, as the vanilla overworld does with an `add` whose `right` is `{ "type": "minecraft:beardifier" }`:

```kotlin
val terrain = with(dp.densityFunctionsBuilder) {
	val beardifier = beardifier("beardifier")
	add("terrain_with_structures", DensityFunctions.Overworld.SLOPED_CHEESE, beardifier)
}

dp.noiseSettings("my_terrain") {
	noiseRouter {
		finalDensity(terrain)
	}
}
```

Reference: [Noise router](https://minecraft.wiki/w/Noise_settings#Noise_router)

### Aquifers

`aquifers { }` carves the local water and lava pockets. Without it, the terrain is only flooded up to `seaLevel`, like the Nether and the End
in vanilla. Every field is a `DensityFunctionOrDouble`, set with the matching function, overloaded for a `Double` and a
`DensityFunctionArgument`, and unset fields serialize as `0.0`.

| Field                   | Role                                                       |
|-------------------------|------------------------------------------------------------|
| `barrier`               | Noise separating the fluid pockets from the stone          |
| `exclusion`             | Density below which no aquifer generates                   |
| `fluidLevelFloodedness` | How often the aquifers are filled                          |
| `fluidLevelSpread`      | How much the fluid level of the aquifers varies            |
| `lava`                  | Whether an aquifer holds lava instead of water             |
| `surfaceLevel`          | Surface height the aquifers are placed relative to         |

### Ore Veins

`oreVein(oreBlock, rawOreBlock, fillerBlock)` adds one large vein, repeatable. `rawOreChance` is the chance, from `0.0` to `1.0`, of a raw
ore block instead of the ore (default `0.02`, like vanilla). The block takes the density functions of the vein, unset ones serializing as
`0.0`:

```kotlin
oreVein(Blocks.DEEPSLATE_IRON_ORE, Blocks.RAW_IRON_BLOCK, Blocks.TUFF, rawOreChance = 0.02f) {
	density(DensityFunctions.Overworld.OreVein.IRON_DENSITY)
	fillerGap(DensityFunctions.Overworld.OreVein.GAP)
	richness(DensityFunctions.Overworld.OreVein.RICHNESS)
}
```

| Field       | Role                                                          |
|-------------|---------------------------------------------------------------|
| `density`   | Where the vein generates, positive inside the vein            |
| `fillerGap` | Noise below which the filler block is skipped                 |
| `richness`  | Chance of an ore block instead of a filler block in the vein  |

### Material Rules

Material rules decide which blocks replace the top layers of terrain. They are evaluated in order, and the first rule producing a block wins.

```kotlin
materialRules {
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

Every builder below lives on the `materialRules` scope, so nothing leaks into the global namespace and the IDE completes the whole rule set
from inside the block.

| Rule        | Builder                    | Description                                  |
|-------------|----------------------------|----------------------------------------------|
| `bandlands` | `bandlands()`              | Vanilla badlands terracotta banding.         |
| `block`     | `block(block) { }`         | Places a block state.                        |
| `condition` | `condition(condition) { }` | Runs nested rules when the condition passes. |
| `sequence`  | `sequence { }`             | Groups rules, first match wins.              |

A `condition` block holding a single rule serializes as that rule, several rules are wrapped in a `sequence`.

#### Conditions

Condition builders live on the `materialRules` scope too, so they resolve without extra imports inside the block.

| Condition                   | Builder                                                      | Description                                         |
|-----------------------------|--------------------------------------------------------------|-----------------------------------------------------|
| `above_preliminary_surface` | `AbovePreliminarySurface`                                    | Position is above the router's preliminary surface. |
| `biome`                     | `biomes(...)`                                                | Position is in one of the listed biomes.            |
| `hole`                      | `Hole`                                                       | Column has a surface depth of 0.                    |
| `noise_threshold`           | `noiseThreshold(noise, minThreshold, maxThreshold) { is3d }` | Noise value falls within a range, in 2D or 3D.      |
| `not`                       | `not(condition)`                                             | Inverts another condition.                          |
| `steep`                     | `Steep`                                                      | Position is on a steep north or east facing slope.  |
| `stone_depth`               | `stoneDepth(surfaceType, offset, ...)`                       | Depth below the floor or ceiling of the terrain.    |
| `temperature`               | `Temperature`                                                | Biome is cold enough for snowfall.                  |
| `vertical_gradient`         | `verticalGradient(name, trueAtAndBelow, falseAtAndAbove)`    | Random blend between two Y anchors.                 |
| `water`                     | `water(offset, surfaceDepthMultiplier, addStoneDepth)`       | Position is above the local water level.            |
| `y_above`                   | `yAbove(anchor, surfaceDepthMultiplier, addStoneDepth)`      | Position is above a Y anchor, exclusive.            |

`noiseThreshold` evaluates its noise in 2D (X/Z) by default. Set `is3d = true` to evaluate it in 3D instead:

```kotlin
condition(noiseThreshold(hills) {
	minThreshold = -0.5
	maxThreshold = 0.5
	is3d = true
}) {
	block(Blocks.GRANITE)
}
```

`AbovePreliminarySurface`, `Hole`, `Steep` and `Temperature` take no arguments, so they are passed directly as objects:

```kotlin
condition(AbovePreliminarySurface) {
	block(Blocks.GRASS_BLOCK)
}

condition(not(Steep)) {
	block(Blocks.GRAVEL)
}
```

The vertical anchors used by `yAbove` and `verticalGradient` are built with `absolute(y)`, `aboveBottom(offset)` or
`belowTop(offset)`, all scoped to the `materialRules { }` block:

```kotlin
condition(verticalGradient("bedrock_floor", aboveBottom(0), aboveBottom(5))) {
	block(Blocks.BEDROCK)
}
```

#### Material Rule and Condition Files

Rules and conditions can live in their own files, `worldgen/material_rule/<name>.json` and `worldgen/material_condition/<name>.json`, and
be used by ID wherever an inline one fits. `materialRule` holds the rules of its block, a single one as-is and several in a `sequence`,
`materialCondition` the condition its block returns:

```kotlin
val highlands = materialCondition("highlands") { yAbove(absolute(120)) }

val peaks = materialRule("peaks") {
	condition(reference(highlands)) { block(Blocks.SNOW_BLOCK) }
}

noiseSettings("my_settings") {
	materialRules {
		rule(peaks)
		rule(MaterialRules.Overworld.SURFACE)
	}
}
```

`rule(id)` appends a rule file, `reference(id)` turns a condition file into a condition, and `materialRule(id)` sets the noise settings'
rule to a file, like vanilla's `materialRule(MaterialRules.Overworld)`. The vanilla files are typed in the generated `MaterialRules` and
`MaterialConditions`.

Reference: [Material rule](https://minecraft.wiki/w/Surface_rule)

---

## Putting It Together

The [worldgen overview](/docs/data-driven/worldgen#a-complete-custom-dimension) walks through a noise, its density functions, the noise
settings wiring them together, and the dimension pointing at the result.

## See Also

- [Biomes](/docs/data-driven/worldgen/biomes) - the biomes a noise generator distributes over the terrain
- [Carvers](/docs/data-driven/worldgen/carvers) - the caves cut out of the terrain afterwards
- [Dimensions](/docs/data-driven/worldgen/dimensions) - the dimension pointing at noise settings
- [Providers](/docs/data-driven/worldgen/providers) - the vertical anchors used by material rule conditions
- [World Generation](/docs/data-driven/worldgen) - overview of the worldgen system
