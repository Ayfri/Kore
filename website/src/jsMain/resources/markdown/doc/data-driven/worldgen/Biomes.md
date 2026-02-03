---
root: .components.layouts.MarkdownLayout
title: Biomes
nav-title: Biomes
description: Define biomes with climate, effects, spawns, carvers, and features using Kore's DSL.
keywords: minecraft, datapack, kore, worldgen, biome, carver, spawner, effects
date-created: 2026-02-03
date-modified: 2026-02-03
routeOverride: /docs/data-driven/worldgen/biomes
---

# Biomes

Biomes define climate, visuals, mob spawns, carvers, and the placed features list for each decoration step. In Minecraft, biomes control not
just the terrain appearance but also weather behavior, mob spawning rules, and which features generate.

## How Biomes Work

In the Overworld, biome placement is determined by 6 climate parameters:

- **Temperature** - Controls snow/ice coverage and vegetation types (5 levels from frozen to hot)
- **Humidity** - Affects vegetation density (5 levels from arid to humid)
- **Continentalness** - Determines ocean/beach/inland placement
- **Erosion** - Controls flat vs mountainous terrain (7 levels)
- **Weirdness** - Triggers biome variants (e.g., Jungle → Bamboo Jungle)
- **Depth** - Determines surface vs cave biomes

These parameters form a 6D space where each biome occupies defined intervals. The game selects the closest matching biome for any given
location.

References: [Biome](https://minecraft.wiki/w/Biome), [Biome definition](https://minecraft.wiki/w/Biome_definition)

## Basic Biome

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
}
```

## Effects

The `effects` block controls visual and audio aspects of the biome. These settings define the atmosphere players experience, from sky color
to ambient sounds.

Reference: [Biome definition - Effects](https://minecraft.wiki/w/Biome_definition#Effects)

```kotlin
effects {
	// Required colors
	skyColor = 0x78A7FF
	fogColor = 0xC0D8FF
	waterColor = 0x3F76E4
	waterFogColor = 0x050533

	// Optional colors
	foliageColor = 0x59AE30
	grassColor = 0x79C05A

	// Optional ambient effects
	// ambientSound = ...
	// moodSound { ... }
	// additionsSound { ... }
	// music { ... }
	// particle { ... }
}
```

## Spawners

Define mob spawn rules per category. Each spawner entry specifies the entity type, spawn weight (relative probability), and count range.
Higher weights mean more frequent spawns relative to other entries in the same category.

Reference: [Biome definition - Mob spawning](https://minecraft.wiki/w/Biome_definition#Mob_spawning)

```kotlin
spawners {
	creature {
		spawner(EntityTypes.COW, weight = 6, minCount = 2, maxCount = 4)
		spawner(EntityTypes.SHEEP, weight = 8, minCount = 2, maxCount = 4)
	}
	monster {
		spawner(EntityTypes.SKELETON, weight = 80, minCount = 1, maxCount = 2)
		spawner(EntityTypes.ZOMBIE, weight = 80, minCount = 1, maxCount = 2)
	}
	// Other categories: ambient, waterCreature, undergroundWaterCreature, waterAmbient, misc, axolotls
}
```

## Spawn Costs

Spawn costs control mob density using an energy budget system. Each mob type has an `energyBudget` (max population density) and `charge` (
cost per mob). This prevents overcrowding while allowing natural mob distribution.

Reference: [Biome definition - Spawn costs](https://minecraft.wiki/w/Biome_definition#Spawn_costs)

```kotlin
spawnCosts {
	this[EntityTypes.COW] = spawnCost(energyBudget = 1.2f, charge = 0.1f)
	this[EntityTypes.SHEEP] = spawnCost(energyBudget = 1.0f, charge = 0.08f)
}
```

## Carvers

Carvers hollow out terrain to create caves and canyons. They run during the `carvers` generation step, after terrain noise but before
features. Two carving modes exist: `air` for standard caves and `liquid` for underwater caves.

Reference: [Carver](https://minecraft.wiki/w/Carver)

```kotlin
carvers {
	air(myCaveCarver)        // Carves air (caves)
	liquid(myUnderwaterCave) // Carves underwater caves
}
```

See [Carvers](#carvers-cavescanyons) below for creating configured carvers.

## Features

Features are attached to biomes via decoration steps. Each step runs in order during chunk generation, with structures placing before
features within the same step. See the [main worldgen page](../worldgen#decoration-steps) for the full step list.

Reference: [Biome definition - Features](https://minecraft.wiki/w/Biome_definition#Features)

```kotlin
features {
	fluidSprings = listOf(...)
	lakes = listOf(...)
	localModifications = listOf(...)
	rawGeneration = listOf(...)
	strongholds = listOf(...)
	surfaceStructures = listOf(...)
	topLayerModification = listOf(...)
	undergroundDecoration = listOf(...)
	undergroundOres = listOf(orePlaced)
	undergroundStructures = listOf(...)
	vegetalDecoration = listOf(treePlaced, flowerPlaced)
}
```

See [Features](./features) for creating configured and placed features.

---

# Carvers (Caves/Canyons)

Configured carvers remove terrain to form cave systems and canyons. They use noise-based algorithms to create natural-looking underground
spaces. Carvers run after terrain generation but before features, ensuring caves don't destroy placed decorations.

Minecraft has two carver types:

- **Cave carvers** - Create winding tunnel systems with variable radius
- **Canyon carvers** - Create deep ravines with steep walls

References: [Carver](https://minecraft.wiki/w/Carver), [Configured carver](https://minecraft.wiki/w/Configured_carver)

## Cave Carver

```kotlin
val caveCfg = caveConfig {
	floorLevel = constant(-0.2f)
	horizontalRadiusMultiplier = constant(1.0f)
	lavaLevel = absolute(8)
	probability = 0.08
	verticalRadiusMultiplier = constant(0.7f)
	y = uniformHeightProvider(32, 128)
	yScale = constant(0.5f)
}

