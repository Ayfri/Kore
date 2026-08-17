---
root: .components.layouts.MarkdownLayout
title: Structures
nav-title: Structures
description: Create structures with template pools, processors, and structure sets using Kore's DSL.
keywords: minecraft, datapack, kore, worldgen, structure, template pool, processor, jigsaw
date-created: 2026-02-03
date-modified: 2026-08-17
routeOverride: /docs/data-driven/worldgen/structures
---

# Structures

Structures are large, complex generated features like villages, temples, strongholds, and dungeons. Unlike simple features (trees, ores),
structures can span multiple chunks and consist of interconnected pieces assembled using the **jigsaw system**.

## Structure Generation Pipeline

Structure generation involves four interconnected components:

1. **Processor list** - Modifies blocks when placing structure pieces (aging, randomization, gravity adjustment)
2. **Template pool** - Defines weighted collections of structure pieces that can connect via jigsaw blocks
3. **Configured structure** - Specifies the structure type, starting pool, biome restrictions, and terrain adaptation
4. **Structure set** - Controls world-scale placement: spacing between structures, clustering, and exclusion zones

Structures generate during the `structures_starts` step, before terrain features. This allows terrain to adapt around structures rather than
structures cutting through terrain.

References: [Structure](https://minecraft.wiki/w/Structure), [Structure definition](https://minecraft.wiki/w/Structure_definition), [Structure set](https://minecraft.wiki/w/Structure_set), [Template pool](https://minecraft.wiki/w/Template_pool), [Processor list](https://minecraft.wiki/w/Processor_list)

---

## Processor List

Processor lists transform blocks when structure pieces are placed. They enable effects like aging (cracked bricks, mossy stone),
randomization (varied block types), and terrain adaptation (gravity for surface structures).

Reference: [Processor list](https://minecraft.wiki/w/Processor_list)

Processors run in declaration order, each one taking the output of the previous one as its input. `processorList`
returns a `ProcessorListArgument` you pass to template pool elements.

```kotlin
import io.github.ayfri.kore.features.worldgen.processorlist.processorList
import io.github.ayfri.kore.features.worldgen.processorlist.types.*

val processors = dp.processorList("my_processors") {
	blockIgnore(Blocks.STRUCTURE_BLOCK, Blocks.JIGSAW)
	blockAge(0.5)
	gravity(HeightMap.WORLD_SURFACE_WG)
}
```

```json
{
	"processors": [
		{
			"processor_type": "minecraft:block_ignore",
			"blocks": [
				{ "Name": "minecraft:structure_block" },
				{ "Name": "minecraft:jigsaw" }
			]
		},
		{
			"processor_type": "minecraft:block_age",
			"mossiness": 0.5
		},
		{
			"processor_type": "minecraft:gravity",
			"heightmap": "WORLD_SURFACE_WG",
			"offset": 0
		}
	]
}
```

### Processors

Every processor is a function scoped to the `processorList { }` block.

| Processor                             | Description                                                            |
|---------------------------------------|------------------------------------------------------------------------|
| `blackstoneReplace()`                 | Replaces blocks with their blackstone counterparts, like bastions      |
| `blockAge(mossiness)`                 | Ages blocks: moss, cracks, and randomly missing blocks                 |
| `blockIgnore(vararg blocks)`          | Skips those block states, keeping whatever the world already has       |
| `blockRot(integrity, rottableBlocks)` | Randomly removes blocks, `integrity` is the chance to keep one         |
| `capped(limit) { }`                   | Runs a single delegate processor on at most `limit` blocks             |
| `gravity(heightmap, offset)`          | Drops the blocks onto a heightmap so the structure follows the terrain |
| `jigsawReplacement()`                 | Turns leftover jigsaw blocks into their final state block              |
| `lavaSubmergedBlock()`                | Fills the positions around blocks placed inside a lava lake with lava  |
| `nop()`                               | Does nothing, useful as a placeholder delegate                         |
| `protectedBlocks(vararg blocks)`      | Keeps those world blocks untouched, the template never replaces them   |
| `rules { }`                           | Replaces blocks using a list of rules, the first matching rule wins    |

`limit` accepts a plain `Int` or any int provider, and the delegate cannot be another `capped` processor:

```kotlin
processorList("capped_aging") {
	capped(uniform(1, 4)) {
		blockAge(0.8)
	}
}
```

### Rules

A rule replaces a template block by `outputState` when its three predicates pass, and the first matching rule wins.

| Property              | Description                                               | Default      |
|-----------------------|-----------------------------------------------------------|--------------|
| `positionPredicate`   | Test on the position inside the structure piece           | `null`       |
| `inputPredicate`      | Test on the block of the structure template               | `AlwaysTrue` |
| `locationPredicate`   | Test on the block already in the world at that position   | `AlwaysTrue` |
| `outputState`         | Block state placed when every predicate passes            | stone        |
| `blockEntityModifier` | What happens to the block entity data of the placed block | `null`       |

```kotlin
processorList("mossify") {
	rules {
		rule {
			inputPredicate = blockMatch(Blocks.STONE_BRICKS)
			locationPredicate = tagMatch(Tags.Block.DIRT)
			outputState = blockState(Blocks.MOSSY_STONE_BRICKS)
		}

		rule {
			positionPredicate = axisAlignedLinearPos(Axis.Y) {
				minDist = 0
				maxDist = 4
				minChance = 1.0
				maxChance = 0.0
			}

			inputPredicate = randomBlockStateMatch(blockState(Blocks.STONE), 0.5)
			outputState = blockState(Blocks.CRACKED_STONE_BRICKS)
			blockEntityModifier = appendLoot(LootTables.Chests.SIMPLE_DUNGEON)
		}
	}
}
```

Position predicates, scoped to the `rule { }` block:

| Position predicate               | Description                                                      |
|----------------------------------|------------------------------------------------------------------|
| `alwaysTruePos()`                | Passes everywhere, same as leaving `positionPredicate` to `null` |
| `linearPos(...)`                 | Chance interpolated with the distance from the piece origin      |
| `axisAlignedLinearPos(axis) { }` | Same, with the distance measured along one axis only             |

Block entity modifiers, also scoped to the `rule { }` block, decide what happens to the block entity data of the
placed block:

| Block entity modifier   | Description                                                         |
|-------------------------|---------------------------------------------------------------------|
| `appendLoot(lootTable)` | Fills the placed container from a loot table                        |
| `appendStatic { }`      | Merges NBT data into the block entity                               |
| `clear()`               | Drops the block entity data of the template                         |
| `passthrough()`         | Keeps it untouched, same as leaving `blockEntityModifier` to `null` |

Rule tests, used by `inputPredicate` and `locationPredicate`, are the shared worldgen ones from
`io.github.ayfri.kore.features.worldgen.ruletest`:

| Rule test                                   | Description                                |
|---------------------------------------------|--------------------------------------------|
| `AlwaysTrue`                                | Matches any block                          |
| `blockMatch(block)`                         | Matches one block, ignoring its properties |
| `blockStateMatch(blockState)`               | Matches one exact block state              |
| `randomBlockMatch(block, probability)`      | Matches a block with a probability         |
| `randomBlockStateMatch(state, probability)` | Matches a block state with a probability   |
| `tagMatch(tag)`                             | Matches any block of a block tag           |

Group processor lists under a tag with `processorListTag`, see [Tags](/docs/data-driven/tags).

---

## Template Pool

Template pools define weighted collections of structure pieces for jigsaw structures. The jigsaw system connects pieces by matching jigsaw
block names, allowing modular structure assembly. Each pool can reference other pools for recursive generation (e.g., village houses
connecting to streets connecting to more houses).

Reference: [Template pool](https://minecraft.wiki/w/Template_pool)

```kotlin
val pool = dp.templatePool("my_pool") {
	fallback = TemplatePools.Empty
	elements {
		// Add weighted template pool entries
	}
}
```

### Pool Elements

```kotlin
elements {
	// Single piece with weight
	singlePoolElement(
		location = "my_namespace:structures/house",
		projection = Projection.RIGID,
		processors = myProcessors,
		weight = 1
	)

	// Empty element (for spacing)
	emptyPoolElement(weight = 1)

	// Feature element
	featurePoolElement(
		feature = myPlacedFeature,
		projection = Projection.TERRAIN_MATCHING,
		weight = 1
	)

	// List of elements (all placed together)
	listPoolElement(
		elements = listOf(/* ... */),
		projection = Projection.RIGID,
		weight = 1
	)
}
```

### Projection Types

| Type               | Description              |
|--------------------|--------------------------|
| `RIGID`            | Maintains original shape |
| `TERRAIN_MATCHING` | Adapts to terrain height |

---

## Configured Structure

Configured structures define the structure type, starting template pool, biome restrictions, generation step, and terrain adaptation
settings. The structure type determines the generation algorithm (jigsaw assembly, single piece, or specialized logic).

Reference: [Structure definition](https://minecraft.wiki/w/Structure_definition)

```kotlin
dp.structures {
	// Use the StructuresBuilder DSL
}
```

### Structure Types

Common structure types include:

- `jigsaw` - Modular structures using template pools
- `buried_treasure` - Single buried chest
- `desert_pyramid` - Desert temple
- `end_city` - End city
- `fortress` - Nether fortress
- `igloo` - Igloo with optional basement
- `jungle_temple` - Jungle temple
- `mineshaft` - Underground mineshaft
- `monument` - Ocean monument
- `nether_fossil` - Nether fossil
- `ocean_ruin` - Ocean ruins
- `ruined_portal` - Ruined portal
- `shipwreck` - Shipwreck
- `stronghold` - Stronghold
- `swamp_hut` - Witch hut
- `woodland_mansion` - Woodland mansion

### Terrain Adaptation

| Value         | Description                                                   |
|---------------|---------------------------------------------------------------|
| `NONE`        | No adaptation                                                 |
| `BEARD_THIN`  | Generates terrain under the structure, removes terrain inside |
| `BEARD_BOX`   | Advanced alternative of beard_thin                            |
| `BURY`        | Generates terrain surrounding the structure to make it buried |
| `ENCAPSULATE` | Advanced alternative of bury (used by Trial Chambers)         |

### Pool Aliases

Pool aliases rewire jigsaw pool connections by redirecting pool references on individual structure instances.

```kotlin
poolAliases {
	// Direct: rewire alias to a specific target
	directPoolAlias(TemplatePools.Empty, TemplatePools.Empty)

	// Random: rewire alias to a randomly selected weighted target
	randomPoolAlias(TemplatePools.Empty) {
		weightedPoolEntry(1, myPool)
		weightedPoolEntry(2, otherPool)
	}

	// Random group: select a weighted group of pool aliases
	randomGroupPoolAlias {
		weightedGroupEntry(1) {
			directPoolAlias(TemplatePools.Empty, myPool)
			randomPoolAlias(TemplatePools.Empty) {
				weightedPoolEntry(1, otherPool)
			}
		}
	}
}
```

---

## Structure Set

Structure sets control world-scale placement using a grid-based system. **Spacing** defines the grid cell size (average distance), while *
*separation** ensures minimum distance between structures. Multiple structures can share a set with weights for mutual exclusion (only one
generates per cell).

Reference: [Structure set](https://minecraft.wiki/w/Structure_set)

```kotlin
val structSet = dp.structureSet("my_structures") {
	structure(myConfiguredStructure, weight = 1)

	// Placement type
	randomSpreadPlacement(spacing = 32, separation = 8) {
		// Optional: salt, spreadType, etc.
	}
}
```

### Placement Types

#### Random Spread

The most common placement type. Divides the world into a grid where each cell may contain one structure at a random position. The `salt`
value ensures different structure sets don't align their grids.

```kotlin
randomSpreadPlacement(
	spacing = 32,      // Average distance between structures
	separation = 8     // Minimum distance between structures
) {
	salt = 12345       // Seed modifier for randomization
	spreadType = SpreadType.LINEAR // or TRIANGULAR
}
```

#### Concentric Rings

Places structures in expanding rings around the world origin. Used by strongholds to ensure they're distributed at increasing distances from
spawn.

Reference: [Structure set - Concentric rings](https://minecraft.wiki/w/Structure_set#concentric_rings)

```kotlin
concentricRingsPlacement(
	distance = 32,
	spread = 3,
	count = 128
)
```

---

## Complete Example

```kotlin
fun DataPack.createCustomVillage() {
	// 1) Processor list for aging blocks
	val villageProcessors = processorList("village_processors") {
		blockAge(0.3)
		gravity(HeightMap.WORLD_SURFACE_WG)
	}

	// 2) Template pool for houses
	val housesPool = templatePool("village/houses") {
		fallback = TemplatePools.Empty
		elements {
			singlePoolElement(
				location = "my_pack:village/house_small",
				projection = Projection.RIGID,
				processors = villageProcessors,
				weight = 3
			)
			singlePoolElement(
				location = "my_pack:village/house_large",
				projection = Projection.RIGID,
				processors = villageProcessors,
				weight = 1
			)
		}
	}

	// 3) Start pool (village center)
	val startPool = templatePool("village/start") {
		fallback = TemplatePools.Empty
		elements {
			singlePoolElement(
				location = "my_pack:village/center",
				projection = Projection.RIGID,
				processors = villageProcessors,
				weight = 1
			)
		}
	}

	// 4) Configured structure (via structures builder)
	structures {
		// Define jigsaw structure referencing startPool
	}

	// 5) Structure set for placement
	structureSet("custom_villages") {
		// structure(customVillage, weight = 1)
		randomSpreadPlacement(spacing = 34, separation = 8) {
			salt = 10387312
		}
	}
}
```
