package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrDouble
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.predicates.conditions.entityProperties
import io.github.ayfri.kore.features.predicates.predicate
import io.github.ayfri.kore.features.predicates.sub.entityspecific.*
import io.github.ayfri.kore.generated.Recipes
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.predicateEntityTypeSpecificTests() {
	predicate("fishing_hook_type_specific") {
		entityProperties {
			fishingHookTypeSpecific(inOpenWater = true)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"type_specific": {
					"type": "minecraft:fishing_hook",
					"in_open_water": true
				}
			}
		}
	""".trimIndent()

	predicate("lightning_type_specific") {
		entityProperties {
			lightningTypeSpecific {
				blocksSetOnFire = rangeOrInt(1..5)
			}
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"type_specific": {
					"type": "minecraft:lightning",
					"blocks_set_on_fire": {
						"min": 1,
						"max": 5
					}
				}
			}
		}
	""".trimIndent()

	predicate("player_type_specific") {
		entityProperties {
			playerTypeSpecific {
				gamemodes(Gamemode.CREATIVE)
				recipes {
					this[Recipes.BOW] = true
				}
				input {
					forward = true
					backward = false
					left = true
					right = false
					jump = true
					sneak = false
					sprint = true
				}
			}
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"type_specific": {
					"type": "minecraft:player",
					"gamemode": [
						"creative"
					],
					"recipes": {
						"minecraft:bow": true
					},
					"input": {
						"forward": true,
						"backward": false,
						"left": true,
						"right": false,
						"jump": true,
						"sneak": false,
						"sprint": true
					}
				}
			}
		}
	""".trimIndent()

	predicate("player_food_type_specific") {
		entityProperties {
			playerTypeSpecific {
				food {
					level = rangeOrInt(5..15)
					saturation = rangeOrDouble(1.0, 10.0)
				}
			}
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"type_specific": {
					"type": "minecraft:player",
					"food": {
						"level": {
							"min": 5,
							"max": 15
						},
						"saturation": {
							"min": 1.0,
							"max": 10.0
						}
					}
				}
			}
		}
	""".trimIndent()

	predicate("raider_type_specific") {
		entityProperties {
			raiderTypeSpecific(hasRaid = true, isCaptain = false)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"type_specific": {
					"type": "minecraft:raider",
					"has_raid": true,
					"is_captain": false
				}
			}
		}
	""".trimIndent()

	predicate("sheep_type_specific") {
		entityProperties {
			sheepTypeSpecific(sheared = true)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"type_specific": {
					"type": "minecraft:sheep",
					"sheared": true
				}
			}
		}
	""".trimIndent()

	predicate("slime_type_specific") {
		entityProperties {
			slimeTypeSpecific(rangeOrInt(2))
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"type_specific": {
					"type": "minecraft:slime",
					"size": 2
				}
			}
		}
	""".trimIndent()
}

class PredicateEntityTypeSpecificTests : FunSpec({
	test("predicate entity type specific") {
		dataPack("predicateEntityTypeSpecific") {
			pretty()
			predicateEntityTypeSpecificTests()
		}
	}
})