val cave = dp.configuredCarver("my_cave", caveCfg) {}
```

## Canyon Carver

```kotlin
val canyonCfg = canyonConfig {
	lavaLevel = absolute(8)
	probability = 0.02
	y = uniformHeightProvider(10, 67)
	yScale = constant(3.0f)
	// Additional canyon-specific settings...
}

val canyon = dp.configuredCarver("my_canyon", canyonCfg) {}
```

---

# Complete Biome Example

```kotlin
fun DataPack.createHighlandsBiome() {
	// Create a cave carver
	val caveCfg = caveConfig {
		floorLevel = constant(-0.2f)
		horizontalRadiusMultiplier = constant(1.0f)
		lavaLevel = absolute(8)
		probability = 0.08
		verticalRadiusMultiplier = constant(0.7f)
		y = uniformHeightProvider(32, 128)
		yScale = constant(0.5f)
	}
	val cave = configuredCarver("highlands_cave", caveCfg) {}

	// Create placed features (see Features page)
	val treePlaced = /* ... */
	val orePlaced = /* ... */

		// Create the biome
		biome("highlands") {
			temperature = 0.8f
			downfall = 0.3f
			hasPrecipitation = true

			effects {
				fogColor = 0xBFEFFF
				skyColor = 0x99D9FF
				waterColor = 0x34A7F0
				waterFogColor = 0x0A2C4F
			}

			spawners {
				creature {
					spawner(EntityTypes.COW, 6, 2, 4)
					spawner(EntityTypes.SHEEP, 8, 2, 4)
				}
				monster {
					spawner(EntityTypes.SKELETON, 80, 1, 2)
					spawner(EntityTypes.ZOMBIE, 80, 1, 2)
				}
			}

			spawnCosts {
				this[EntityTypes.COW] = spawnCost(1.2f, 0.1f)
				this[EntityTypes.SHEEP] = spawnCost(1.0f, 0.08f)
			}

			carvers { air(cave) }

			features {
				undergroundOres = listOf(orePlaced)
				vegetalDecoration = listOf(treePlaced)
			}
		}
}
```
