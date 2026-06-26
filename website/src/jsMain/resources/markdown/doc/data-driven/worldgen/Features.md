---
root: .components.layouts.MarkdownLayout
title: Features
nav-title: Features
description: Create configured and placed features (trees, ores, vegetation) with Kore's DSL.
keywords: minecraft, datapack, kore, worldgen, configured feature, placed feature, tree, ore
date-created: 2026-02-03
date-modified: 2026-06-26
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

Configured features define the feature type and its parameters. Kore provides builder functions for common feature types.

### Tree

Trees are complex features with trunk placers, foliage placers, and decorators. The trunk and foliage providers define which blocks to use,
while placers control the shape.

```kotlin
val treeCfg = tree {
	blobFoliagePlacer(radius = constant(2), offset = constant(0), height = 3)
	foliageProvider = simpleStateProvider(Blocks.OAK_LEAVES)
	straightTrunkPlacer(baseHeight = 6, heightRandA = 3, heightRandB = 1)
	trunkProvider = simpleStateProvider(Blocks.OAK_LOG)

	belowTrunkProvider = ruleBasedStateProvider {
		fallback = simpleStateProvider(Blocks.DIRT)
		rule {
			ifTrue {
				hasSturdyFace(direction = Direction.DOWN)
			}
			then(simpleStateProvider(Blocks.GRASS_BLOCK))
		}
	}
}

val tree = dp.configuredFeature("my_tree", treeCfg) {}
```

---

### Block State Providers

Block state providers determine which block state is placed at a given position. Every feature field typed as
`BlockStateProvider`
accepts any of the following.

| Provider                      | Picks based on                                 |
|-------------------------------|------------------------------------------------|
| `dualNoiseProvider { }`       | Two-layer Perlin noise                         |
| `noiseProvider { }`           | Single-layer Perlin noise                      |
| `noiseTresholdProvider { }`   | Noise threshold between two blocks             |
| `randomizedIntProvider { }`   | Int-provider-driven index into a state list    |
| `rotatedBlockProvider(state)` | Fixed block with random rotation               |
| `ruleBasedStateProvider { }`  | Ordered predicate rules with optional fallback |
| `simpleStateProvider(state)`  | Fixed block                                    |
| `weightedStateProvider { }`   | Weighted random across several blocks          |

#### `ruleBasedStateProvider`

Evaluates rules top-to-bottom and uses the first matching block state, or `fallback` when nothing matches.

All three `rule` styles are available inside the provider block:

```kotlin
ruleBasedStateProvider {
	fallback = simpleStateProvider(Blocks.STONE)

	// Style 1: receiver block: ifTrue { } and then(...) called on the rule
	rule {
		ifTrue { solid() }
		then(simpleStateProvider(Blocks.DIRT))
	}

	// Style 2: direct values
	rule(
		ifTrue = hasSturdyFace(direction = Direction.DOWN),
		then = simpleStateProvider(Blocks.GRAVEL),
	)

	// Style 3: trailing lambda for the predicate; then is a normal argument
	rule(then = simpleStateProvider(Blocks.SAND)) {
		not { matchingBlockTag(tag = Tags.Block.CANNOT_REPLACE_BELOW_TREE_TRUNK) }
		solid()
	}
}
```

The `ifTrue { }` block in Style 1 and the trailing lambda in Style 3 both run on a `MutableList<BlockPredicate>`
receiver, so all block
predicate extensions (`solid()`, `not { }`, `matchingBlocks()`, `hasSturdyFace()`, `matchingBlockTag()`, etc.) work
inside them. A
single-element list is used as-is; multiple entries are automatically wrapped in `allOf`.

---

### Ore

Ore features place clusters of blocks that replace existing terrain. The `size` controls maximum vein size, while
`discardChanceOnAirExposure` prevents ores from generating in caves (set to 0 for full veins, 1 to skip all exposed blocks).

