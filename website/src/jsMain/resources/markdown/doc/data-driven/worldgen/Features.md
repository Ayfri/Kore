---
root: .components.layouts.MarkdownLayout
title: Features
nav-title: Features
description: Generate trees, ores and vegetation in Minecraft with Kore - configured features, placement modifiers and block state providers.
keywords: minecraft, datapack, kore, worldgen, configured feature, placed feature, tree, ore, block state provider
date-created: 2026-02-03
date-modified: 2026-10-22
routeOverride: /docs/data-driven/worldgen/features
---

# Features

Features are everything the game scatters over the terrain during the `features` step: trees, ores, flowers, geodes, springs. They are
declared in two files:

- A **configured feature** says *what* to place - the tree species, the ore type, the block - with all its parameters.
- A **placed feature** wraps a configured feature and says *where* and *how often* - count per chunk, height range, rarity, filters.

The split is what lets one tree configuration be placed densely in a forest and sparsely in plains. A biome only ever references placed
features, listed per [decoration step](/docs/data-driven/worldgen#decoration-steps).

References: [Configured feature](https://minecraft.wiki/w/Configured_feature), [Placed feature](https://minecraft.wiki/w/Placed_feature),
misode's [feature](https://misode.github.io/worldgen/feature/) and [placed feature](https://misode.github.io/worldgen/placed-feature/)
generators

---

## Configured Features

Each feature type is a function on `configuredFeaturesBuilder`, taking the file name first: one call produces one file and returns its
`FeatureArgument`, so a configured feature always holds exactly one feature type. Declare several at once inside a `configuredFeatures { }`
block, or call `configuredFeaturesBuilder` directly to capture the argument:

```kotlin
val flower = dp.configuredFeaturesBuilder.simpleBlock("my_flower") {
	toPlace = simpleStateProvider(Blocks.DANDELION)
}

dp.configuredFeatures {
	bamboo("my_bamboo", probability = 0.3)
	desertWell("my_well")
}
```

Each file lands in `data/<namespace>/worldgen/feature/<name>.json`, its fields next to `type`:

```json
{
	"type": "minecraft:bamboo",
	"probability": 0.3
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

Every vanilla trunk placer, foliage placer and decorator has its builder, like the poplar ones: a straight trunk branching where the
rhombus-shaped foliage starts, with shelf mushrooms on the logs.

```kotlin
tree("my_poplar") {
	poplarTrunkPlacer {
		baseHeight = 7
		branchAmount = uniform(1, 4)
		trunkHeightAboveBranches = constant(4)
	}
	poplarFoliagePlacer {
		height = uniform(5, 6)
		sideHoleChance = 0.15
	}
	decorators { shelfMushroom(0.4) }
}
```

The other decorators are `alterGround`, `attachedToLeaves`, `attachedToLogs`, `beehive`, `cocoa`, `creakingHeart`, `leaveVine`, `paleMoss`,
`placeOnGround` and `trunkVine`.

A `fallenTree` takes the same decorators twice: `stumpDecorators` for the stump, `logDecorators` for the log lying next to it, whose
length is `logLength`. `attachedToLogs` always takes at least one direction, given as its arguments.

```kotlin
fallenTree("my_fallen_oak") {
	trunkProvider = simpleStateProvider(blockState(Blocks.OAK_LOG, "axis" to "y"))
	logLength = uniform(4, 7)
	stumpDecorators { trunkVine() }
	logDecorators {
		attachedToLogs(Direction.UP) {
			probability = 0.1
			blockProvider = simpleStateProvider(Blocks.RED_MUSHROOM)
		}
	}
}
```

`rootPlacer` adds roots under the trunk and raises the trunk above them. Only the mangrove one exists, `mangroveRootPlacer`, with the vanilla
mangrove values below: the roots grow up to 8 blocks wide and 15 long, skew off their course 20% of the time, turn muddy over mud, and get
a moss carpet on top half of the time.

```kotlin
tree("my_mangrove") {
	rootPlacer = mangroveRootPlacer {
		rootProvider = simpleStateProvider(blockState(Blocks.MANGROVE_ROOTS, "waterlogged" to "false"))
		trunkOffsetY = uniform(1, 3)
		aboveRootPlacement(chance = 0.5) {
			aboveRootProvider = simpleStateProvider(Blocks.MOSS_CARPET)
		}
		mangroveRootPlacement {
			maxRootWidth = 8
			maxRootLength = 15
			randomSkewChance = 0.2
			canGrowThrough = listOf(Tags.Block.MANGROVE_ROOTS_CAN_GROW_THROUGH)
			muddyRootsIn = listOf(Blocks.MUD, Blocks.MUDDY_MANGROVE_ROOTS)
			muddyRootsProvider = simpleStateProvider(blockState(Blocks.MUDDY_MANGROVE_ROOTS, "axis" to "y"))
		}
	}
}
```

### Ore

Ore features replace existing terrain blocks. `size` is the maximum vein size, higher means bigger veins. `discardChanceOnAirExposure`
skips blocks exposed to air (`0.0` for full veins, `1.0` to never show an ore in a cave wall). Vanilla values:

| Vanilla feature      | `size` | `discardChanceOnAirExposure` |
|----------------------|--------|------------------------------|
| `ore_iron`           | `9`    | `0.0`                        |
| `ore_iron_small`     | `4`    | `0.0`                        |
| `ore_gold`           | `9`    | `0.0`                        |
| `ore_gold_buried`    | `9`    | `0.5`                        |
| `ore_diamond_small`  | `4`    | `0.5`                        |
| `ore_diamond_medium` | `8`    | `0.5`                        |
| `ore_diamond_large`  | `12`   | `0.7`                        |
| `ore_diamond_buried` | `8`    | `1.0`                        |

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
same ones the [structure processors](/docs/data-driven/worldgen/structures#rules) use. Vanilla bounds each ore target in height by
combining them:

```kotlin
target(blockState(Blocks.IRON_ORE)) {
	target = allOf(tagMatch(Tags.Block.STONE_ORE_REPLACEABLES), heightMatch(minInclusive = 0, maxInclusive = 2031))
}
```

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

### Columns, Pillars And Spreads

Three features build the basalt deltas and the glowstone of the Nether. Their predicate properties (`canReplace`, `continueThrough`) take
the [block predicate](/docs/data-driven/worldgen/block-predicates) builders.

`steppedColumnCluster` grows a cluster of columns up from the ground, each one stepping down towards its edges.

| Property          | Range    | Tuning                                                                                         |
|-------------------|----------|------------------------------------------------------------------------------------------------|
| `columnCount`     | 1..150   | How many columns the cluster has, more columns make a denser, wider cluster.                   |
| `clusterReach`    | 0..13    | How far from the placement position columns can start, capped by `height`.                     |
| `columnReach`     | 0..3     | How wide each column is, `0` gives one-block-wide spikes.                                      |
| `height`          | 1..10    | How tall the columns are.                                                                      |
| `canReplace`      | -        | The blocks a column can grow into.                                                             |
| `continueThrough` | -        | The blocks a growing column goes through instead of stopping, such as its own block.           |
| `cannotPlaceOn`   | -        | A block, a block tag or a list of blocks a column cannot start from.                           |

```kotlin
steppedColumnCluster("basalt_cluster") {
	block = simpleStateProvider(Blocks.BASALT)
	canReplace { matchingBlocks(Blocks.AIR, Blocks.LAVA) }
	continueThrough { matchingBlocks(Blocks.BASALT) }
	cannotPlaceOn(Blocks.LAVA)
	columnCount = constant(50)
	clusterReach = constant(5)
	columnReach = uniform(2, 3)
	height = uniform(5, 10)
}
```

`singleBlockPillar` stacks one block after the other, up or down from the placement position.

| Property           | Default | Tuning                                                                          |
|--------------------|---------|---------------------------------------------------------------------------------|
| `direction`        | `UP`    | `VerticalDirection.UP` grows towards the sky, `DOWN` hangs from a ceiling.      |
| `chanceToContinue` | `1`     | Chance (`0` to `1`) to place one more block, lower values give shorter pillars. |
| `canReplace`       | any     | The blocks the pillar can grow into.                                            |
| `capFeature`       | none    | A placed feature run at the end of the pillar.                                  |

```kotlin
singleBlockPillar("basalt_pillar", VerticalDirection.DOWN) {
	block = simpleStateProvider(Blocks.BASALT)
	canReplace { matchingBlocks(Blocks.AIR) }
	chanceToContinue = 0.9f
}
```

`randomNeighborSpread` sets `block` on the placement position, then tries random positions around it and places `block` on each one that
passes `canReplace` and has exactly one of its 6 face neighbors in `acceptedNeighbors`, so a blob grows outwards from the blocks it is
attached to.

| Property            | Range    | Tuning                                                                              |
|---------------------|----------|-------------------------------------------------------------------------------------|
| `attempts`          | 1..3000  | How many positions are tried, more attempts give a bigger blob.                     |
| `xzOffset`          | -16..16  | Horizontal spread of the attempts, a `trapezoid` keeps most near the center.        |
| `yOffset`           | -16..16  | Vertical spread of the attempts, `uniform(-11, 0)` only grows downwards.            |
| `acceptedNeighbors` | -        | A block, a block tag or a list of blocks, exactly one face neighbor has to be one.  |
| `canReplace`        | -        | The blocks that can be replaced.                                                    |

```kotlin
randomNeighborSpread("glowstone_extra") {
	block = simpleStateProvider(Blocks.GLOWSTONE)
	acceptedNeighbors(Blocks.GLOWSTONE)
	canReplace { matchingBlocks(Blocks.AIR) }
	attempts = constant(1500)
	xzOffset = trapezoid(-7, 7, 0)
	yOffset = uniform(-11, 0)
}
```

### Overlay And Patchy Squares

`overlay` places every placed feature of its list, whether or not the others were placed, where `sequence` stops at the first one that
isn't. A single feature or tag is written as a bare string.

```kotlin
overlay("basalt_pillars", PlacedFeatures.BASALT_BLOBS, PlacedFeatures.BASALT_PILLAR)
```

`overlay`, `sequence` and `simpleRandomSelector` also take a block where `feature(...)` adds a placed feature by ID and `placed(...) { }`
adds a placed feature written inline, with its placement modifiers declared in the block, using the same builders as `placedFeature { }`.
A tag isn't possible in the block because the game only reads a tag as the whole field, never as an entry of a list. An inline placed
feature references its configured feature by ID: Kore can't write the configured feature inline yet, because the configured feature builders
always register a file. A single entry is written bare, as a string or as an object.

```kotlin
overlay("basalt_pillars") {
	feature(PlacedFeatures.BASALT_BLOBS)
	placed(Features.BASALT_PILLAR) {
		offset(y = constant(-2))
	}
}
```

```json
{
	"type": "minecraft:overlay",
	"features": [
		"minecraft:basalt_blobs",
		{
			"feature": "minecraft:basalt_pillar",
			"placement": [{"type": "minecraft:offset", "x": 0, "y": -2, "z": 0}]
		}
	]
}
```

`projectedRandomPatchySquare` covers a square of side `2 * size + 1` centered on the placement position, keeping each column with a
probability that falls off towards the corners (the center cross is always kept). Each kept column is projected downward through the
blocks matching `projectThrough`, for at most `maxProjectionHeight` steps, and `block` is placed where it stops. Vanilla caps its basalt
pillars with it.

| Property              | Range  | Tuning                                                                      |
|-----------------------|--------|-----------------------------------------------------------------------------|
| `size`                | 1..16  | Half the side of the square, sampled once per placement.                    |
| `maxProjectionHeight` | 0..    | The most steps down a column is projected, `0` behaving like `1`.           |
| `projectThrough`      | -      | The blocks a projection passes through while moving down, usually air.      |

```kotlin
projectedRandomPatchySquare("basalt_cap") {
	block = simpleStateProvider(Blocks.BASALT)
	projectThrough { matchingBlocks(Blocks.AIR) }
	maxProjectionHeight = 3
	size = constant(3)
}
```

### Templates And Processors

`template` places one of its weighted structure templates, picked by `weight`, and runs the optional `processors` list on it. `processors`
takes a processor list by ID with `processorListOrInline(ProcessorLists.X)`, or written inline with `processorListOrInline { }`, whose block
holds the same [processor builders](https://kore.ayfri.com/docs/data-driven/worldgen/structures) as `processorList { }`.

```kotlin
template("desert_well", structureTemplate(Structures.DesertWell.WELL), processors = processorListOrInline(ProcessorLists.EMPTY))
```

```kotlin
template(
	"desert_well_sand",
	structureTemplate(Structures.DesertWell.SUSPICIOUS_SAND),
	processors = processorListOrInline {
		rules {
			rule {
				inputPredicate = blockMatch(Blocks.SUSPICIOUS_SAND)
				locationPredicate = alwaysTrue()
				outputState = blockState(Blocks.SUSPICIOUS_SAND)
				blockEntityModifier = appendLoot(LootTables.Archaeology.DESERT_WELL)
			}
		}
	},
)
```

```json
{
	"type": "minecraft:template",
	"processors": {
		"processors": [
			{
				"processor_type": "minecraft:rule",
				"rules": [
					{
						"location_predicate": {"predicate_type": "minecraft:always_true"},
						"input_predicate": {"predicate_type": "minecraft:block_match", "block": "minecraft:suspicious_sand"},
						"output_state": "minecraft:suspicious_sand",
						"block_entity_modifier": {"type": "minecraft:append_loot", "loot_table": "minecraft:archaeology/desert_well"}
					}
				]
			}
		]
	},
	"templates": [{"data": {"id": "minecraft:desert_well/suspicious_sand"}, "weight": 1}]
}
```

An ID is written as a bare string (`"processors": "minecraft:empty"`). `fossil` takes both forms for `fossilProcessors` and
`overlayProcessors`, and so do the `single` and `legacySingle` [template pool](https://kore.ayfri.com/docs/data-driven/worldgen/structures)
elements.

### All Feature Types

Kore covers every vanilla configured feature type.

| Builder                            | Places                               | Vanilla use                    |
|------------------------------------|--------------------------------------|--------------------------------|
| `bamboo(probability)`              | Bamboo stalks                        | Jungle bamboo                  |
| `blockBlob(...)`                   | Small block blob on a surface        | Mossy cobblestone in forests   |
| `blockColumn(...)`                 | Vertical stack of blocks with layers | Custom pillars                 |
| `blockPile(...)`                   | Piles of blocks                      | Pumpkin/melon patches          |
| `coralClaw(feature)`               | A claw-shaped coral                  | Warm ocean vegetation          |
| `coralTree(feature)`               | A branching coral tree               | Warm ocean vegetation          |
| `deltaFeature(...)`                | Basalt delta with contents and rim   | Nether basalt deltas           |
| `disk(...)`                        | Circular disk of blocks              | Clay, sand, gravel patches     |
| `endGateway(...)`                  | End gateway portal                   | End outer islands              |
| `endPodium(active)`                | End exit portal podium               | Dragon fight portal            |
| `endSpike(...)`                    | End obsidian pillar with crystal     | The End respawn pillars        |
| `fallenTree(...)`                  | A tree trunk lying on the ground     | Fallen oak, birch, spruce logs |
| `fillLayer(...)`                   | A full layer of blocks               | Custom dimension layers        |
| `fossil(...)`                      | Structure-based fossil               | Underground fossils            |
| `geode(...)`                       | Hollow structure with layered shells | Amethyst geodes                |
| `hugeBrownMushroom(...)`           | Large brown mushroom                 | Swamp/mushroom island fungi    |
| `hugeFungus(...)`                  | Huge nether fungus                   | Crimson/warped forests         |
| `hugeRedMushroom(...)`             | Large red mushroom                   | Swamp/mushroom island fungi    |
| `iceberg(...)`                     | Iceberg                              | Frozen ocean icebergs          |
| `lake(...)`                        | Liquid pool                          | Underground lava lakes         |
| `largeDripstone(...)`              | Tall stalactite or stalagmite        | Cave ceilings/floors           |
| `multifaceGrowth(block, ...)`      | Multi-face block spread              | Glow lichen, sculk vein        |
| `netherrackReplaceBlobs(...)`      | Blobs replacing netherrack           | Nether gravel/blackstone blobs |
| `ore(...)`                         | Ore vein replacing terrain           | Every overworld ore            |
| `overlay(...)`                     | Several features, all of them placed | Basalt delta pillars           |
| `projectedRandomPatchySquare(...)` | Patchy square projected on a surface | Basalt pillar caps             |
| `randomBooleanSelector(...)`       | One of two features, at random       | Symmetric ore variants         |
| `randomNeighborSpread(...)`        | Blocks spreading from neighbors      | Nether glowstone blobs         |
| `randomSelector(...)`              | Weighted random feature picker       | Mixed ore deposits             |
| `replaceSingleBlock(...)`          | Blocks replaced by rule targets      | Custom block swaps             |
| `rootSystem(...)`                  | Roots under a tree                   | Mangrove roots                 |
| `scatteredOre(...)`                | Scattered ore deposits               | Nether gold ore blobs          |
| `sculkPatch(...)`                  | Sculk spread from charges            | Deep dark sculk patches        |
| `sequence(...)`                    | Several features, in order           | Multi-step feature chains      |
| `simpleBlock(...)`                 | A single block state                 | Flowers, mushrooms             |
| `simpleRandomSelector(...)`        | Uniform random feature picker        | Coral type variety             |
| `singleBlockPillar(...)`           | One-block-wide pillar, up or down    | Basalt delta pillars           |
| `speleothem(...)`                  | Single pointed speleothem            | Cave stalactites/stalagmites   |
| `speleothemCluster(...)`           | Dense speleothem growth              | Cave speleothem rooms          |
| `spike(...)`                       | Tall spiky columns                   | Ice spikes in frozen biomes    |
| `springFeature(...)`               | Fluid source block                   | Water/lava springs             |
| `steppedColumnCluster(...)`        | Cluster of stepped columns           | Basalt delta columns           |
| `template(...)`                    | A weighted structure template        | Desert wells, sulfur springs   |
| `tree(...)`                        | A tree, trunk and foliage            | Every vanilla tree             |
| `underwaterMagma(...)`             | Underwater magma blocks              | Ocean floors                   |
| `vegetationPatch(...)`             | Vegetation on surfaces               | Cave moss patches              |
| `waterloggedVegetationPatch(...)`  | Waterlogged vegetation patches       | Underwater cave plants         |
| `weightedRandomSelector(...)`      | Weighted random feature picker       | Mixed ore/vegetation deposits  |

### Features Without Configuration

Eleven feature types have no fields at all. Their builders take only a file name and nothing else:

```kotlin
configuredFeatures {
	desertWell("my_well")
	monsterRoom("my_dungeon")
}
```

`blueIce`, `bonusChest`, `chorusPlant`, `desertWell`, `endIsland`, `endPlatform`, `freezeTopLayer`, `monsterRoom`, `noOp`, `vines`,
`voidStartPlatform`.

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
| `copyPropertiesProvider(source)`           | A source provider, with the existing block's properties copied    |
| `dualNoiseProvider { }`                    | Two Perlin noises, one selecting the variety, one picking a state |
| `noiseProvider { }`                        | A single Perlin noise mapped onto a list of states                |
| `noiseThresholdProvider { }`               | A Perlin noise compared against a threshold                       |
| `randomBlockProvider(blocks)`              | The default state of a random block or block tag                  |
| `randomizedIntStateProvider(property) { }` | Another provider, with one integer property randomized            |
| `rotatedBlockProvider(state, direction)`   | A block or provider, rotated on its `axis` or `facing`            |
| `ruleBasedStateProvider { }`               | Ordered block predicate rules, with an optional fallback          |
| `simpleStateProvider(block)`               | A fixed block                                                     |
| `weightedStateProvider { }`                | A weighted random pick across several providers                   |

Reference: [Block state provider](https://minecraft.wiki/w/Block_state_provider)

### Simple And Rotated

Both take a block, a block plus a property map, or a block plus a block state builder. `rotatedBlockProvider` also takes another provider,
and rotates the `axis` of a log or the `facing` of a dispenser towards `direction`, or towards a random one at every position when
`direction` is omitted.

```kotlin
simpleStateProvider(Blocks.DANDELION)
simpleStateProvider(Blocks.OAK_LOG, mapOf("axis" to "y"))
rotatedBlockProvider(Blocks.OAK_LOG)
rotatedBlockProvider(Blocks.DISPENSER, Direction.NORTH)
rotatedBlockProvider(weightedStateProvider { entry(Blocks.OAK_LOG) }, Direction.UP)
```

### Random Block

`randomBlockProvider` places the default state of a random block among the blocks or block tags it is given.

```kotlin
randomBlockProvider(Blocks.DANDELION, Blocks.POPPY)
randomBlockProvider(Tags.Block.FLOWERS)
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

The three noise-based providers share `seed`, `scale` and a `noise { }` block. `noise(baseOctave, vararg amplitudeModifiers)` is the short form,
and `noise(baseOctave) { }` the receiver form over `NoiseParameters` (`amplitudeModifiers(...)`, `baseAmplitude`, `normalize`, `octaveCount`);
`dualNoiseProvider` gets the same pair as `slowNoise`. `states(...)` takes either blocks or block states.

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
	count(10)
	heightRange(uniformHeightProvider(64, 128))
	biome()
}
```

The usual shape is: pick a frequency, spread horizontally, position vertically, then filter. `biome()` belongs last, since it has to see the
final position.

| Modifier                              | Effect                                              | Vanilla example (`placed_feature/`)                                       |
|---------------------------------------|-----------------------------------------------------|---------------------------------------------------------------------------|
| `biome()`                             | Drops positions outside a biome listing the feature | `ore_diamond`, last of its modifiers                                      |
| `blockPredicateFilter { }`            | Drops positions failing a block predicate           | `acacia`, a `would_survive` check on the sapling                          |
| `carvingMask(step)`                   | Keeps only blocks carved by `air` or `liquid`       | -                                                                         |
| `count(n)`                            | Repeats the position `n` times per chunk            | `ore_diamond`, `7`                                                        |
| `countOnEveryLayer(n)`                | Repeats `n` times on every solid layer              | `delta`, `40`                                                             |
| `cuboid(xzSize, ySize)`               | Spreads the position over a cuboid around it        | -                                                                         |
| `environmentScan(...)`                | Scans up or down for a valid position               | `cave_vines`, scans `up` for at most `12` steps                           |
| `fixedPlacement(...)`                 | Replaces positions with absolute coordinates        | `end_platform`, `[100, 49, 0]`                                            |
| `heightMap(type)`                     | Moves the position onto a heightmap                 | `bamboo_light`, `MOTION_BLOCKING`                                         |
| `heightRange(provider)`               | Moves the position to a sampled Y level             | `ore_diamond`, trapezoid from `aboveBottom(-80)` to `aboveBottom(80)`     |
| `inSquare()`                          | Randomizes X and Z inside the chunk                 | `ore_diamond`                                                             |
| `noiseBasedCount(...)`                | Count driven by the noise value at the position     | `kelp_cold`, `noiseToCountRatio = 120`, `noiseFactor = 80.0`              |
| `noiseThresholdCount(...)`            | One of two counts, chosen by a noise threshold      | `flower_plains`, `noiseLevel = -0.8`, `belowNoise = 15`, `aboveNoise = 4` |
| `offset(x, y, z)`                     | Shifts the position, each axis `-16..16`            | `cave_vines`, `y = -1`                                                    |
| `randomChance(chance)`                | Keeps the position with a `0..1` probability        | -                                                                         |
| `randomlySelected { }`                | Sends each position through one random modifier     | `feature/desert_well`, suspicious sand at one of five `offset`s           |
| `rarityFilter(chance)`                | Keeps the position with a `1/chance` probability    | `lake_lava_surface`, `200`                                                |
| `surfaceRelativeThresholdFilter(...)` | Filters on the distance to the surface              | `glow_lichen`, `OCEAN_FLOOR_WG` with `maxInclusive = -13`                 |
| `surfaceWaterDepthFilter(maxDepth)`   | Drops positions under deeper water than `maxDepth`  | `dark_forest_vegetation`, `0`                                             |

`count`, `countOnEveryLayer`, `cuboid`, `heightRange`, `randomChance` and `rarityFilter` have no default, the value is required. `count`,
`countOnEveryLayer` and `cuboid` take int providers or plain `Int`s (`count(10)` is `count(constant(10))`), `heightRange` takes a height
provider, `randomChance` a `Float` from `0` to `1` and `rarityFilter` a positive `Int`; `cuboid` also takes optional `includeEdges` and
`includeInterior` booleans, both `true` in the game when left out; see [Providers](/docs/data-driven/worldgen/providers) for the distributions available. `offset` takes one int provider per axis:
`constant(1)` moves every position the same way, `uniform(-2, 2)` scatters it. `randomlySelected { }` takes a block of at least one modifier, built with
the same builders as the placed feature itself, and picks one of them at random for each position:

```kotlin
placedFeature("shifted_acacia", Features.ACACIA) {
	randomlySelected {
		offset(y = constant(-1))
		offset(x = constant(-1), y = constant(-1))
		offset(y = constant(-1), z = constant(1))
		offset(x = constant(1), y = constant(-1))
		offset(y = constant(-1), z = constant(-1))
	}
}
```

Vanilla uses it in the `desert_well` configured feature, where the suspicious sand of the well is placed at one of five offsets under it. An empty
block isn't possible because the game requires at least one modifier, so `randomlySelected {}` throws.

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
		count(8)
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
		count(20)
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
