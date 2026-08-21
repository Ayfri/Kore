---
root: .components.layouts.MarkdownLayout
title: Structures
nav-title: Structures
description: Build Minecraft structures with Kore - configured structures, jigsaw template pools, block processors and structure set placement.
keywords: minecraft, datapack, kore, worldgen, structure, template pool, processor list, jigsaw, structure set
date-created: 2026-02-03
date-modified: 2026-08-21
routeOverride: /docs/data-driven/worldgen/structures
---

# Structures

Structures are the large, hand-authored pieces of the world: villages, temples, strongholds, trial chambers. Unlike a feature, a structure
can span many chunks and be assembled from separate building blocks, and it starts generating during `structures_starts`, before the terrain
itself, which is why terrain can adapt around it instead of cutting through it.

Four file types work together, from the smallest to the largest scale:

| File                     | Answers                                                             | Kore API              |
|--------------------------|---------------------------------------------------------------------|-----------------------|
| **Processor list**       | How are the blocks of a piece changed as it is placed?              | `processorList(...)`  |
| **Template pool**        | Which pieces may connect here, and with what weights?               | `templatePool(...)`   |
| **Configured structure** | What kind of structure is this, in which biomes, at which step?     | `structures { }`      |
| **Structure set**        | How far apart do instances of it stand across the world?            | `structureSet(...)`   |

A structure made of a single fixed shape only needs the last two; a jigsaw structure like a village uses all four.

