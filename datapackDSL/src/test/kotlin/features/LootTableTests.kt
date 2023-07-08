package features

import DataPack
import arguments.self
import commands.loot
import features.loottables.*
import features.loottables.entries.lootTable
import features.predicates.conditions.weatherCheck
import features.predicates.providers.constant
import functions.load
import generated.LootTables

fun DataPack.lootTableTests() {
	val lootTable = lootTable("loot_table") {
		function("test fonction loottable2")
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
					function("function are currently not implemented")
				}
			}
			function("test fonction lootpool2")
		}
	}

	load {
		loot(self(), lootTable)
	}
}
