package features

import DataPack
import commands.execute.execute
import commands.execute.run
import features.advancements.types.item
import features.predicates.conditions.allOf
import features.predicates.conditions.matchTool
import features.predicates.conditions.randomChance
import features.predicates.conditions.weatherCheck
import features.predicates.predicate
import functions.function
import generated.Items
import utils.assertsIs

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
					"items": [
						"minecraft:diamond_pickaxe"
					]
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