References: [Structure](https://minecraft.wiki/w/Structure), [Structure definition](https://minecraft.wiki/w/Structure_definition)

---

## Processor List

Processors transform the blocks of a structure template as it is stamped into the world: aging bricks, randomizing block types, dropping the
structure onto the terrain. They run in declaration order, each one taking the output of the previous.

```kotlin
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
		{ "processor_type": "minecraft:block_age", "mossiness": 0.5 },
		{ "processor_type": "minecraft:gravity", "heightmap": "WORLD_SURFACE_WG", "offset": 0 }
	]
}
```

Every processor is a function scoped to the `processorList { }` block.

| Processor                             | Effect                                                                 |
|---------------------------------------|------------------------------------------------------------------------|
| `blackstoneReplace()`                 | Replaces blocks with their blackstone counterparts, like bastions      |
| `blockAge(mossiness)`                 | Ages blocks: moss, cracks, and randomly missing blocks                 |
| `blockIgnore(vararg blocks)`          | Skips those block states, keeping whatever the world already has       |
| `blockRot(integrity, rottableBlocks)` | Randomly removes blocks, `integrity` being the chance to keep one      |
| `capped(limit) { }`                   | Runs a single delegate processor on at most `limit` blocks             |
| `gravity(heightmap, offset)`          | Drops the blocks onto a heightmap so the structure follows the terrain |
| `jigsawReplacement()`                 | Turns leftover jigsaw blocks into their final state block              |
| `lavaSubmergedBlock()`                | Fills the positions around blocks placed inside a lava lake with lava  |
| `nop()`                               | Does nothing, useful as a placeholder delegate                         |
| `protectedBlocks(vararg blocks)`      | Keeps those world blocks untouched, the template never replaces them   |
| `rules { }`                           | Replaces blocks using a list of rules, the first matching rule winning |

`limit` accepts a plain `Int` or any [int provider](/docs/data-driven/worldgen/providers#int-providers), and the delegate cannot be another
`capped` processor:

```kotlin
processorList("capped_aging") {
	capped(uniform(1, 4)) {
		blockAge(0.8)
	}
}
```

Reference: [Processor list](https://minecraft.wiki/w/Processor_list)

### Rules

A rule replaces a template block by `outputState` when its three predicates pass, and the first matching rule wins.

| Property              | Tests or sets                                             | Default      |
|-----------------------|-----------------------------------------------------------|--------------|
| `positionPredicate`   | The position inside the structure piece                   | `null`       |
| `inputPredicate`      | The block of the structure template                       | `AlwaysTrue` |
| `locationPredicate`   | The block already in the world at that position           | `AlwaysTrue` |
| `outputState`         | The block state placed when every predicate passes        | stone        |
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

**Position predicates**, scoped to `rule { }`:

| Builder                          | Passes                                                           |
|----------------------------------|------------------------------------------------------------------|
| `alwaysTruePos()`                | Everywhere, same as leaving `positionPredicate` to `null`        |
| `linearPos(...)`                 | With a chance interpolated over the distance from the origin     |
| `axisAlignedLinearPos(axis) { }` | Same, with the distance measured along one axis only             |

**Block entity modifiers**, also scoped to `rule { }`:

| Builder                 | Effect                                                              |
|-------------------------|---------------------------------------------------------------------|
| `appendLoot(lootTable)` | Fills the placed container from a loot table                        |
| `appendStatic { }`      | Merges NBT data into the block entity                               |
| `clear()`               | Drops the block entity data of the template                         |
| `passthrough()`         | Keeps it untouched, same as leaving `blockEntityModifier` to `null` |

**Rule tests**, used by `inputPredicate` and `locationPredicate`. These are the shared worldgen ones from
`io.github.ayfri.kore.features.worldgen.ruletest`, also available in the `target { }` block of the
[ore-like features](/docs/data-driven/worldgen/features#ore):

| Builder                                     | Matches                                                     |
|---------------------------------------------|-------------------------------------------------------------|
| `alwaysTrue()`                              | Any block, same as leaving the predicate to its default     |
| `blockMatch(block)`                         | One block, whatever its block state properties are          |
| `blockStateMatch(blockState)`               | One exact block state, every property having to match       |
| `randomBlockMatch(block, probability)`      | A block with a probability, clamped between `0.0` and `1.0` |
| `randomBlockStateMatch(state, probability)` | A block state with a probability                            |
| `tagMatch(tag)`                             | Any block of a block tag                                    |

---

## Template Pool

A template pool is a weighted bag of pieces the jigsaw system may pick from. Pieces connect through jigsaw blocks: each one names the pool
it wants next, so a village street pool can point at a house pool that points back at the street pool, and the structure grows until it hits
its `size` budget.

```kotlin
val pool = dp.templatePool("my_pool") {
	fallback = TemplatePools.Empty

	single(Structures.Village.Plains.TownCenters.PLAINS_FOUNTAIN_01, ProcessorLists.EMPTY, weight = 3)
	feature(PlacedFeatures.PILE_HAY, weight = 1)
	empty(weight = 2)
}
```

`fallback` is the pool used when a piece cannot connect any further - usually `TemplatePools.Empty`, which is also the default.

| Builder                              | `element_type`                         | Places                                                       |
|--------------------------------------|----------------------------------------|--------------------------------------------------------------|
| `single(location, processors)`       | `minecraft:single_pool_element`        | A structure template                                         |
| `legacySingle(location, processors)` | `minecraft:legacy_single_pool_element` | Same, but keeps existing world blocks instead of placing air |
| `feature(feature)`                   | `minecraft:feature_pool_element`       | A placed feature, in a 1x1x1 piece                           |
| `list { }`                           | `minecraft:list_pool_element`          | Several elements at the same position, in order              |
| `empty()`                            | `minecraft:empty_pool_element`         | Nothing, useful as filler weight                             |

Every builder takes `weight` (1 to 150, default `1`) and `projection` (`Projection.RIGID` keeps the original shape, `TERRAIN_MATCHING`
adapts it to the terrain height; default `RIGID`). `processors` defaults to `ProcessorLists.EMPTY`. A trailing block reaches the remaining
fields, such as `overrideLiquidSettings` on `single` and `legacySingle`.

```kotlin
dp.templatePool("houses") {
	single(Structures.Village.Plains.Houses.PLAINS_SMALL_HOUSE_1, weight = 4) {
		projection = Projection.TERRAIN_MATCHING
		overrideLiquidSettings = LiquidSettings.IGNORE_WATERLOGGING
	}

	list(weight = 1) {
		single(Structures.Village.Plains.Houses.PLAINS_SMALL_HOUSE_2, ProcessorLists.MOSSIFY_10_PERCENT)
		feature(PlacedFeatures.PILE_HAY)
	}
}
```

Elements inside a `list` are unweighted: the weight belongs to the list entry itself.

Reference: [Template pool](https://minecraft.wiki/w/Template_pool)

---

## Configured Structure

A configured structure picks the generation algorithm and says where it is allowed to appear. Each type is a function on the
`structures { }` scope, taking the file name first and everything else in a trailing block, so one call writes one file:

```kotlin
dp.structures {
	desertPyramid("my_pyramid") {
		biomes(Biomes.DESERT, Biomes.BADLANDS)
		terrainAdaptation = TerrainAdaptation.BEARD_BOX

		spawnOverrides {
			monster(BoundingBox.FULL) {
				spawner(EntityTypes.HUSK, weight = 1, minCount = 4, maxCount = 4)
			}
		}
	}
}
```

`dp.structuresBuilder.desertPyramid(...)` writes the same file outside a `structures { }` block, and `dp.structure(fileName, type)`
takes an already built type, for the rare case where the generator itself needs tweaking, such as overriding its namespace.

### Shared Fields

| Field               | Type                  | Default                                 | Meaning                                                                         |
|---------------------|-----------------------|-----------------------------------------|---------------------------------------------------------------------------------|
| `biomes`            | Biomes or a biome tag | empty, meaning nowhere                  | Biomes the structure start is allowed to land in.                               |
| `step`              | `GenerationStep`      | the vanilla step of that structure type | Which [decoration step](/docs/data-driven/worldgen#decoration-steps) places it. |
| `spawnOverrides`    | `SpawnOverrides`      | empty, keeping the biome spawns         | Mob spawn lists replacing the biome ones inside the structure.                  |
| `terrainAdaptation` | `TerrainAdaptation`   | none                                    | How the terrain reacts around the structure.                                    |

Every type defaults `step` to the step its vanilla counterpart uses, so it only needs setting when a structure should generate at another
point of the world generation.

`spawnOverrides` takes one block per mob category (`ambient`, `axolotls`, `creature`, `misc`, `monster`, `undergroundWaterCreature`,
`waterAmbient`, `waterCreature`), each with a `BoundingBox`: `FULL` applies to the whole structure, `PIECE` to the individual piece and is
the default. A category left out keeps the biome spawns, while a category declared with an empty block stops it from spawning inside the
structure entirely:

```kotlin
spawnOverrides {
	// No monster ever spawns inside the mansion, whatever the biome says.
	monster(BoundingBox.PIECE)

	// Guardians spawn anywhere inside the monument, on top of the biome spawns being dropped.
	waterCreature(BoundingBox.FULL) {
		spawner(EntityTypes.GUARDIAN, weight = 20, minCount = 2, maxCount = 4)
	}
}
```

| `TerrainAdaptation` | Effect                                                            |
|---------------------|-------------------------------------------------------------------|
| `NONE`              | No adaptation, the structure is stamped as-is.                    |
| `BEARD_THIN`        | Grows terrain under the structure and clears the inside.          |
| `BEARD_BOX`         | Same, over the full bounding box.                                 |
| `BURY`              | Grows terrain around the structure so it ends up buried.          |
| `ENCAPSULATE`       | Same over the full bounding box, used by the trial chambers.      |

### Structure Types

| Builder                  | Default `step`           | Extra fields                                                                             |
|--------------------------|--------------------------|------------------------------------------------------------------------------------------|
| `buriedTreasure(...)`    | `underground_structures` | none                                                                                     |
| `desertPyramid(...)`     | `surface_structures`     | none                                                                                     |
| `endCity(...)`           | `surface_structures`     | none                                                                                     |
| `fortress(...)`          | `underground_decoration` | none                                                                                     |
| `igloo(...)`             | `surface_structures`     | none                                                                                     |
| `jigsaw(..., startPool)` | `surface_structures`     | see below                                                                                |
| `jungleTemple(...)`      | `surface_structures`     | none                                                                                     |
| `mineshaft(...)`         | `underground_structures` | `mineshaftType`: `MineshaftType.NORMAL` or `MESA`                                        |
| `netherFossil(...)`      | `underground_decoration` | `height`: a height provider                                                              |
| `oceanMonument(...)`     | `surface_structures`     | none                                                                                     |
| `oceanRuin(...)`         | `surface_structures`     | `biomeTemp`: `BiomeTemperature.COLD` or `WARM`, `largeProbability`, `clusterProbability` |
| `ruinedPortal(...)`      | `surface_structures`     | `setup(...)` entries, see below                                                          |
| `shipwreck(...)`         | `surface_structures`     | `isBeached`                                                                              |
| `stronghold(...)`        | `surface_structures`     | none                                                                                     |
| `swampHut(...)`          | `surface_structures`     | none                                                                                     |
| `woodlandMansion(...)`   | `surface_structures`     | none                                                                                     |

Only `jigsaw` builds its shape from data; every other type runs a hardcoded algorithm and only exposes the knobs listed above.

```kotlin
structures {
	mineshaft("badlands_mineshaft") {
		biomes(Tags.Worldgen.Biome.IS_BADLANDS)
		mineshaftType = MineshaftType.MESA
	}

	netherFossil("deep_fossil") {
		biomes(Biomes.SOUL_SAND_VALLEY)
		height = uniformHeightProvider(aboveBottom(32), belowTop(2))
	}

	ruinedPortal("my_ruined_portal") {
		biomes(Biomes.DESERT)
		setup(RuinedPortalPlacement.PARTLY_BURIED, mossiness = 0.0f, weight = 1f)
		setup(RuinedPortalPlacement.ON_LAND_SURFACE, overgrown = true, vines = true, weight = 0.5f)
	}

	oceanRuin("my_ocean_ruin") {
		biomes(Biomes.WARM_OCEAN)
		biomeTemp = BiomeTemperature.WARM
		largeProbability = 0.4f
		clusterProbability = 0.8f
	}
}
```

A `ruinedPortal` needs at least one `setup`: it is the weighted list one variant is drawn from per instance, and vanilla rejects the
structure when it is empty.

### Jigsaw

```kotlin
structures {
	jigsaw("my_village", startPool = villageStart) {
		biomes(Biomes.PLAINS)
		terrainAdaptation = TerrainAdaptation.BEARD_THIN
		size = 6
		startHeight = constantAbsolute(0)
		projectStartToHeightmap = HeightMap.WORLD_SURFACE_WG
		maxDistanceFromCenter(80)
	}
}
```

| Field                     | Default               | Meaning                                                                                |
|---------------------------|-----------------------|----------------------------------------------------------------------------------------|
| `startPool`               | mandatory             | The template pool the first piece is drawn from.                                       |
| `size`                    | `0`                   | How many times pieces may branch out from the start piece.                             |
| `startHeight`             | `constantAbsolute(0)` | Height provider for the start piece.                                                   |
| `startJigsawName`         | none                  | Only connect through jigsaw blocks carrying this name.                                 |
| `projectStartToHeightmap` | none                  | Snaps the start piece onto a heightmap instead of using `startHeight`.                 |
| `maxDistanceFromCenter`   | `80`                  | Radius in blocks the structure may not grow past, up to `128` horizontally.            |
| `dimensionPadding`        | none                  | Blocks kept free above and below the structure, `dimensionPadding(top, bottom)`.       |
| `liquidSettings`          | none                  | Whether pieces waterlog, `LiquidSettings.APPLY_WATERLOGGING` or `IGNORE_WATERLOGGING`. |
| `useExpansionHack`        | `false`               | The legacy village terrain hack, raising pieces above the ground.                      |
| `poolAliases`             | none                  | Per-instance pool rewiring, see below.                                                 |

`dimensionPadding(value)` and `maxDistanceFromCenter(value)` are functions, taking one value for both directions or two for each. Given one
value, they write a bare number instead of an object.

#### Pool Aliases

Pool aliases rewire which pool an alias resolves to, per structure instance. That is how a single jigsaw structure can produce differently
themed variants without duplicating every pool.

```kotlin
poolAliases {
	// Always resolve this alias to that pool.
	directPoolAlias(TemplatePools.Empty, housesPool)

	// Draw the target from a weighted list, per instance.
	randomPoolAlias(TemplatePools.Empty) {
		weightedPoolEntry(1, desertHouses)
		weightedPoolEntry(2, plainsHouses)
	}

	// Draw a whole group of aliases at once, keeping a variant coherent.
	randomGroupPoolAlias {
		weightedGroupEntry(1) {
			directPoolAlias(TemplatePools.Empty, desertHouses)
			directPoolAlias(TemplatePools.Empty, desertStreets)
		}
	}
}
```

---

## Structure Set

A structure set decides how instances spread over the world. Several structures can share a set, in which case the weights make them
mutually exclusive: only one generates per grid cell.

```kotlin
val villages = dp.structureSet("custom_villages") {
	structure(ConfiguredStructures.VILLAGE_PLAINS, weight = 3)
	structure(ConfiguredStructures.VILLAGE_DESERT, weight = 1)

	randomSpreadPlacement(spacing = 34, separation = 8) {
		salt = 10387312
		spreadType = SpreadType.LINEAR
	}
}
```

Every configured structure builder returns a `ConfiguredStructureArgument`, which is exactly what `structure()` takes, so a set mixes your
own structures and the vanilla ones from the generated `ConfiguredStructures` object:

```kotlin
val myPyramid = dp.structuresBuilder.desertPyramid("my_pyramid") {
	biomes(Biomes.DESERT)
}

dp.structureSet("my_pyramids") {
	structure(myPyramid, weight = 2)
	randomSpreadPlacement(spacing = 32, separation = 8)
}
```

### Placement

Two placement types exist, and a set has exactly one. Without a call to either, it uses an empty random spread.

**`randomSpreadPlacement(spreadType, spacing, separation)`** is the common one. The world is cut into a grid of `spacing` chunks; each cell
gets at most one structure, placed at random inside it, never closer than `separation` chunks to the cell edge. `separation` must stay below
`spacing`, and the closer the two, the more regular the layout. `SpreadType.LINEAR` picks the position uniformly, `TRIANGULAR` biases it
towards the cell center.

**`concentricRingsPlacement(distance, spread, count)`** places structures in rings around the world origin, the way strongholds do:
`distance` chunks to the first ring, `spread` chunks of jitter, `count` structures in total. `preferredBiomes(...)` restricts where within a
ring a structure may land.

Both share these fields, set inside the trailing block:

| Field                      | Meaning                                                                                      |
|----------------------------|----------------------------------------------------------------------------------------------|
| `salt`                     | Seed modifier keeping different sets from aligning their grids. Random unless you set it.    |
| `frequency`                | Fraction of eligible cells actually generating a structure, from `0.0` to `1.0`.             |
| `frequencyReductionMethod` | How `frequency` is applied: `DEFAULT`, or one of the three `LEGACY_TYPE_*` vanilla variants. |
| `exclusionZone(set, n)`    | Skips a placement within `n` chunks of a structure of another set.                           |
| `locateOffset`             | Offset applied to what `/locate` reports, as `listOf(x, y, z)`.                              |

```kotlin
dp.structureSet("my_strongholds") {
	structure(ConfiguredStructures.STRONGHOLD)

	concentricRingsPlacement(distance = 32, spread = 3, count = 128) {
		preferredBiomes(Biomes.PLAINS, Biomes.FOREST)
		exclusionZone(villages, chunkCount = 5)
	}
}
```

Reference: [Structure set](https://minecraft.wiki/w/Structure_set)

## See Also

- [Biomes](/docs/data-driven/worldgen/biomes) - the biomes a structure restricts itself to
- [Features](/docs/data-driven/worldgen/features) - the smaller decorations placed alongside structures
- [Providers](/docs/data-driven/worldgen/providers) - height and int providers used by jigsaw and processors
- [Tags](/docs/data-driven/tags) - grouping processor lists, template pools and structures
- [World Generation](/docs/data-driven/worldgen) - overview of the worldgen system
