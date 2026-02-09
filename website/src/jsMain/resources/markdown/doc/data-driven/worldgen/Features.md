---
root: .components.layouts.MarkdownLayout
title: Features
nav-title: Features
description: Create configured and placed features (trees, ores, vegetation) with Kore's DSL.
keywords: minecraft, datapack, kore, worldgen, configured feature, placed feature, tree, ore
date-created: 2026-02-03
date-modified: 2026-02-04
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
	dirtProvider = simpleStateProvider(Blocks.DIRT)
	foliageProvider = simpleStateProvider(Blocks.OAK_LEAVES)
	straightTrunkPlacer(baseHeight = 6, heightRandA = 3, heightRandB = 1)
	trunkProvider = simpleStateProvider(Blocks.OAK_LOG)
}

val tree = dp.configuredFeature("my_tree", treeCfg) {}
```

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

Kore supports many feature types. Here are the most commonly used:

| Feature Type     | Description                   | Example Use                    |
|------------------|-------------------------------|--------------------------------|
| `blockPile`      | Piles of blocks               | Pumpkin patches, melon patches |
| `diskFeature`    | Circular disk of blocks       | Clay, sand, gravel patches     |
| `fossilFeature`  | Structure-based fossils       | Underground fossils            |
| `geode`          | Hollow structures with layers | Amethyst geodes                |
| `icebergFeature` | Iceberg structures            | Ocean icebergs                 |
| `lakeFeature`    | Liquid pools                  | Underground lava lakes         |
| `randomPatch`    | Scattered block placements    | Flowers, grass, mushrooms      |
| `springFeature`  | Fluid source blocks           | Water/lava springs             |

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
- Apply `heightRange()` or `heightmap()` for vertical positioning
- End with `biome()` to respect biome boundaries

Reference: [Placement modifier](https://minecraft.wiki/w/Placed_feature#Placement_modifiers)

| Modifier                              | Description                   |
|---------------------------------------|-------------------------------|
| `biome()`                             | Only place in valid biomes    |
| `blockPredicateFilter(predicate)`     | Custom block condition        |
| `count(n)`                            | Place n times per chunk       |
| `countOnEveryLayer(n)`                | Place n times on every layer  |
| `environmentScan(...)`                | Scan for valid placement      |
| `heightmap(type)`                     | Place relative to heightmap   |
| `heightRange(provider)`               | Vertical placement range      |
| `inSquare()`                          | Spread horizontally in chunk  |
| `rarityFilter(chance)`                | 1/chance probability to place |
| `surfaceRelativeThresholdFilter(...)` | Surface-relative placement    |
| `surfaceWaterDepthFilter(maxDepth)`   | Max water depth filter        |

### Height Providers

Height providers control vertical distribution. Different providers create different ore/feature distributions:

- **Uniform** - Equal chance at all heights (good for evenly distributed ores)
- **Trapezoid** - Peaks in the middle, tapers at edges (vanilla iron ore pattern)
- **Very biased to bottom** - Concentrates near minimum Y (vanilla diamond pattern)

Reference: [Height provider](https://minecraft.wiki/w/Height_provider)

```kotlin
// Uniform distribution between min and max
heightRange(uniformHeightProvider(minY = 0, maxY = 64))

// Triangular distribution (peaks at center)
heightRange(trapezoidHeightProvider(minY = 0, maxY = 64, plateau = 20))

// Constant height
heightRange(constantHeightProvider(y = 32))

// Relative to world bounds
heightRange(veryBiasedToBottomHeightProvider(minY = 0, maxY = 64))
```

---

## Complete Example

```kotlin
fun DataPack.createForestFeatures() {
	// 1) Tree configured feature
	val oakTreeCfg = tree {
		blobFoliagePlacer(radius = constant(2), offset = constant(0), height = 3)
		dirtProvider = simpleStateProvider(Blocks.DIRT)
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
