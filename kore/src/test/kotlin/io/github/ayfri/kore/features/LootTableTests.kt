package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.loot
import io.github.ayfri.kore.features.itemmodifiers.functions.conditions
import io.github.ayfri.kore.features.itemmodifiers.functions.enchantRandomly
import io.github.ayfri.kore.features.itemmodifiers.functions.setCount
import io.github.ayfri.kore.features.loottables.*
import io.github.ayfri.kore.features.loottables.entries.LootEntryDynamicName
import io.github.ayfri.kore.features.loottables.entries.dynamic
import io.github.ayfri.kore.features.loottables.entries.functions
import io.github.ayfri.kore.features.loottables.entries.lootTable
import io.github.ayfri.kore.features.predicates.conditions.randomChance
import io.github.ayfri.kore.features.predicates.conditions.weatherCheck
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.features.predicates.providers.uniform
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.Enchantments
import io.github.ayfri.kore.generated.LootTables

fun DataPack.lootTableTests() {
	val lootTable = lootTable {
		functions {
			enchantRandomly {
				options += Enchantments.LOOTING
			}
		}

		pool {
			rolls = constant(2f)
			bonusRolls = constant(1f)
			conditions {
				weatherCheck(true)
			}

			entries {
				dynamic(LootEntryDynamicName.SHERDS)

				lootTable(LootTables.Gameplay.PIGLIN_BARTERING) {
					conditions {
						weatherCheck(true)
					}

					functions {
						setCount(1f) {
							conditions {
								randomChance(0.5f)
							}
						}
					}
				}
			}

			functions {
				setCount(1f)
			}
		}
	}

	lootTables.last() assertsIs """
		{
			"functions": [
				{
					"function": "minecraft:enchant_randomly",
					"options": "minecraft:looting"
				}
			],
			"pools": [
				{
					"rolls": 2.0,
					"bonus_rolls": 1.0,
					"conditions": [
						{
							"condition": "minecraft:weather_check",
							"raining": true
						}
					],
					"entries": [
						{
							"type": "minecraft:dynamic",
							"name": "minecraft:sherds"
						},
						{
							"type": "minecraft:loot_table",
							"name": "minecraft:gameplay/piglin_bartering",
							"functions": [
								{
									"function": "minecraft:set_count",
									"conditions": [
										{
											"condition": "minecraft:random_chance",
											"chance": 0.5
										}
									],
									"count": 1.0
								}
							]
						}
					],
					"functions": [
						{
							"function": "minecraft:set_count",
							"count": 1.0
						}
					]
				}
			]
		}
	""".trimIndent()

	load {
		loot(self(), lootTable)
	}

	lootTable("test") {
		pool {
			rolls = uniform(5f, 2f)
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"rolls": {
						"type": "minecraft:uniform",
						"min": 5.0,
						"max": 2.0
					},
					"entries": []
				}
			]
		}
	""".trimIndent()
}
