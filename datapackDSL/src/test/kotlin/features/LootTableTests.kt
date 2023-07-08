package features

import DataPack
import features.loottables.LootPool
import features.loottables.LootTable
import features.predicates.Predicate
import features.predicates.conditions.weatherCheck
import features.predicates.providers.constant
import generated.LootTables

fun DataPack.lootTableTests() {
	val lootTable = LootTable(
		fileName = "loot_table",
		functions = listOf("test fonction loottable"),
		pools = listOf(
			LootPool(
				rolls = constant(2f),
				bonusRolls = constant(1f),
				conditions = Predicate().apply {
					weatherCheck(true)
				}.predicateConditions,
				entries = listOf(
					features.loottables.entries.LootTable(
						name = LootTables.Gameplay.PIGLIN_BARTERING,
						conditions = Predicate().apply {
							weatherCheck(true)
						}.predicateConditions,
						functions = listOf("test fonction lootpool entry loottable"),
						quality = null,
						weight = null
					)
				),
				functions = listOf("test fonction lootpool")
			)
		)
	)

	lootTables += lootTable
}
