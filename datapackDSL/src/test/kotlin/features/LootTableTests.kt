package features

import DataPack
import arguments.self
import commands.loot
import features.itemmodifiers.conditions
import features.itemmodifiers.functions.enchantRandomly
import features.itemmodifiers.functions.setCount
import features.loottables.*
import features.loottables.entries.functions
import features.loottables.entries.lootTable
import features.predicates.conditions.randomChance
import features.predicates.conditions.weatherCheck
import features.predicates.providers.constant
import functions.load
import generated.Enchantments
import generated.LootTables

fun DataPack.lootTableTests() {
	val lootTable = lootTable("loot_table") {
		functions {
			enchantRandomly {
				this += Enchantments.LOOTING
			}
		}

		pool {
			rolls = constant(2f)
			bonusRolls = constant(1f)
			conditions {
				weatherCheck(true)
			}

			entries {
				lootTable(LootTables.Gameplay.PIGLIN_BARTERING) {
					conditions {
						weatherCheck(true)
					}

					functions {
						setCount(1f)

						conditions {
							randomChance(0.5f)
						}
					}
				}
			}

			functions {
				setCount(1f)
			}
		}
	}

	load {
		loot(self(), lootTable)
	}
}
