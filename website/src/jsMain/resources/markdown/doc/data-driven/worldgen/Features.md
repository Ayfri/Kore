---
root: .components.layouts.MarkdownLayout
title: Features
nav-title: Features
description: Create configured and placed features (trees, ores, vegetation) with Kore's DSL.
keywords: minecraft, datapack, kore, worldgen, configured feature, placed feature, tree, ore
date-created: 2026-02-03
date-modified: 2026-08-20
routeOverride: /docs/data-driven/worldgen/features
---

# Features

Features are world generation elements like trees, ores, flowers, and other decorations placed during the `features` generation step. They
represent everything from single blocks to complex multi-block structures like trees and geodes.

## Two-Part System

Minecraft separates feature definition into two parts:

- **Configured feature** - Defines *what* to place (tree species, ore type, flower) with all its parameters (block types, sizes, shapes)
- **Placed feature** - Defines *where* and *how often* to place (count per chunk, height range, rarity, biome restrictions)

This separation allows reusing the same configured feature with different placement rules. For example, one tree configuration can be placed
densely in forests but sparsely in plains.

References: [Configured feature](https://minecraft.wiki/w/Configured_feature), [Placed feature](https://minecraft.wiki/w/Placed_feature), [Feature](https://minecraft.wiki/w/Feature)

---

## Configured Features

A configured feature is declared through `configuredFeaturesBuilder`: each feature type (tree, ore, geode, ...) is a function on it taking
the file name first, so one call produces one `ConfiguredFeature` file and returns its `ConfiguredFeatureArgument` - there's no way to
combine feature types into a single configured feature.

### Tree

Trees are complex features with trunk placers, foliage placers, and decorators. The trunk and foliage providers define which blocks to use,
while placers control the shape. `belowTrunkProvider` takes a `ruleBasedStateProvider` and defaults to an empty one, which places nothing
under the trunk.

```kotlin
configuredFeatures {
	tree("my_tree") {
		blobFoliagePlacer(radius = constant(2), offset = constant(0), height = 3)
		foliageProvider = simpleStateProvider(Blocks.OAK_LEAVES)
		straightTrunkPlacer(baseHeight = 6, heightRandA = 3, heightRandB = 1)
		trunkProvider = simpleStateProvider(Blocks.OAK_LOG)

		belowTrunkProvider = ruleBasedStateProvider {
			fallback = simpleStateProvider(Blocks.DIRT)
			rule(simpleStateProvider(Blocks.GRASS_BLOCK)) {
				hasSturdyFace(Direction.DOWN)
			}
		}
	}
}
```

---

### Block State Providers

Block state providers determine which block state is placed at a given position. They fill every feature field typed as
`BlockStateProvider`: `toPlace` on `simpleBlock`, `stateProvider` on `disk`, `trunkProvider` and `foliageProvider` on `tree`, and so on.

Every builder is an extension on `BlockStateProviderScope`, so they resolve inside the configuration block of a feature that actually takes
a provider, and nowhere else:

```kotlin
configuredFeatures {
	simpleBlock("my_flower") {
		toPlace = simpleStateProvider(Blocks.DANDELION)
	}
}
```

| Provider                                    | Picks based on                                                    |
|---------------------------------------------|-------------------------------------------------------------------|
| `dualNoiseProvider { }`                     | Two Perlin noises, one selecting the variety, one picking a state |
| `noiseProvider { }`                         | A single Perlin noise mapped onto a list of states                |
| `noiseThresholdProvider { }`                | A Perlin noise compared against a threshold                       |
| `randomizedIntStateProvider(property) { }`  | Another provider, with one integer property randomized            |
| `rotatedBlockProvider(block)`               | A fixed block with a random `axis`                                |
| `ruleBasedStateProvider { }`                | Ordered block predicate rules, with an optional fallback          |
| `simpleStateProvider(block)`                | A fixed block                                                     |
| `weightedStateProvider { }`                 | A weighted random pick across several providers                   |

Providers nest freely: any of them can appear as the `source` of a `randomizedIntStateProvider`, the `data` of a weighted entry or the
`then` of a rule.

#### `simpleStateProvider` and `rotatedBlockProvider`

Both take a block, a block plus a property map, or a block plus a block state builder. `rotatedBlockProvider` ignores the `axis` of the
state it is given and randomizes it at every position.

```kotlin
simpleStateProvider(Blocks.DANDELION)
simpleStateProvider(Blocks.OAK_LOG, mapOf("axis" to "y"))
rotatedBlockProvider(Blocks.OAK_LOG)
```

#### `weightedStateProvider`

Entries are declared inside the provider block, each one drawn proportionally to its weight, which defaults to `1`. An entry takes a block,
a block state, another provider, or a receiver block setting `data` itself.

```kotlin
weightedStateProvider {
	entry(Blocks.DANDELION, weight = 3)
	entry(Blocks.POPPY)
	entry(weight = 2) {
		data = rotatedBlockProvider(Blocks.OAK_LOG)
	}
}
```

#### `noiseProvider`, `dualNoiseProvider` and `noiseThresholdProvider`

The three noise-based providers share `seed`, `scale` and a `noise { }` block. `noise(firstOctave, vararg amplitudes)` is the short form,
`noise(firstOctave) { }` the receiver form; `dualNoiseProvider` gets the same pair as `slowNoise`. `states(...)` takes either blocks or
block states.

```kotlin
noiseProvider {
	seed = 2345
	scale = 0.05
	noise(-3, 1.0, 1.0)
	states(Blocks.MOSS_BLOCK, Blocks.STONE)
}

dualNoiseProvider {
	seed = 2345
	scale = 0.05
	slowScale = 0.005
	noise(-3, 1.0)
	slowNoise(-10, 1.0, 1.0)
	variety(1, 3) // How many states are in play at a position.
	states(Blocks.CRIMSON_ROOTS, Blocks.WARPED_ROOTS)
}

noiseThresholdProvider {
	seed = 2345
	scale = 0.05
	threshold = -0.8
	highChance = 0.31
	noise(-3, 1.0)
	defaultState = blockState(Blocks.GRASS_BLOCK)
	lowStates(Blocks.PODZOL)
	highStates(Blocks.COARSE_DIRT)
}
```

#### `randomizedIntStateProvider`

Takes the state given by `source` and overrides one integer block state property with a sampled value. The block placed by `source` has to
declare that property.

```kotlin
randomizedIntStateProvider("age") {
	values(0, 7) // Shorthand for a uniform int provider.
	source = simpleStateProvider(Blocks.WHEAT)
}
```

#### `ruleBasedStateProvider`

Evaluates rules top-to-bottom and uses the first matching block state, or `fallback` when nothing matches. With no `fallback` and no
matching rule, the feature places nothing.

Both `rule` styles are available inside the provider block:

```kotlin
ruleBasedStateProvider {
	fallback = simpleStateProvider(Blocks.STONE)

	// Style 1: the provider first, the predicate in the trailing lambda
	rule(simpleStateProvider(Blocks.DIRT)) {
		solid()
	}

	// Style 2: receiver block, with ifTrue { } and then set on the rule
	rule {
		ifTrue {
			not { matchingBlockTag(Tags.Block.CANNOT_REPLACE_BELOW_TREE_TRUNK) }
			solid()
		}
		then = simpleStateProvider(Blocks.SAND)
	}
}
```

Both predicate blocks run on a block predicate scope, so every builder (`solid()`, `not { }`, `matchingBlocks()`, `matchingBiomes()`,
`hasSturdyFace()`, `matchingBlockTag()`, ...) resolves inside them. A single predicate is used as-is, several ones are wrapped in an
`all_of`. See [Block Predicates](/docs/data-driven/worldgen/block-predicates) for the full list.

Reference: [Block state provider](https://minecraft.wiki/w/Block_state_provider)

---

### Ore

Ore features place clusters of blocks that replace existing terrain. The `size` controls maximum vein size, while
`discardChanceOnAirExposure` prevents ores from generating in caves (set to 0 for full veins, 1 to skip all exposed blocks).

Reference: [Ore feature](https://minecraft.wiki/w/Ore_(feature))

```kotlin
val ore = ore(
	"my_ore",
	size = 10,                        // Max blocks per vein
	discardChanceOnAirExposure = 0.1, // Skip blocks exposed to air
) {
	targets {
		// The rule test builders are scoped to the target { } block.
		target(blockState(Blocks.IRON_ORE)) {
			target = tagMatch(Tags.Block.STONE_ORE_REPLACEABLES)
		}

		target(blockState(Blocks.DEEPSLATE_IRON_ORE)) {
			target = tagMatch(Tags.Block.DEEPSLATE_ORE_REPLACEABLES)
		}
	}
}
```

`targets { }` is available on `ore`, `scatteredOre` and `replaceSingleBlock`, the three configured features replacing
terrain blocks. The first target matching a block wins.

### Simple Block

```kotlin
val flower = simpleBlock("my_flower") {
	toPlace = simpleStateProvider(Blocks.DANDELION)
}
```

### Block Column

`blockColumn` stacks layers along a direction, each layer declared inside a `layers { }` block with its height and its provider.

```kotlin
val caveVines = blockColumn("cave_vines", direction = Direction.DOWN) {
	allowedPlacement { replaceable() }
	layers {
		layer(constant(3)) { provider = simpleStateProvider(Blocks.CAVE_VINES_PLANT) }
		layer(constant(1)) { provider = simpleStateProvider(Blocks.CAVE_VINES) }
	}
}
```

### Other Feature Types

Kore supports all vanilla configured feature types. Functions are listed alphabetically:

| Feature Type                      | Description                          | Example Use                    |
|-----------------------------------|--------------------------------------|--------------------------------|
| `bamboo(probability)`             | Bamboo stalks                        | Jungle bamboo                  |
| `basaltColumns(reach, height)`    | Paired basalt pillar columns         | Nether basalt deltas           |
| `blockBlob(...)`                  | Small block blob on a surface        | Mossy cobblestone in forests   |
| `blockColumn(...)`                | Vertical stack of blocks with layers | Custom pillars                 |
| `blockPile(...)`                  | Piles of blocks                      | Pumpkin/melon patches          |
| `deltaFeature(...)`               | Basalt delta with contents and rim   | Nether basalt deltas           |
| `disk(...)`                       | Circular disk of blocks              | Clay, sand, gravel patches     |
| `endGateway(...)`                 | End gateway portal                   | End outer islands              |
| `endSpike(...)`                   | End obsidian pillar with crystal     | The End respawn pillars        |
| `fillLayer(...)`                  | Fill a layer with blocks             | Custom dimension layers        |
| `fossil(...)`                     | Structure-based fossil               | Underground fossils            |
| `geode(...)`                      | Hollow structure with layered shells | Amethyst geodes                |
| `hugeBrownMushroom(...)`          | Large brown mushroom                 | Swamp/mushroom island fungi    |
| `hugeFungus(...)`                 | Huge nether fungus                   | Crimson/warped forests         |
| `hugeRedMushroom(...)`            | Large red mushroom                   | Swamp/mushroom island fungi    |
| `iceberg(...)`                    | Iceberg structure                    | Frozen ocean icebergs          |
| `lake(...)`                       | Liquid pool                          | Underground lava lakes         |
| `largeDripstone(...)`             | Tall stalactite or stalagmite        | Cave ceilings/floors           |
| `multifaceGrowth(block, ...)`     | Multi-face block spread              | Glow lichen, sculk vein        |
| `netherForestVegetation(...)`     | Nether plant scatter                 | Warped/crimson forest floors   |
| `netherrackReplaceBlobs(...)`     | Replace netherrack with blobs        | Nether gravel/blackstone blobs |
| `randomBooleanSelector(...)`      | Picks one of two features randomly   | Symmetric ore variants         |
| `randomSelector(...)`             | Weighted random feature picker       | Mixed ore deposits             |
| `replaceSingleBlock(...)`         | Replace blocks by rule targets       | Custom block swaps             |
| `rootSystem(...)`                 | Root placer for trees                | Mangrove roots                 |
| `scatteredOre(...)`               | Scattered ore deposits               | Nether gold ore blobs          |
| `sculkPatch(...)`                 | Sculk spread with catalyst           | Ancient city surroundings      |
| `seagrass(probability)`           | Seagrass placement                   | Ocean floors                   |
| `seaPickle(count)`                | Sea pickle colonies                  | Warm ocean floors              |
| `sequence(...)`                   | Places multiple features in order    | Multi-step feature chains      |
| `simpleRandomSelector(...)`       | Uniform random feature picker        | Coral type variety             |
| `speleothem(...)`                 | Single pointed speleothem            | Cave stalactites/stalagmites   |
| `speleothemCluster(...)`          | Dense speleothem growth              | Cave speleothem rooms          |
| `spike(...)`                      | Tall spiky columns                   | Ice spikes in frozen biomes    |
| `springFeature(...)`              | Fluid source block                   | Water/lava springs             |
| `template(...)`                   | Places a weighted structure template | Random structure variants      |
| `twistingVines(...)`              | Twisting vine growth                 | Warped forest floors           |
| `underwaterMagma(...)`            | Underwater magma blocks              | Ocean floors                   |
| `vegetationPatch(...)`            | Vegetation on surfaces               | Cave moss patches              |
| `waterloggedVegetationPatch(...)` | Waterlogged vegetation patches       | Underwater cave plants         |
| `weightedRandomSelector(...)`     | Weighted random feature picker       | Mixed ore/vegetation deposits  |

### No-Config Features

These feature types have no configuration fields and are used directly as Kotlin `data object` values:

```kotlin
basaltPillar("basalt_pillar")
desertWell("desert_well")
```

| Object              | Description                      |
|---------------------|----------------------------------|
| `BasaltPillar`      | Single basalt pillar             |
| `BlueIce`           | Blue ice patch on icebergs       |
| `BonusChest`        | Bonus chest at spawn             |
| `ChorusPlant`       | Chorus plant on End islands      |
| `CoralClaw`         | Coral claw structure             |
| `CoralMushroom`     | Coral mushroom structure         |
| `CoralTree`         | Coral tree structure             |
| `DesertWell`        | Desert well structure            |
| `EndIsland`         | Small End island                 |
| `EndPlatform`       | Obsidian end platform            |
| `FreezeTopLayer`    | Freeze/snow the top layer        |
| `GlowstoneBlob`     | Glowstone blob on Nether ceiling |
| `Kelp`              | Kelp stalk                       |
| `MonsterRoom`       | Monster spawner room             |
| `NoOp`              | Does nothing (placeholder)       |
| `Vines`             | Random vine placement            |
| `VoidStartPlatform` | Void dimension start platform    |
| `WeepingVines`      | Weeping vines in the Nether      |

Reference: [Feature types](https://minecraft.wiki/w/Configured_feature#Types)

---

## Placed Features

Placed features wrap a configured feature with **placement modifiers** that control where and how often the feature generates. Modifiers are
applied in sequence, filtering and transforming placement positions.

Reference: [Placed feature](https://minecraft.wiki/w/Placed_feature)

```kotlin
val treePlaced = dp.placedFeature("my_tree_placed", treeConfigured) {
	inSquare()
	count(constant(10))
	heightRange(uniformHeightProvider(64, 128))
	biome()
}
```

### Placement Modifiers

Modifiers process in order, each one filtering or transforming the placement stream. Common patterns:

- Start with `count()` or `rarityFilter()` to control frequency
- Use `inSquare()` to spread horizontally within the chunk
- Apply `heightRange()` or `heightMap()` for vertical positioning
- End with `biome()` to respect biome boundaries

Reference: [Placement modifier](https://minecraft.wiki/w/Placed_feature#Placement_modifiers)

| Modifier                              | Description                                      |
|---------------------------------------|--------------------------------------------------|
| `biome()`                             | Only place in valid biomes                       |
| `blockPredicateFilter { }`            | Custom block condition                           |
| `carvingMask(step)`                   | Only place in blocks carved by `air` or `liquid` |
| `count(n)`                            | Place n times per chunk                          |
| `countOnEveryLayer(n)`                | Place n times on every layer                     |
| `environmentScan(...)`                | Scan for valid placement                         |
| `fixedPlacement(...)`                 | Place at specific absolute positions             |
| `heightMap(type)`                     | Place relative to heightmap                      |
| `heightRange(provider)`               | Vertical placement range                         |
| `inSquare()`                          | Spread horizontally in chunk                     |
| `noiseBasedCount(...)`                | Count based on noise value at the position       |
| `noiseThresholdCount(...)`            | Fixed count chosen by noise threshold            |
| `randomOffset(xzSpread, ySpread)`     | Scatter placement within an XZ/Y radius          |
| `rarityFilter(chance)`                | 1/chance probability to place                    |
| `surfaceRelativeThresholdFilter(...)` | Surface-relative placement                       |
| `surfaceWaterDepthFilter(maxDepth)`   | Max water depth filter                           |

### Vertical Anchors

A vertical anchor is a single Y level, the building block every height provider takes as a bound. The three forms
serialize as a one-key object, and the resolved Y is always clamped to the dimension's build height.

| Builder          | JSON                  | Meaning                                                           |
|------------------|-----------------------|-------------------------------------------------------------------|
| `absolute(y)`    | `{"absolute": y}`     | Absolute Y coordinate, the one shown on the F3 screen.            |
| `aboveBottom(n)` | `{"above_bottom": n}` | `n` blocks above the bottom of the dimension, `0` being `min_y`.  |
| `belowTop(n)`    | `{"below_top": n}`    | `n` blocks below the top of the dimension, larger values go down. |

The builders are extensions on `VerticalAnchorScope`, so they resolve inside any block that accepts an anchor: a
placed feature, a carver configuration or a `surfaceRules { }` block.

Reference: [Vertical anchor](https://minecraft.wiki/w/Custom_world_generation/vertical_anchor)

### Height Providers

Height providers control vertical distribution. Different providers create different ore/feature distributions:

| Provider                                                    | Behaviour                                                                         |
|-------------------------------------------------------------|-----------------------------------------------------------------------------------|
| `constantHeightProvider(anchor)`                            | Always the given anchor. Serializes as the bare anchor, no wrapper object.        |
| `constantAbsolute(y)` / `constantAboveBottom(n)` / `constantBelowTop(n)` | Shorthands for `constantHeightProvider` on each anchor form.          |
| `uniformHeightProvider(min, max)`                           | Equal chance at every level between both bounds, included.                        |
| `trapezoidHeightProvider(min, max, plateau)`                | Flat top of `plateau` blocks in the middle, linear falloff on both sides.         |
| `biasedToBottomHeightProvider(min, max, inner)`             | Uniform over the `inner` bottom blocks, exponential falloff above them.           |
| `veryBiasedToBottomHeightProvider(min, max, inner)`         | Same shape with a sharper falloff, the vanilla diamond pattern.                   |
| `weightedListHeightProvider { }`                            | Picks one of the nested providers, by weight.                                     |

Every builder except `weightedListHeightProvider` also takes plain `Int` bounds, which are read as absolute Y
coordinates. They are extensions on `HeightProviderScope`, which extends `VerticalAnchorScope`, so the anchors are
available in the same block.

Reference: [Height provider](https://minecraft.wiki/w/Custom_world_generation/height_provider)

```kotlin
// Uniform distribution between min and max
heightRange(uniformHeightProvider(minInclusive = 0, maxInclusive = 64))

// Triangular distribution (peaks at center)
heightRange(trapezoidHeightProvider(minInclusive = 0, maxInclusive = 64, plateau = 20))

// Constant absolute Y
heightRange(constantAbsolute(32))

// Constant offset above world bottom
heightRange(constantAboveBottom(8))

// Constant offset below world top
heightRange(constantBelowTop(8))

// Any anchor, wrapped in a constant provider
heightRange(constantHeightProvider(belowTop(16)))

// Biased toward the bottom
heightRange(biasedToBottomHeightProvider(minInclusive = -64, maxInclusive = 0))

// Strongly biased toward the bottom
heightRange(veryBiasedToBottomHeightProvider(minInclusive = -64, maxInclusive = 16))

// One provider drawn out of several, by weight
heightRange(weightedListHeightProvider {
	entry(3, constantAbsolute(32))
	entry(1, uniformHeightProvider(aboveBottom(0), absolute(16)))
})
```

### Float Providers

Float providers supply a float value sampled at runtime. They appear in configured feature fields like `heightScale`,
`stalactiteBluntness`, and `windSpeed`, as well as enchantment effect fields like `volume` and `pitch`.

| Provider                                   | Behaviour                                                                                  |
|--------------------------------------------|--------------------------------------------------------------------------------------------|
| `constant(value)`                          | Always returns `value`. Serializes as a plain float, no wrapper object.                    |
| `uniform(minInclusive, maxExclusive)`      | Uniform random float in `[min, max)`. `maxExclusive` cannot be less than `minInclusive`.   |
| `clampedNormal(mean, deviation, min, max)` | Samples a normal distribution (`mean`/`deviation`) and clamps the result to `[min, max]`.  |
| `trapezoid(min, max, plateau)`             | Samples a trapezoid distribution spanning `[min, max]` with a flat top of width `plateau`. |

Each function also has a `*FloatProvider` alias (`constantFloatProvider`, `uniformFloatProvider`, etc.) for use when
both float and int provider imports are in scope.

```kotlin
// Fixed scale
heightScale = constant(1.5f)

// Random uniform pitch 0.8..1.2 (exclusive upper)
pitch = uniform(0.8f, 1.2f)

// Normal distribution centered at 0.5, clamped to 0..1
heightScale = clampedNormal(mean = 0.5f, deviation = 0.2f, min = 0.0f, max = 1.0f)

// Trapezoid: peaks in the center of 0..2, plateau width 0.5
heightScale = trapezoid(min = 0.0f, max = 2.0f, plateau = 0.5f)
```

---

### Int Providers

Int providers supply an integer value sampled at runtime. They are used wherever Minecraft expects a variable count or
size, for example `count()`, `countOnEveryLayer()`, or block state providers.

| Provider                                               | Behaviour                                                                           |
|--------------------------------------------------------|-------------------------------------------------------------------------------------|
| `biasedToBottom(minInclusive, maxInclusive)`           | Random integer in `[min, max]`, weighted towards the minimum.                       |
| `clamped(minInclusive, maxInclusive, source)`          | Evaluates `source` and clamps its result to `[min, max]`.                           |
| `clampedNormal(minInclusive, maxInclusive, mean, dev)` | Samples a normal distribution (`mean`/`dev`) and clamps the result to `[min, max]`. |
| `constant(value)`                                      | Always returns `value`. Serializes as a plain integer, no wrapper object.           |
| `uniform(minInclusive, maxInclusive)`                  | Uniform random integer in `[min, max]`.                                             |
| `weightedList { }`                                     | Randomly selects one entry from a weighted pool.                                    |

```kotlin
// Fixed count
count(constant(10))

// Random 3-8 times per chunk, biased towards 3
count(biasedToBottom(3, 8))

// Normal distribution centered at 5, clamped to 1-10
count(clampedNormal(1, 10, mean = 5.0f, deviation = 2.0f))

// Clamped source: reroll uniform(0, 20) but never below 4 or above 12
count(clamped(4, 12, uniform(0, 20)))

// Weighted pool: 70% chance of 1, 30% chance of 3
count(weightedList {
	add(weightedEntry(7, constant(1)))
	add(weightedEntry(3, constant(3)))
})
```

---

## Complete Example

```kotlin
fun DataPack.createForestFeatures() {
	// 1) Tree configured feature
	val oakTree = configuredFeaturesBuilder.tree("oak_tree") {
		blobFoliagePlacer(radius = constant(2), offset = constant(0), height = 3)
		foliageProvider = simpleStateProvider(Blocks.OAK_LEAVES)
		straightTrunkPlacer(baseHeight = 5, heightRandA = 2, heightRandB = 0)
		trunkProvider = simpleStateProvider(Blocks.OAK_LOG)
	}

	// 2) Tree placed feature
	val oakTreePlaced = placedFeature("oak_tree_placed", oakTree) {
		inSquare()
		count(constant(8))
		heightRange(uniformHeightProvider(64, 100))
		biome()
	}

	// 3) Flower configured feature
	val flower = configuredFeaturesBuilder.simpleBlock("forest_flower") {
		toPlace = simpleStateProvider(Blocks.POPPY)
	}

	// 4) Flower placed feature
	val flowerPlaced = placedFeature("forest_flower_placed", flower) {
		inSquare()
		rarityFilter(4)
		heightRange(uniformHeightProvider(64, 100))
		biome()
	}

	// 5) Ore configured feature
	val ironOre = configuredFeaturesBuilder.ore(
		"iron_ore",
		size = 9,
		discardChanceOnAirExposure = 0.0,
	) {
		targets {
			target(blockState(Blocks.IRON_ORE)) {
				target = tagMatch(Tags.Block.STONE_ORE_REPLACEABLES)
			}
		}
	}

	// 6) Ore placed feature
	val ironOrePlaced = placedFeature("iron_ore_placed", ironOre) {
		inSquare()
		count(constant(20))
		heightRange(uniformHeightProvider(-64, 72))
		biome()
	}

	// 7) Use in biome
	biome("custom_forest") {
		temperature = 0.7f
		downfall = 0.8f
		hasPrecipitation = true

		attributes {
			skyColor(0x78A7FF)
			fogColor(0xC0D8FF)
			waterFogColor(0x050533)
		}

		effects {
			waterColor = color(0x3F76E4)
		}
		features {
			undergroundOres = listOf(ironOrePlaced)
			vegetalDecoration = listOf(oakTreePlaced, flowerPlaced)
		}
	}
}
```