Reference: [Ore feature](https://minecraft.wiki/w/Ore_(feature))

```kotlin
val oreCfg = ore(
	size = 10,                        // Max blocks per vein
	discardChanceOnAirExposure = 0.1, // Skip blocks exposed to air
	targets = listOf(Target())        // Rule tests for replacement
)

val ore = dp.configuredFeature("my_ore", oreCfg) {}
```

### Simple Block

```kotlin
val flowerCfg = simpleBlock(toPlace = simpleStateProvider(Blocks.DANDELION))

val flower = dp.configuredFeature("my_flower", flowerCfg) {}
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
| `dripstoneCluster(...)`           | Dense dripstone growth               | Cave dripstone rooms           |
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
| `multifaceGrowth(...)`            | Multi-face block spread              | Glow lichen, sculk vein        |
| `netherForestVegetation(...)`     | Nether plant scatter                 | Warped/crimson forest floors   |
| `netherrackReplaceBlobs(...)`     | Replace netherrack with blobs        | Nether gravel/blackstone blobs |
| `pointedDripstone(...)`           | Single pointed dripstone             | Cave stalactites/stalagmites   |
| `randomBooleanSelector(...)`      | Picks one of two features randomly   | Symmetric ore variants         |
| `randomSelector(...)`             | Weighted random feature picker       | Mixed ore deposits             |
| `replaceSingleBlock(...)`         | Replace blocks by rule targets       | Custom block swaps             |
| `rootSystem(...)`                 | Root placer for trees                | Mangrove roots                 |
| `scatteredOre(...)`               | Scattered ore deposits               | Nether gold ore blobs          |
| `sculkPatch(...)`                 | Sculk spread with catalyst           | Ancient city surroundings      |
| `seagrass(probability)`           | Seagrass placement                   | Ocean floors                   |
| `seaPickle(count)`                | Sea pickle colonies                  | Warm ocean floors              |
| `simpleRandomSelector(...)`       | Uniform random feature picker        | Coral type variety             |
| `spike(...)`                      | Tall spiky columns                   | Ice spikes in frozen biomes    |
| `springFeature(...)`              | Fluid source block                   | Water/lava springs             |
| `twistingVines(...)`              | Twisting vine growth                 | Warped forest floors           |
| `underwaterMagma(...)`            | Underwater magma blocks              | Ocean floors                   |
| `vegetationPatch(...)`            | Vegetation on surfaces               | Cave moss patches              |
| `waterloggedVegetationPatch(...)` | Waterlogged vegetation patches       | Underwater cave plants         |

### No-Config Features

These feature types have no configuration fields and are used directly as Kotlin `data object` values:

```kotlin
configuredFeature("basalt_pillar", BasaltPillar)
configuredFeature("desert_well", DesertWell)
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
| `blockPredicateFilter(predicate)`     | Custom block condition                           |
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

### Height Providers

Height providers control vertical distribution. Different providers create different ore/feature distributions:

- **Uniform** - Equal chance at all heights (good for evenly distributed ores)
- **Trapezoid** - Peaks in the middle, tapers at edges (vanilla iron ore pattern)
- **Biased to bottom** - Concentrates near the minimum Y
- **Very biased to bottom** - More extreme bottom concentration (vanilla diamond pattern)
- **Weighted list** - Picks from multiple providers by weight

Reference: [Height provider](https://minecraft.wiki/w/Height_provider)

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

// Biased toward the bottom
heightRange(biasedToBottomHeightProvider(minInclusive = -64, maxInclusive = 0))

// Strongly biased toward the bottom
heightRange(veryBiasedToBottomHeightProvider(minInclusive = -64, maxInclusive = 16))
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
	val oakTreeCfg = tree {
		blobFoliagePlacer(radius = constant(2), offset = constant(0), height = 3)
		foliageProvider = simpleStateProvider(Blocks.OAK_LEAVES)
		straightTrunkPlacer(baseHeight = 5, heightRandA = 2, heightRandB = 0)
		trunkProvider = simpleStateProvider(Blocks.OAK_LOG)
	}
	val oakTree = configuredFeature("oak_tree", oakTreeCfg) {}

	// 2) Tree placed feature
	val oakTreePlaced = placedFeature("oak_tree_placed", oakTree) {
		inSquare()
		count(constant(8))
		heightRange(uniformHeightProvider(64, 100))
		biome()
	}

	// 3) Flower configured feature
	val flowerCfg = simpleBlock(toPlace = simpleStateProvider(Blocks.POPPY))
	val flower = configuredFeature("forest_flower", flowerCfg) {}

	// 4) Flower placed feature
	val flowerPlaced = placedFeature("forest_flower_placed", flower) {
		inSquare()
		rarityFilter(4)
		heightRange(uniformHeightProvider(64, 100))
		biome()
	}

	// 5) Ore configured feature
	val ironOreCfg = ore(
		size = 9,
		discardChanceOnAirExposure = 0.0,
		targets = listOf(Target())
	)
	val ironOre = configuredFeature("iron_ore", ironOreCfg) {}

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
