---
root: .components.layouts.MarkdownLayout
title: Features
nav-title: Features
description: Generate trees, ores and vegetation in Minecraft with Kore - configured features, placement modifiers and block state providers.
keywords: minecraft, datapack, kore, worldgen, configured feature, placed feature, tree, ore, block state provider
date-created: 2026-02-03
date-modified: 2026-08-21
routeOverride: /docs/data-driven/worldgen/features
---

# Features

Features are everything the game scatters over the terrain during the `features` step: trees, ores, flowers, geodes, springs. They are
declared in two files:

- A **configured feature** says *what* to place - the tree species, the ore type, the block - with all its parameters.
- A **placed feature** wraps a configured feature and says *where* and *how often* - count per chunk, height range, rarity, filters.

The split is what lets one tree configuration be placed densely in a forest and sparsely in plains. A biome only ever references placed
features, listed per [decoration step](/docs/data-driven/worldgen#decoration-steps).

References: [Configured feature](https://minecraft.wiki/w/Configured_feature), [Placed feature](https://minecraft.wiki/w/Placed_feature)

---

## Configured Features

Each feature type is a function on `configuredFeaturesBuilder`, taking the file name first. One call produces one file and returns its
`ConfiguredFeatureArgument`, so a configured feature always holds exactly one feature type.

Declare several at once inside a `configuredFeatures { }` block, or call `configuredFeaturesBuilder` directly to capture the argument:

```kotlin
val flower = dp.configuredFeaturesBuilder.simpleBlock("my_flower") {
	toPlace = simpleStateProvider(Blocks.DANDELION)
}

dp.configuredFeatures {
	seagrass("my_seagrass", probability = 0.3)
	desertWell("my_well")
}
```

### Tree

Trees combine a trunk placer, a foliage placer and block state providers for the blocks themselves. `belowTrunkProvider` takes a
`ruleBasedStateProvider` and defaults to an empty one, which places nothing under the trunk.

```kotlin
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
```

### Ore

Ore features replace existing terrain blocks. `size` is the maximum vein size, and `discardChanceOnAirExposure` skips blocks exposed to air
(`0.0` for full veins, `1.0` to never show an ore in a cave wall).

```kotlin
ore("my_ore", size = 10, discardChanceOnAirExposure = 0.1) {
	targets {
		target(blockState(Blocks.IRON_ORE)) {
			target = tagMatch(Tags.Block.STONE_ORE_REPLACEABLES)
		}

		target(blockState(Blocks.DEEPSLATE_IRON_ORE)) {
			target = tagMatch(Tags.Block.DEEPSLATE_ORE_REPLACEABLES)
		}
	}
}
```

`targets { }` is shared by `ore`, `scatteredOre` and `replaceSingleBlock`, the three features replacing terrain blocks; the first target
matching a block wins. The rule test builders (`blockMatch`, `tagMatch`, `randomBlockMatch`, ...) are scoped to `target { }` and are the
same ones the [structure processors](/docs/data-driven/worldgen/structures#rules) use.

Reference: [Ore feature](https://minecraft.wiki/w/Ore_(feature))

### Block Column

`blockColumn` stacks layers along a direction, each declared inside `layers { }` with its height and its provider.

```kotlin
blockColumn("cave_vines", direction = Direction.DOWN) {
	allowedPlacement { replaceable() }
	layers {
		layer(constant(3)) { provider = simpleStateProvider(Blocks.CAVE_VINES_PLANT) }
		layer(constant(1)) { provider = simpleStateProvider(Blocks.CAVE_VINES) }
	}
}
```

### All Feature Types

Kore covers every vanilla configured feature type.

| Builder                           | Places                               | Vanilla use                    |
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
| `fillLayer(...)`                  | A full layer of blocks               | Custom dimension layers        |
| `fossil(...)`                     | Structure-based fossil               | Underground fossils            |
| `geode(...)`                      | Hollow structure with layered shells | Amethyst geodes                |
| `hugeBrownMushroom(...)`          | Large brown mushroom                 | Swamp/mushroom island fungi    |
| `hugeFungus(...)`                 | Huge nether fungus                   | Crimson/warped forests         |
| `hugeRedMushroom(...)`            | Large red mushroom                   | Swamp/mushroom island fungi    |
| `iceberg(...)`                    | Iceberg                              | Frozen ocean icebergs          |
| `lake(...)`                       | Liquid pool                          | Underground lava lakes         |
| `largeDripstone(...)`             | Tall stalactite or stalagmite        | Cave ceilings/floors           |
| `multifaceGrowth(block, ...)`     | Multi-face block spread              | Glow lichen, sculk vein        |
| `netherForestVegetation(...)`     | Nether plant scatter                 | Warped/crimson forest floors   |
| `netherrackReplaceBlobs(...)`     | Blobs replacing netherrack           | Nether gravel/blackstone blobs |
| `ore(...)`                        | Ore vein replacing terrain           | Every overworld ore            |
| `randomBooleanSelector(...)`      | One of two features, at random       | Symmetric ore variants         |
| `randomSelector(...)`             | Weighted random feature picker       | Mixed ore deposits             |
| `replaceSingleBlock(...)`         | Blocks replaced by rule targets      | Custom block swaps             |
| `rootSystem(...)`                 | Roots under a tree                   | Mangrove roots                 |
| `scatteredOre(...)`               | Scattered ore deposits               | Nether gold ore blobs          |
| `sculkPatch(...)`                 | Sculk spread with catalyst           | Ancient city surroundings      |
| `seaPickle(count)`                | Sea pickle colonies                  | Warm ocean floors              |
| `seagrass(probability)`           | Seagrass                             | Ocean floors                   |
| `sequence(...)`                   | Several features, in order           | Multi-step feature chains      |
| `simpleBlock(...)`                | A single block state                 | Flowers, mushrooms             |
| `simpleRandomSelector(...)`       | Uniform random feature picker        | Coral type variety             |
| `speleothem(...)`                 | Single pointed speleothem            | Cave stalactites/stalagmites   |
| `speleothemCluster(...)`          | Dense speleothem growth              | Cave speleothem rooms          |
| `spike(...)`                      | Tall spiky columns                   | Ice spikes in frozen biomes    |
| `springFeature(...)`              | Fluid source block                   | Water/lava springs             |
| `template(...)`                   | A weighted structure template        | Random structure variants      |
| `tree(...)`                       | A tree, trunk and foliage            | Every vanilla tree             |
| `twistingVines(...)`              | Twisting vine growth                 | Warped forest floors           |
| `underwaterMagma(...)`            | Underwater magma blocks              | Ocean floors                   |
| `vegetationPatch(...)`            | Vegetation on surfaces               | Cave moss patches              |
| `waterloggedVegetationPatch(...)` | Waterlogged vegetation patches       | Underwater cave plants         |
| `weightedRandomSelector(...)`     | Weighted random feature picker       | Mixed ore/vegetation deposits  |

### Features Without Configuration

Eighteen feature types have no fields at all. Their builders take only a file name and nothing else:

```kotlin
configuredFeatures {
	desertWell("my_well")
	monsterRoom("my_dungeon")
}
```

`basaltPillar`, `blueIce`, `bonusChest`, `chorusPlant`, `coralClaw`, `coralMushroom`, `coralTree`, `desertWell`, `endIsland`,
`endPlatform`, `freezeTopLayer`, `glowstoneBlob`, `kelp`, `monsterRoom`, `noOp`, `vines`, `voidStartPlatform`, `weepingVines`.

Reference: [Feature types](https://minecraft.wiki/w/Configured_feature#Types)

---

## Block State Providers

A block state provider decides which block state is placed at a position. It fills every field typed `BlockStateProvider`: `toPlace` on
`simpleBlock`, `stateProvider` on `disk`, `trunkProvider` and `foliageProvider` on `tree`, and so on.

Every builder extends `BlockStateProviderScope`, so they resolve inside the configuration block of a feature actually taking a provider, and
nowhere else. Providers nest freely: any of them can be the `source` of a `randomizedIntStateProvider`, the `data` of a weighted entry or
the `then` of a rule.

| Builder                                    | Picks the state from                                              |
|--------------------------------------------|-------------------------------------------------------------------|
| `dualNoiseProvider { }`                    | Two Perlin noises, one selecting the variety, one picking a state |
| `noiseProvider { }`                        | A single Perlin noise mapped onto a list of states                |
| `noiseThresholdProvider { }`               | A Perlin noise compared against a threshold                       |
| `randomizedIntStateProvider(property) { }` | Another provider, with one integer property randomized            |
| `rotatedBlockProvider(block)`              | A fixed block with a random `axis`                                |
| `ruleBasedStateProvider { }`               | Ordered block predicate rules, with an optional fallback          |
| `simpleStateProvider(block)`               | A fixed block                                                     |
| `weightedStateProvider { }`                | A weighted random pick across several providers                   |

Reference: [Block state provider](https://minecraft.wiki/w/Block_state_provider)

### Simple And Rotated

Both take a block, a block plus a property map, or a block plus a block state builder. `rotatedBlockProvider` ignores the `axis` of the
state it is given and randomizes it at every position.

```kotlin
simpleStateProvider(Blocks.DANDELION)
simpleStateProvider(Blocks.OAK_LOG, mapOf("axis" to "y"))
rotatedBlockProvider(Blocks.OAK_LOG)
```

### Weighted

Entries are declared inside the provider block, each drawn proportionally to its weight, which defaults to `1`. An entry takes a block, a
block state, another provider, or a receiver block setting `data` itself.

```kotlin
weightedStateProvider {
	entry(Blocks.DANDELION, weight = 3)
	entry(Blocks.POPPY)
	entry(weight = 2) {
		data = rotatedBlockProvider(Blocks.OAK_LOG)
	}
}
```

### Noise-Based

The three noise-based providers share `seed`, `scale` and a `noise { }` block. `noise(firstOctave, vararg amplitudes)` is the short form and
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

### Randomized Int State

Takes the state given by `source` and overrides one integer block state property with a sampled value. The block placed by `source` has to
declare that property.

```kotlin
randomizedIntStateProvider("age") {
	values(0, 7) // Shorthand for a uniform int provider.
	source = simpleStateProvider(Blocks.WHEAT)
}
```

### Rule Based

Rules are evaluated top-to-bottom and the first matching one wins, falling back to `fallback` when nothing matches. With no `fallback` and
no matching rule, the feature places nothing.

```kotlin
ruleBasedStateProvider {
	fallback = simpleStateProvider(Blocks.STONE)

	// The provider first, the predicate in the trailing lambda.
	rule(simpleStateProvider(Blocks.DIRT)) {
		solid()
	}

	// Or a receiver block, with ifTrue { } and then.
	rule {
		ifTrue {
			not { matchingBlockTag(Tags.Block.CANNOT_REPLACE_BELOW_TREE_TRUNK) }
			solid()
		}
		then = simpleStateProvider(Blocks.SAND)
	}
}
```

Both predicate blocks run on a block predicate scope: a single predicate is used as-is, several are wrapped in an `all_of`. See
[Block Predicates](/docs/data-driven/worldgen/block-predicates) for the full list.

---

## Placed Features

A placed feature wraps a configured feature with **placement modifiers**, applied in order. Each modifier filters or transforms the stream
of candidate positions, so the order matters: `count()` multiplies positions, `heightRange()` moves them vertically, `biome()` drops the
ones that ended up outside the biome.

```kotlin
val treePlaced = dp.placedFeature("my_tree_placed", treeConfigured) {
	inSquare()
	count(constant(10))
	heightRange(uniformHeightProvider(64, 128))
	biome()
}
```

The usual shape is: pick a frequency, spread horizontally, position vertically, then filter. `biome()` belongs last, since it has to see the
final position.

| Modifier                              | Effect                                              |
|---------------------------------------|-----------------------------------------------------|
| `biome()`                             | Drops positions outside a biome listing the feature |
| `blockPredicateFilter { }`            | Drops positions failing a block predicate           |
| `carvingMask(step)`                   | Keeps only blocks carved by `air` or `liquid`       |
| `count(n)`                            | Repeats the position `n` times per chunk            |
| `countOnEveryLayer(n)`                | Repeats `n` times on every solid layer              |
| `environmentScan(...)`                | Scans up or down for a valid position               |
| `fixedPlacement(...)`                 | Replaces positions with absolute coordinates        |
| `heightMap(type)`                     | Moves the position onto a heightmap                 |
| `heightRange(provider)`               | Moves the position to a sampled Y level             |
| `inSquare()`                          | Randomizes X and Z inside the chunk                 |
| `noiseBasedCount(...)`                | Count driven by the noise value at the position     |
| `noiseThresholdCount(...)`            | One of two counts, chosen by a noise threshold      |
| `randomOffset(xzSpread, ySpread)`     | Scatters the position within an XZ/Y radius         |
| `rarityFilter(chance)`                | Keeps the position with a `1/chance` probability    |
| `surfaceRelativeThresholdFilter(...)` | Filters on the distance to the surface              |
| `surfaceWaterDepthFilter(maxDepth)`   | Drops positions under deeper water than `maxDepth`  |

`heightRange` and `count` take a height provider and an int provider; see [Providers](/docs/data-driven/worldgen/providers) for the
distributions available.

Reference: [Placement modifier](https://minecraft.wiki/w/Placed_feature#Placement_modifiers)

---

## Complete Example

An ore and a tree, from configured feature to biome.

```kotlin
fun DataPack.createForestFeatures() {
	val oakTree = configuredFeaturesBuilder.tree("oak_tree") {
		blobFoliagePlacer(radius = constant(2), offset = constant(0), height = 3)
		foliageProvider = simpleStateProvider(Blocks.OAK_LEAVES)
		straightTrunkPlacer(baseHeight = 5, heightRandA = 2, heightRandB = 0)
		trunkProvider = simpleStateProvider(Blocks.OAK_LOG)
	}

	val oakTreePlaced = placedFeature("oak_tree_placed", oakTree) {
		inSquare()
		count(constant(8))
		heightRange(uniformHeightProvider(64, 100))
		biome()
	}

	val ironOre = configuredFeaturesBuilder.ore("iron_ore", size = 9, discardChanceOnAirExposure = 0.0) {
		targets {
			target(blockState(Blocks.IRON_ORE)) {
				target = tagMatch(Tags.Block.STONE_ORE_REPLACEABLES)
			}
		}
	}

	val ironOrePlaced = placedFeature("iron_ore_placed", ironOre) {
		inSquare()
		count(constant(20))
		heightRange(uniformHeightProvider(-64, 72))
		biome()
	}

	biome("custom_forest") {
		temperature = 0.7f
		downfall = 0.8f
		hasPrecipitation = true

		features {
			undergroundOres = listOf(ironOrePlaced)
			vegetalDecoration = listOf(oakTreePlaced)
		}
	}
}
```

## See Also

- [Biomes](/docs/data-driven/worldgen/biomes) - listing placed features per decoration step
- [Block Predicates](/docs/data-driven/worldgen/block-predicates) - the tests used by placement filters and providers
- [Providers](/docs/data-driven/worldgen/providers) - height, int and float providers
- [World Generation](/docs/data-driven/worldgen) - overview of the worldgen system
