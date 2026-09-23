package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.EquipmentSlot
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.trialspawners.*
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.LootTables
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.set
import io.kotest.core.spec.style.FunSpec

fun DataPack.trialSpawnerTests() {
	trialSpawner("empty")

	trialSpawners.last() assertsIs """
		{}
	""".trimIndent()

	trialSpawner("slimes") {
		simultaneousMobs = 3f
		simultaneousMobsAddedPerPlayer = 0.5f
		spawnPotential(EntityTypes.SLIME, weight = 3) {
			entity { this["Size"] = 1 }
		}
		spawnPotential(EntityTypes.SLIME) {
			entity { this["Size"] = 2 }
			customSpawnRules(blockLightLimit = 0..7)
		}
		ticksBetweenSpawn = 20
	}

	trialSpawners.last() assertsIs """
		{
			"simultaneous_mobs": 3.0,
			"simultaneous_mobs_added_per_player": 0.5,
			"spawn_potentials": [
				{
					"data": {
						"entity": {
							"id": "minecraft:slime",
							"Size": 1
						}
					},
					"weight": 3
				},
				{
					"data": {
						"custom_spawn_rules": {
							"block_light_limit": {
								"max_inclusive": 7,
								"min_inclusive": 0
							}
						},
						"entity": {
							"id": "minecraft:slime",
							"Size": 2
						}
					},
					"weight": 1
				}
			],
			"ticks_between_spawn": 20
		}
	""".trimIndent()

	trialSpawner("ominous_strays") {
		itemsToDropWhenOminous = LootTables.Spawners.TrialChamber.ITEMS_TO_DROP_WHEN_OMINOUS
		lootTableToEject(LootTables.Spawners.Ominous.TrialChamber.KEY, weight = 3)
		lootTableToEject(LootTables.Spawners.Ominous.TrialChamber.CONSUMABLES, weight = 7)
		spawnPotential(EntityTypes.STRAY) {
			equipment(LootTables.Equipment.TRIAL_CHAMBER_RANGED, 0f)
		}
		spawnPotential(EntityTypes.SKELETON) {
			equipment(LootTables.Equipment.TRIAL_CHAMBER_RANGED, mapOf(EquipmentSlot.HEAD to 0.5f, EquipmentSlot.MAINHAND to 0f))
		}
		spawnRange = 6
		totalMobs = 8f
		totalMobsAddedPerPlayer = 2f
	}

	trialSpawners.last() assertsIs """
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
							"id": "minecraft:stray"
						},
						"equipment": {
							"loot_table": "minecraft:equipment/trial_chamber_ranged",
							"slot_drop_chances": 0.0
						}
					},
					"weight": 1
				},
				{
					"data": {
						"entity": {
							"id": "minecraft:skeleton"
						},
						"equipment": {
							"loot_table": "minecraft:equipment/trial_chamber_ranged",
							"slot_drop_chances": {
								"head": 0.5,
								"mainhand": 0.0
							}
						}
					},
					"weight": 1
				}
			],
			"spawn_range": 6,
			"total_mobs": 8.0,
			"total_mobs_added_per_player": 2.0
		}
	""".trimIndent()

	roundTrip(trialSpawners.last())
}

class TrialSpawnerTests : FunSpec({
	test("trial spawner") {
		dataPack("trialSpawner") {
			pretty()
			trialSpawnerTests()
		}
	}
})
