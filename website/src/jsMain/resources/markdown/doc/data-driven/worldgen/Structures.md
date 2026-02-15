---
root: .components.layouts.MarkdownLayout
title: Structures
nav-title: Structures
description: Create structures with template pools, processors, and structure sets using Kore's DSL.
keywords: minecraft, datapack, kore, worldgen, structure, template pool, processor, jigsaw
date-created: 2026-02-03
date-modified: 2026-02-15
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

```kotlin
val processors = dp.processorList("my_processors") {
	processors = listOf(
		// Add processor entries here
	)
}
```

### Common Processors

| Processor          | Description                                 |
|--------------------|---------------------------------------------|
| `block_rot`        | Randomly rotates blocks                     |
| `block_ignore`     | Ignores certain blocks during placement     |
| `block_age`        | Ages blocks (cracks, moss)                  |
| `gravity`          | Adjusts Y position to terrain               |
| `rule`             | Replaces blocks based on rules              |
| `protected_blocks` | Prevents certain blocks from being replaced |
| `capped`           | Limits processor applications               |

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
		processors = listOf(
			// Add aging, gravity, etc.
		)
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
