package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.features.advancements.types.item
import io.github.ayfri.kore.features.predicates.conditions.allOf
import io.github.ayfri.kore.features.predicates.conditions.matchTool
import io.github.ayfri.kore.features.predicates.conditions.randomChance
import io.github.ayfri.kore.features.predicates.conditions.weatherCheck
import io.github.ayfri.kore.features.predicates.predicate
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.Items

fun DataPack.predicateTests() {
	val myPredicate = predicate("test1") {
		matchTool {
			item(Items.DIAMOND_PICKAXE)
		}

		allOf {
			randomChance(0.5f)
			weatherCheck(raining = true, thundering = false)
		}
	}

	predicates.last() assertsIs """
		[
			{
				"condition": "minecraft:match_tool",
				"predicate": {
					"items": "minecraft:diamond_pickaxe"
				}
			},
			{
				"condition": "minecraft:all_of",
				"terms": [
					{
						"condition": "minecraft:random_chance",
						"chance": 0.5
					},
					{
						"condition": "minecraft:weather_check",
						"raining": true,
						"thundering": false
					}
				]
			}
		]
	""".trimIndent()

	function("test") {
		execute {
			ifCondition {
				predicate(myPredicate)
			}

			run {
				debug("predicate validated !")
			}
		}
	}
}
