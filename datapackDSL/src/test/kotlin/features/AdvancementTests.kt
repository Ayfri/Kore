package features

import DataPack
import arguments.types.literals.self
import commands.advancement
import commands.say
import features.advancements.*
import features.advancements.triggers.ConsumeItem
import features.advancements.types.itemStack
import features.predicates.conditions.anyOf
import features.predicates.conditions.randomChance
import features.predicates.conditions.timeCheck
import functions.function
import functions.load
import generated.Advancements
import generated.Items
import generated.LootTables
import generated.Recipes
import utils.assertsIs

fun DataPack.advancementTests() {
	val advancement = advancement("test") {
		display(Items.DIAMOND_SWORD, "Hello", "World") {
			frame = AdvancementFrameType.CHALLENGE
		}

		parent = Advancements.Story.ROOT

		criteria(
			name = "test",
			triggerCondition = ConsumeItem(itemStack(Items.ENCHANTED_GOLDEN_APPLE))
		) {
			randomChance(chance = 0.5f)
			timeCheck(10f..20f)

			anyOf {
				timeCheck(10f..20f)
			}
		}

		requirements("test")
		rewards {
			experience = 10
			function = function("gradient") {
				say("Yay !")
			}

			loot = listOf(LootTables.Chests.IGLOO_CHEST)
			recipes = listOf(Recipes.POLISHED_BLACKSTONE_BRICK_STAIRS_FROM_POLISHED_BLACKSTONE_BRICKS_STONECUTTING)
		}

		sendsTelemetryEvent = false
	}

	advancements.last() assertsIs """
		{
			"display": {
				"icon": {
					"item": "minecraft:diamond_sword"
				},
				"title": "Hello",
				"description": "World",
				"frame": "challenge"
			},
			"parent": "minecraft:story/root",
			"criteria": {
				"test": {
					"trigger": "minecraft:consume_item",
					"conditions": {
						"player": [
							{
								"condition": "minecraft:random_chance",
								"chance": 0.5
							},
							{
								"condition": "minecraft:time_check",
								"value": {
									"min": 10.0,
									"max": 20.0
								}
							},
							{
								"condition": "minecraft:any_of",
								"terms": [
									{
										"condition": "minecraft:time_check",
										"value": {
											"min": 10.0,
											"max": 20.0
										}
									}
								]
							}
						],
						"item": {
							"items": [
								"minecraft:enchanted_golden_apple"
							]
						}
					}
				}
			},
			"requirements": [
				[
					"test"
				]
			],
			"rewards": {
				"experience": 10,
				"function": "test:gradient",
				"loot": [
					"minecraft:chests/igloo_chest"
				],
				"recipes": [
					"minecraft:polished_blackstone_brick_stairs_from_polished_blackstone_bricks_stonecutting"
				]
			},
			"sends_telemetry_event": false
		}
	""".trimIndent()

	load {
		advancement {
			grant(self(), advancement)
		}
	}
}
