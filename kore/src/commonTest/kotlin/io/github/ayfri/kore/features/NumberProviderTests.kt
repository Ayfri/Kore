package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.loottables.lootTable
import io.github.ayfri.kore.features.loottables.pool
import io.github.ayfri.kore.features.predicates.providers.*
import io.github.ayfri.kore.features.predicates.types.EntityType
import io.github.ayfri.kore.generated.EnvironmentAttributes
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.numberProviderTests() {
	lootTable("binomial_test") {
		pool {
			rolls = binomial(5f, 0.5f)
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"rolls": {
						"type": "minecraft:binomial",
						"n": 5.0,
						"p": 0.5
					},
					"entries": []
				}
			]
		}
	""".trimIndent()

	lootTable("constant_test") {
		pool {
			rolls = constant(5f)
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"rolls": 5.0,
					"entries": []
				}
			]
		}
	""".trimIndent()

	lootTable("enchantment_level_test") {
		pool {
			rolls = enchantmentLevel(5)
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"rolls": {
						"type": "minecraft:enchantment_level",
						"amount": 5
					},
					"entries": []
				}
			]
		}
	""".trimIndent()

	lootTable("environment_attribute_test") {
		pool {
			rolls = environmentAttribute(EnvironmentAttributes.Gameplay.BABY_VILLAGER_ACTIVITY)
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"rolls": {
						"type": "minecraft:environment_attribute",
						"attribute": "minecraft:gameplay/baby_villager_activity"
					},
					"entries": []
				}
			]
		}
	""".trimIndent()

	lootTable("score_context_test") {
		pool {
			rolls = scoreNumber("kills", EntityType.THIS)
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"rolls": {
						"type": "minecraft:score",
						"target": {
							"type": "minecraft:context",
							"target": "this"
						},
						"score": "kills"
					},
					"entries": []
				}
			]
		}
	""".trimIndent()

	lootTable("score_fixed_test") {
		pool {
			rolls = scoreNumber("kills", name = "Steve")
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"rolls": {
						"type": "minecraft:score",
						"target": {
							"type": "minecraft:fixed",
							"name": "Steve"
						},
						"score": "kills"
					},
					"entries": []
				}
			]
		}
	""".trimIndent()

	lootTable("storage_test") {
		pool {
			rolls = storageNumber("my_pack:data", "player.health")
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"rolls": {
						"type": "minecraft:storage",
						"storage": "my_pack:data",
						"path": "player.health"
					},
					"entries": []
				}
			]
		}
	""".trimIndent()

	lootTable("sum_test") {
		pool {
			rolls = sum(constant(1f), uniform(1f, 3f))
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"rolls": {
						"type": "minecraft:sum",
						"summands": [
							1.0,
							{
								"type": "minecraft:uniform",
								"min": 1.0,
								"max": 3.0
							}
						]
					},
					"entries": []
				}
			]
		}
	""".trimIndent()

	lootTable("uniform_test") {
		pool {
			rolls = uniform(1f, 5f)
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"rolls": {
						"type": "minecraft:uniform",
						"min": 1.0,
						"max": 5.0
					},
					"entries": []
				}
			]
		}
	""".trimIndent()
}

class NumberProviderTests : FunSpec({
	test("number providers") {
		dataPack("numberProviders") {
			pretty()
			numberProviderTests()
		}
	}
})
