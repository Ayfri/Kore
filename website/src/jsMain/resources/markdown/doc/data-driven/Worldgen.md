---
root: .components.layouts.MarkdownLayout
title: World generation
nav-title: Worldgen
description: A complete guide to custom world generation (1.21+) with Kore’s Kotlin DSL.
keywords: minecraft, datapack, kore, worldgen, dimension, biome, features, structures, noise
date-created: 2025-08-11
date-modified: 2025-08-11
routeOverride: /docs/data-driven/worldgen
---

# World generation

This guide shows how to build custom world generation using Kore’s Kotlin DSL. It maps the common datapack JSON files described on the Minecraft Wiki to concise Kotlin builders, and links to authoritative references for each concept.

- **What worldgen is
  **: terrain, biomes, carvers, features, and structures placed procedurally per chunk. See [World generation](https://minecraft.wiki/w/World_generation).
- Cross‑references: see [Predicates](/docs/data-driven/predicates) for condition logic in other features, and [Test Features](/docs/advanced/test-features) to automate validation with GameTest.

## Biomes

Biomes define climate, visuals, mob spawns, carvers, and the placed features list for each decoration step.

- Reference: [Biome definition](https://minecraft.wiki/w/Biome_definition)

```kotlin
val myBiome = dp.biome("my_biome") {
	temperature = 0.8f
	downfall = 0.4f
	hasPrecipitation = true

	effects {
		skyColor = 0x78A7FF
		fogColor = 0xC0D8FF
		waterColor = 0x3F76E4
		waterFogColor = 0x050533
	}

	spawners { /* add spawn rules */ }
	spawnCosts { /* per-entity spawn costs */ }
	carvers { /* add ConfiguredCarvers here */ }
	features { /* populate steps with PlacedFeatures */ }
}
```

## Carvers (caves/canyons)

Configured carvers remove terrain to form cave systems and canyons.

- Reference: [Carver definition](https://minecraft.wiki/w/Carver_definition)

```kotlin
val caves = dp.configuredCarver("my_caves", config = /* ConfiguredCarver.Config */) {}
```

## Decoration Steps

Minecraft runs 11 decoration steps in order for each chunk; structures of a step place before features in that step.

1. `raw_generation` - small end islands
2. `lakes` - lava lakes
3. `local_modifications` - geodes, icebergs
4. `underground_structures` - trial chambers, mineshafts, etc.
5. `surface_structures` - other structures, desert wells, blue ice patches
6. `strongholds` - unused (strongholds use surface_structures)
7. `underground_ores` - ore blobs, sand/gravel/clay disks
8. `underground_decoration` - infested blobs, nether gravel/blackstone, nether ores
9. `fluid_springs` - water/lava springs
10. `vegetal_decoration` - trees, cacti, kelp, vegetation
11. `top_layer_modification` - freeze top layer

Reference: [Decoration steps](https://minecraft.wiki/w/World_Generation#Decoration_steps)

## Density Functions and Noises

Low‑level noise graph pieces used by the noise router and terrain shaping.

- References: [Density function](https://minecraft.wiki/w/Density_function), [Noise router](https://minecraft.wiki/w/Noise_router)

```kotlin
val df = dp.densityFunction("my_df", type = /* DensityFunctionType */) {}
val n = dp.noise("my_noise_def") { firstOctave = -7; amplitudes = listOf(1.0, 1.0, 0.5) }
```

## Dimension

Represents one world. Binds a `dimension_type` to a generator (noise/flat/debug) and optional biome source.

- References: [Dimension definition](https://minecraft.wiki/w/Dimension_definition), [Custom dimension](https://minecraft.wiki/w/Custom_dimension)

```kotlin
val dim = dp.dimension("my_dimension", type = myDimType) {
	// Choose one generator:
	// debugGenerator()
	// noiseGenerator(settings = myNoise, biomeSource = /* your BiomeSource */)
	// flatGenerator(biome = /* a BiomeArgument */) { /* layers, structures */ }
}
```

## Dimension Type

Controls fundamental world rules (skylight, ceilings, respawn rules, height, infiniburn, etc.).

- Reference: [Dimension type](https://minecraft.wiki/w/Dimension_type)

```kotlin
val myDimType = dp.dimensionType("my_dim_type") {
	minY = -64
	height = 384
	hasSkylight = true
	hasCeiling = false
	ambientLight = 0f
}
```

## Features (Configured + Placed)

- Configured feature: what to place (tree, ore, etc.) with parameters.
- Placed feature: where/how often to place (count, height, rarity, biome filters).

References: [Configured feature](https://minecraft.wiki/w/Configured_feature), [Placed feature](https://minecraft.wiki/w/Placed_feature)

```kotlin
val treeCfg = /* build a FeatureConfig (e.g., tree/ore) */
val myTree = dp.configuredFeature("my_tree", featureConfig = treeCfg) {}

val myTreePlaced = dp.placedFeature("my_tree_placed", myTree) {
	// placementModifiers = listOf(...)
}

// Attach placed features to a biome by step inside dp.biome { features { ... } }
```

## Flat Level Generator Presets

Expose superflat presets in the world creation UI.

- Reference: Superflat preset section in [World preset definition](https://minecraft.wiki/w/World_preset_definition#Superflat_Level_Generation_Preset)

```kotlin
val flatPreset = dp.flatLevelGeneratorPreset("classic_like") {
	// display item, layers, biome, structure overrides are available in the builder
}
```

## Noise Settings (terrain)

Defines vertical range, aquifers/veins toggles, default blocks/fluids, noise router graph, spawn targets, and surface rules.

- Reference: [Noise settings](https://minecraft.wiki/w/Noise_settings)

```kotlin
val myNoise = dp.noiseSettings("my_noise") {
	noiseOptions(minY = -64, height = 384, sizeHorizontal = 1, sizeVertical = 2)
	defaultBlock(Blocks.STONE) {}
	defaultFluid(Blocks.WATER) { this["level"] = "0" }
	// noiseRouter { ... }
	// surfaceRule = Bandlands
}
```

## Output Paths (generated by Kore)

Kore APIs generate JSON under standard datapack directories (replace `<ns>` with your namespace):

- `DataPack.biome("...")` → `data/<ns>/worldgen/biome/<fileName>.json`
- `DataPack.configuredCarver("...")` → `data/<ns>/worldgen/configured_carver/<fileName>.json`
- `DataPack.configuredFeature("...")` → `data/<ns>/worldgen/configured_feature/<fileName>.json`
- `DataPack.densityFunction("...")` → `data/<ns>/worldgen/density_function/<fileName>.json`
- `DataPack.dimension("...")` → `data/<ns>/dimension/<fileName>.json`
- `DataPack.dimensionType("...")` → `data/<ns>/dimension_type/<fileName>.json`
- `DataPack.flatLevelGeneratorPreset("...")` → `data/<ns>/worldgen/flat_level_generator_preset/<fileName>.json`
- `DataPack.noise("...")` → `data/<ns>/worldgen/noise/<fileName>.json`
- `DataPack.noiseSettings("...")` → `data/<ns>/worldgen/noise_settings/<fileName>.json`
- `DataPack.processorList("...")` → `data/<ns>/worldgen/processor_list/<fileName>.json`
- `DataPack.structureSet("...")` → `data/<ns>/worldgen/structure_set/<fileName>.json`
- `DataPack.structures { ... }` → `data/<ns>/worldgen/structure/<fileName>.json` (via the builder)
- `DataPack.templatePool("...")` → `data/<ns>/worldgen/template_pool/<fileName>.json`
- `DataPack.worldPreset("...")` → `data/<ns>/worldgen/world_preset/<fileName>.json`

## Structures (processors, pools, configured, global placement)

- Processor list: per‑piece processing (rules, integrity, gravity, etc.) - [Processor list](https://minecraft.wiki/w/Processor_list)
- Template pool: weighted jigsaw pieces and fallback - [Template pool](https://minecraft.wiki/w/Template_pool)
- Configured structure: structure type config (biomes, step,
  adaptations) - [Structure definition](https://minecraft.wiki/w/Structure_definition)
- Structure set: world‑scale placement and spacing - [Structure set](https://minecraft.wiki/w/Structure_set)

```kotlin
val pool = dp.templatePool("my_pool") {
	fallback = TemplatePools.Empty
	elements { /* add TemplatePoolEntry items */ }
}

val processors = dp.processorList("my_processors") {
	processors = listOf(/* ProcessorType entries */)
}

dp.structures {
	// use the StructuresBuilder DSL to create configured structures that reference pools/processors
}

val structSet = dp.structureSet("my_structures") {
	structure(/* configuredStructureArg */, weight = 1)
	randomSpreadPlacement(spacing = 32, separation = 8) {}
}
```

## World Preset (wire everything & expose in UI)

Defines the dimension set for a selectable world creation preset.

- Reference: [World preset definition](https://minecraft.wiki/w/World_preset_definition)

```kotlin
dp.worldPreset("my_preset") {
	dimension(DimensionTypes.OVERWORLD) {
		type = myDimType
		// select a generator as shown above
	}
	// Optionally declare NETHER/END or additional custom dimensions
}
```

---

## End‑to‑end example: simple custom Overworld‑like dimension

```kotlin
val dp = DataPack(/* ... */)

// 1) Dimension type
val dimType = dp.dimensionType("example_type") {
	minY = -64
	height = 384
	hasSkylight = true
}

// 2) Noise settings (terrain)
val terrain = dp.noiseSettings("example_noise") {
	noiseOptions(-64, 384, 1, 2)
}

// 3) Biome with one placed feature
val cfg = /* build a FeatureConfig (e.g., simple tree) */
val tree = dp.configuredFeature("example_tree", cfg) {}
val treePlaced = dp.placedFeature("example_tree_placed", tree) { /* placementModifiers = ... */ }

val plains = dp.biome("example_plains") {
	temperature = 0.8f
	features {
		// add your placed features to the appropriate steps
	}
}

// 4) Dimension using noise generator
val dim = dp.dimension("example_dimension", type = dimType) {
	// Provide a biome source compatible with your terrain settings
	// noiseGenerator(settings = terrain, biomeSource = ...)
}

// 5) World preset to expose it in UI
dp.worldPreset("example_preset") {
	dimension(DimensionTypes.OVERWORLD) {
		type = dimType
		// choose generator (e.g., noiseGenerator as above)
	}
}
```

---

## Tips, validation, and testing

- Validate your concepts against the Wiki JSON schemas and pages:
	- Dimension: [Dimension definition](https://minecraft.wiki/w/Dimension_definition), [Custom dimension](https://minecraft.wiki/w/Custom_dimension)
	- Dimension type: [Dimension type](https://minecraft.wiki/w/Dimension_type)
	- Noise settings: [Noise settings](https://minecraft.wiki/w/Noise_settings), [World generation](https://minecraft.wiki/w/World_generation)
	- Biome: [Biome definition](https://minecraft.wiki/w/Biome_definition)
	- Features: [Configured feature](https://minecraft.wiki/w/Configured_feature), [Placed feature](https://minecraft.wiki/w/Placed_feature)
	- Carvers: [Carver definition](https://minecraft.wiki/w/Carver_definition)
	- Structures: [Template pool](https://minecraft.wiki/w/Template_pool), [Processor list](https://minecraft.wiki/w/Processor_list), [Structure definition](https://minecraft.wiki/w/Structure_definition), [Structure set](https://minecraft.wiki/w/Structure_set)
	- World preset: [World preset definition](https://minecraft.wiki/w/World_preset_definition)

- Test quickly in‑game:
	- Teleport into a dimension: `/execute in <ns>:<dimension> run tp @s 0 200 0`
	- Locate a structure: `/locate structure <ns>:<structure_name>`
	- Reload datapacks: `/reload`

- Keep placed features and structure placement reasonable to avoid performance issues.

If you are new to datapacks, read the high‑level overview first: [World generation](https://minecraft.wiki/w/World_generation). It explains the order of operations (noise → surface → decorations → lighting/spawning) and how the pieces fit together.

---

## Complete example: Aether‑like dimension

```kotlin
import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.DimensionTypes
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.arguments.worldgen.types.BiomeArgument
import io.github.ayfri.kore.features.worldgen.biome.types.spawnCost
import io.github.ayfri.kore.features.worldgen.biome.types.spawnCosts
import io.github.ayfri.kore.features.worldgen.biome.types.spawner
import io.github.ayfri.kore.features.worldgen.biome.types.spawners
import io.github.ayfri.kore.features.worldgen.configuredcarver.Cave
import io.github.ayfri.kore.features.worldgen.configuredcarver.caveConfig
import io.github.ayfri.kore.features.worldgen.configuredfeature.Target
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.ore
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.simpleBlock
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer.blobFoliagePlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer.straightTrunkPlacer
import io.github.ayfri.kore.features.worldgen.dimension.biomesource.checkerboard
import io.github.ayfri.kore.features.worldgen.heightproviders.uniformHeightProvider
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.absolute
import io.github.ayfri.kore.features.worldgen.placedfeature.modifiers.biome
import io.github.ayfri.kore.features.worldgen.placedfeature.modifiers.count
import io.github.ayfri.kore.features.worldgen.placedfeature.modifiers.heightRange
import io.github.ayfri.kore.features.worldgen.placedfeature.modifiers.inSquare
import io.github.ayfri.kore.features.worldgen.placedfeature.modifiers.rarityFilter
import io.github.ayfri.kore.features.worldgen.floatproviders.constant
import io.github.ayfri.kore.features.worldgen.intproviders.constant

fun DataPack.createAetherDimension() {
	// 1) Dimension type: core physical rules for the custom world (skylight, height, respawn, etc.)
	val aetherType = dimensionType("aether_type") {
		ambientLight = 0.15f
		bedWorks = true
		hasCeiling = false
		hasRaids = true
		hasSkylight = true
		height = 384
		logicalHeight = 384
		minY = -64
		natural = true
		piglinSafe = false
		respawnAnchorWorks = false
		ultrawarm = false
	}

	// 2) Noise settings: define vertical range, default block/fluid and noise options for terrain
	val aetherNoise = noiseSettings("aether_noise") {
		defaultBlock(Blocks.STONE) {}
		defaultFluid(Blocks.WATER) { this["level"] = "0" }
		noiseOptions(minY = -64, height = 384, sizeHorizontal = 1, sizeVertical = 2)
	}

	// 3) Configured feature: define what the Highlands tree looks like (dirt/trunk/leaves)
	val highlandsTreeCfg = tree {
		// ground block used as the sapling base when generating the tree
		dirtProvider = simpleStateProvider(Blocks.DIRT)
		// trunk block state
		trunkProvider = simpleStateProvider(Blocks.OAK_LOG)
		// leaf block state
		foliageProvider = simpleStateProvider(Blocks.OAK_LEAVES)
		// trunk placer: baseHeight + two random components -> controls trunk height variability
		straightTrunkPlacer(baseHeight = 6, heightRandA = 3, heightRandB = 1)
		// foliage placer: radius, offset and height shape the leaf blob around the trunk
		blobFoliagePlacer(radius = constant(2), offset = constant(0), height = 3)
	}
	// 4) Placed feature: control where/how often the Highlands tree is placed
	val highlandsTree = configuredFeature("aether_highlands_tree", highlandsTreeCfg) {}
	val highlandsTreePlaced = placedFeature("aether_highlands_tree_placed", highlandsTree) {
		inSquare()
		count(constant(10))
		heightRange(uniformHeightProvider(64, 128))
		biome()
	}

	// 5) Simple flower configured+placed feature (surface decoration)
	val flowerCfg = simpleBlock(toPlace = simpleStateProvider(Blocks.DANDELION))
	val flowers = configuredFeature("aether_highlands_flower", flowerCfg) {}
	val flowersPlaced = placedFeature("aether_highlands_flower_placed", flowers) {
		inSquare()
		rarityFilter(3)
		heightRange(uniformHeightProvider(64, 128))
		biome()
	}

	// 6) Underground ore: configured + placed (e.g., coal-like veins)
	val oreCfg = ore(
		size = 10, // approximate vein size (max blocks)
		discardChanceOnAirExposure = 0.1, // chance to skip ore blocks exposed to air
		targets = listOf(Target()) // Target(ruleTest, state) - default Target() matches stone
	)
	val ore = configuredFeature("aether_coal_ore", oreCfg) {}
	val orePlaced = placedFeature("aether_coal_ore_placed", ore) {
		inSquare()
		count(constant(20))
		heightRange(uniformHeightProvider(0, 128))
		biome()
	}

	// 7) Forest tree variant: denser, taller trees used in the forest biome
	val forestTreeCfg = tree {
		dirtProvider = simpleStateProvider(Blocks.DIRT)
		trunkProvider = simpleStateProvider(Blocks.SPRUCE_LOG)
		foliageProvider = simpleStateProvider(Blocks.SPRUCE_LEAVES)
		straightTrunkPlacer(baseHeight = 8, heightRandA = 4, heightRandB = 2)
		blobFoliagePlacer(radius = constant(3), offset = constant(0), height = 4)
	}
	val forestTree = configuredFeature("aether_forest_tree", forestTreeCfg) {}
	val forestTreePlaced = placedFeature("aether_forest_tree_placed", forestTree) {
		inSquare() // square placement
		count(constant(16)) // number of trees per chunk
		heightRange(uniformHeightProvider(64, 140)) // height range for tree placement
		biome() // biome filter
	}

	// 8) Shore variants: sparser placement near edges
	val shoreTreePlaced = placedFeature("aether_shore_tree_placed", highlandsTree) {
		inSquare()
		count(constant(3))
		heightRange(uniformHeightProvider(62, 75))
		biome()
	}
	val shoreFlowersPlaced = placedFeature("aether_shore_flowers_placed", flowers) {
		inSquare()
		count(constant(5))
		heightRange(uniformHeightProvider(62, 75))
		biome()
	}

	// 9) Carver: gentle cave configuration shared by biomes
	val caveCfg = caveConfig {
		probability = 0.08
		y = uniformHeightProvider(32, 128)
		yScale = constant(0.5f)
		lavaLevel = absolute(8)
		horizontalRadiusMultiplier = constant(1.0f)
		verticalRadiusMultiplier = constant(0.7f)
		floorLevel = constant(-0.2f)
	}
	val cave = configuredCarver("aether_cave", caveCfg) {}

	// 10) Biome: Aether Highlands - rolling terrain with moderate trees and flowers
	val highlands = biome("aether_highlands") {
		downfall = 0.3f
		hasPrecipitation = true
		temperature = 0.8f
		effects {
			fogColor = 0xBFEFFF
			skyColor = 0x99D9FF
			waterColor = 0x34A7F0
			waterFogColor = 0x0A2C4F
		}
		spawners {
			creature {
				// spawner(entityType, weight, minCount, maxCount)
				spawner(EntityTypes.COW, 6, 2, 4)
				spawner(EntityTypes.SHEEP, 8, 2, 4)
			}
			monster {
				spawner(EntityTypes.SKELETON, 80, 1, 2)
				spawner(EntityTypes.ZOMBIE, 80, 1, 2)
			}
		}
		// spawnCosts: controls per-entity population budget (energyBudget, charge)
		spawnCosts {
			this[EntityTypes.COW] = spawnCost(1.2f, 0.1f)
			this[EntityTypes.SHEEP] = spawnCost(1.0f, 0.08f)
		}
		carvers { air(cave) }
		features {
			undergroundOres = listOf(orePlaced)
			vegetalDecoration = listOf(highlandsTreePlaced, flowersPlaced)
		}
	}

	// 11) Biome: Aether Forest - denser canopy and richer mob spawns
	val forest = biome("aether_forest") {
		downfall = 0.5f
		hasPrecipitation = true
		temperature = 0.7f
		effects {
			fogColor = 0xB0E3FF
			skyColor = 0x8ECCFF
			waterColor = 0x3095D8
			waterFogColor = 0x0A2644
		}
		spawners {
			creature {
				spawner(EntityTypes.COW, 6, 2, 4)
				spawner(EntityTypes.SHEEP, 6, 2, 4)
				spawner(EntityTypes.WOLF, 4, 1, 2)
			}
			monster {
				spawner(EntityTypes.SKELETON, 100, 1, 2)
				spawner(EntityTypes.SPIDER, 60, 1, 2)
				spawner(EntityTypes.ZOMBIE, 100, 1, 2)
			}
		}
		carvers { air(cave) }
		features {
			undergroundOres = listOf(orePlaced)
			vegetalDecoration = listOf(forestTreePlaced, flowersPlaced)
		}
	}

	// 12) Biome: Aether Shores - lower elevation edges with sparse trees and flowers
	val shores = biome("aether_shores") {
		downfall = 0.2f
		hasPrecipitation = false
		temperature = 0.9f
		effects {
			fogColor = 0xC6F1FF
			skyColor = 0xA2E0FF
			waterColor = 0x3AB1FF
			waterFogColor = 0x0B2F59
		}
		spawners {
			creature { spawner(EntityTypes.SHEEP, 10, 3, 5) }
			monster { spawner(EntityTypes.ZOMBIE, 60, 1, 2) }
		}
		carvers { air(cave) }
		features {
			undergroundOres = listOf(orePlaced)
			vegetalDecoration = listOf(shoreTreePlaced, shoreFlowersPlaced)
		}
	}

	// 13) Dimension: bind the custom type and use a noise generator with a checkerboard biome source
	dimension("aether", type = aetherType) {
		noiseGenerator(
			settings = aetherNoise,
			biomeSource = checkerboard(scale = 3, highlands, forest, shores)
		)
	}

	// 14) World preset: expose this custom dimension as an Overworld option in the Create World UI
	worldPreset("aether_preset") {
		dimension(DimensionTypes.OVERWORLD) { type = aetherType }
	}
}

// Usage: dataPack("my_pack") { createAetherDimension() }
```

Tips:

- Validate each JSON via the Wiki pages linked above as you fill in real configs.
- Keep feature counts and structure spacing reasonable for performance.
- Use GameTest to validate worldgen deterministically; see [Test Features](/docs/advanced/test-features).
