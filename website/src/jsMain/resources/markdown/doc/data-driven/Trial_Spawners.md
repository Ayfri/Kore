---
root: .components.layouts.MarkdownLayout
title: Trial Spawners - Custom Trial Spawner Configurations in Kore
nav-title: Trial Spawners
description: Configure custom Minecraft trial spawners with Kore's Kotlin DSL. Set spawned mobs, their NBT and equipment, mob counts, spawn delay and reward loot tables.
keywords: minecraft trial spawner, datapack trial_spawner, kore trial spawner, trial spawner configuration, custom trial chamber, ominous trial spawner, spawn_potentials
date-created: 2026-09-23
date-modified: 2026-09-23
routeOverride: /docs/data-driven/trial-spawners
---

# Trial Spawners

A trial spawner configuration decides what a trial spawner block spawns, how many mobs it spawns per player, how fast, and which loot
tables it ejects once the players beat it. A trial spawner block points to one configuration for its normal state and one for its
ominous state, so a custom trial chamber can mix vanilla and custom configurations freely.

## File Structure

```
data/<namespace>/trial_spawner/<name>.json
```

## Creating a Configuration

Use `trialSpawner` on your `DataPack`. Every field is optional, the game falling back to its defaults: a 4 block spawn range, 6 total
mobs, 2 simultaneous mobs, 40 ticks between spawns and the vanilla reward loot tables.

```kotlin
val skeletonArchers = dp.trialSpawner("skeleton_archers") {
	simultaneousMobs = 3f
	simultaneousMobsAddedPerPlayer = 0.5f
	ticksBetweenSpawn = 20
	totalMobs = 8f

	spawnPotential(EntityTypes.SKELETON, weight = 3) {
		equipment(LootTables.Equipment.TRIAL_CHAMBER_RANGED, 0f)
	}
	spawnPotential(EntityTypes.STRAY) {
		equipment(LootTables.Equipment.TRIAL_CHAMBER_RANGED, mapOf(EquipmentSlot.HEAD to 0.5f))
	}
}
```

| Property                         | Default                 | Description                                                  |
|----------------------------------|-------------------------|--------------------------------------------------------------|
| `itemsToDropWhenOminous`         | vanilla loot table      | Loot table of the items dropped around players when ominous. |
| `lootTablesToEject`              | vanilla key/consumables | Weighted reward loot tables, see `lootTableToEject`.         |
| `simultaneousMobs`               | `2`                     | Mobs alive at the same time for a single player.             |
| `simultaneousMobsAddedPerPlayer` | `1`                     | Extra simultaneous mobs for each additional player.          |
| `spawnPotentials`                | none                    | Weighted mobs to spawn, see `spawnPotential`.                |
| `spawnRange`                     | `4`                     | Spawn distance from the spawner, from `1` to `128`.          |
| `ticksBetweenSpawn`              | `40`                    | Delay between two spawns.                                    |
| `totalMobs`                      | `6`                     | Mobs to defeat for a single player.                          |
| `totalMobsAddedPerPlayer`        | `2`                     | Extra mobs to defeat for each additional player.             |

## Spawned Mobs

`spawnPotential` adds a weighted mob, its block customizing what gets spawned:

- `entity { }` adds NBT to the spawned entity, its `id` being always kept.
- `equipment(lootTable, slotDropChance)` rolls the mob's equipment from a loot table, each slot dropping with the same chance. Pass a
  `Map<EquipmentSlot, Float>` instead to set a chance per slot.
- `customSpawnRules(blockLightLimit, skyLightLimit)` replaces the mob's own spawn rules with light ranges, like `0..7`.

```kotlin
dp.trialSpawner("slimes") {
	spawnPotential(EntityTypes.SLIME, weight = 3) {
		entity { this["Size"] = 1 }
	}
	spawnPotential(EntityTypes.SLIME) {
		entity { this["Size"] = 2 }
		customSpawnRules(blockLightLimit = 0..7)
	}
}
```

## Rewards

`lootTableToEject` adds a weighted loot table ejected for each player once the trial is beaten, replacing the vanilla ones.

```kotlin
dp.trialSpawner("ominous_breezes") {
	itemsToDropWhenOminous = LootTables.Spawners.TrialChamber.ITEMS_TO_DROP_WHEN_OMINOUS
	lootTableToEject(LootTables.Spawners.Ominous.TrialChamber.KEY, weight = 3)
	lootTableToEject(LootTables.Spawners.Ominous.TrialChamber.CONSUMABLES, weight = 7)
	spawnPotential(EntityTypes.BREEZE)
}
```

Produces JSON:

```json
{
	"items_to_drop_when_ominous": "minecraft:spawners/trial_chamber/items_to_drop_when_ominous",
	"loot_tables_to_eject": [
		{
			"data": "minecraft:spawners/ominous/trial_chamber/key",
			"weight": 3
		},
		{
			"data": "minecraft:spawners/ominous/trial_chamber/consumables",
			"weight": 7
		}
	],
	"spawn_potentials": [
		{
			"data": {
				"entity": {
					"id": "minecraft:breeze"
				}
			},
			"weight": 1
		}
	]
}
```

Reference: [Trial spawner configuration](https://minecraft.wiki/w/Trial_spawner_configuration)
