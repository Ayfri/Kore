---
root: .components.layouts.MarkdownLayout
title: Block Predicates
nav-title: Block Predicates
description: Test blocks during world generation with Kore's block predicate DSL - solid, matching blocks, fluids, biomes, tags and offsets.
keywords: minecraft, datapack, kore, worldgen, block predicate, placement condition, matching blocks, would survive
date-created: 2026-08-19
date-modified: 2026-08-21
routeOverride: /docs/data-driven/worldgen/block-predicates
---

# Block Predicates

A block predicate is a test on the state of the block at a given position. Placed feature placement modifiers, many configured features and
the enchantment effects all use them to decide whether something may generate at a position.

Reference: [Block predicate](https://minecraft.wiki/w/Block_predicate)

## Writing a Predicate

Every field taking a block predicate exposes a builder block named after it. Inside that block a single predicate is used as-is, and several
predicates are combined into an `all_of`:

```kotlin
placedFeature("my_flower", flowerFeature) {
	blockPredicateFilter {
		// One predicate: used directly.
		predicate { solid { offset(0, -1, 0) } }
	}

	blockPredicateFilter {
		// Several predicates: wrapped in an `all_of`.
		predicate {
			solid { offset(0, -1, 0) }
			not { matchingFluids(Fluids.WATER) }
		}
	}
}
```

Wrap the body in `anyOf { }` to combine the predicates with an *or* instead:

```kotlin
blockPredicateFilter {
	predicate {
		anyOf {
			matchingBlocks(Blocks.STONE)
			matchingBlocks(Blocks.DEEPSLATE)
		}
	}
}
```

The builders are scoped: they only resolve inside a block that accepts a block predicate, so they never show up in the global completion
list. The same field also accepts a value directly, which is handy to reuse a predicate across several features:

```kotlin
blockPredicateFilter {
	predicate = allOf {
		solid()
		insideWorldBounds()
	}
}
```

## Offsets

Every predicate testing a block accepts an offset, applied to the position being placed. Each component has to be between `-16` and `16`:

```kotlin
predicate {
	// Tests the block one below the position.
	solid { offset(0, -1, 0) }
}
```

`all_of`, `any_of`, `not`, `true` and `matching_biomes` have no offset - they either combine other predicates or test the position itself.

## Predicate Types

| Builder                          | Passes when                                                       |
|----------------------------------|-------------------------------------------------------------------|
| `allOf { }`                      | Every child predicate passes                                      |
| `alwaysTrue()`                   | Always                                                            |
| `anyOf { }`                      | At least one child predicate passes                               |
| `hasSturdyFace(direction)`       | The block has a full supporting surface on that face              |
| `insideWorldBounds()`            | The position is inside the height limits of the world             |
| `matchingBiomes(biomes)`         | The biome is one of the given biomes or in the given biome tag    |
| `matchingBlockTag(tag)`          | The block is in the given block tag                               |
| `matchingBlocks(blocks)`         | The block is one of the given blocks or in the given block tag    |
| `matchingFluids(fluids)`         | The fluid is one of the given fluids or in the given fluid tag    |
| `not { }`                        | The child predicate does not pass                                 |
| `replaceable()`                  | The block can be replaced by a placed block (air, water, grass)   |
| `solid()`                        | The block is solid                                                |
| `unobstructed()`                 | No entity occupies the space of the block                         |
| `wouldSurvive(state)`            | The given block state is a valid placement there                  |

### Blocks, Fluids and Biomes

`matchingBlocks`, `matchingFluids` and `matchingBiomes` accept several entries or a tag, and serialize a lone entry as a bare string:

```kotlin
predicate {
	matchingBlocks(Blocks.STONE, Blocks.DEEPSLATE)   // "blocks": ["minecraft:stone", "minecraft:deepslate"]
}

predicate {
	matchingBlocks(Tags.Block.LOGS)                  // "blocks": "#minecraft:logs"
}

predicate {
	matchingBiomes(Tags.Worldgen.Biome.IS_SAVANNA)   // "biomes": "#minecraft:is_savanna"
}
```

`matchingBlockTag` is the separate `matching_block_tag` type, which writes the tag without its `#` prefix.

### Would Survive

`wouldSurvive` tests a block state as if a player had placed it, which is the reliable way to check that a plant has valid ground:

```kotlin
blockPredicateFilter {
	predicate { wouldSurvive(blockState(Blocks.OAK_SAPLING)) }
}
```

## Where Predicates Are Used

| Field                                                                             | Purpose                                        |
|-----------------------------------------------------------------------------------|------------------------------------------------|
| `blockPredicateFilter { predicate { } }`                                          | Discards the placement positions failing it    |
| `environmentScan { targetCondition { } }`                                         | Stops the scan on the first block passing it   |
| `environmentScan { allowedSearchCondition { } }`                                  | Restricts the blocks the scan may walk through |
| `blockBlob / hugeBrownMushroom / hugeRedMushroom / spike { canPlaceOn { } }`      | Ground the feature may grow on                 |
| `blockColumn { allowedPlacement { } }`                                            | Blocks the column may be placed in             |
| `disk { target { } }`                                                             | Blocks the disk replaces                       |
| `hugeFungus { replaceableBlocks { } }`                                            | Blocks the fungus may grow through             |
| `lake { canPlaceFeature / canReplaceWithAirOrFluid / canReplaceWithBarrier { } }` | Lake carving rules                             |
| `rootSystem { allowedTreePosition { } }`                                          | Positions the tree may be placed at            |
| `spike { canReplace { } }`                                                        | Blocks the spike may grow through              |
| `ruleBasedStateProvider { rule(provider) { } }`                                   | Condition of a block state provider rule       |

## See Also

- [Features](/docs/data-driven/worldgen/features) - the placement modifiers and providers taking predicates
- [Structures](/docs/data-driven/worldgen/structures) - the separate rule tests used by processors
- [World Generation](/docs/data-driven/worldgen) - overview of the worldgen system
