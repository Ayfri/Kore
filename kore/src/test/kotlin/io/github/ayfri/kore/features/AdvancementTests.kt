package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.components.types.damage
import io.github.ayfri.kore.arguments.components.types.enchantment
import io.github.ayfri.kore.arguments.components.types.enchantments
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.advancement
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.features.advancements.*
import io.github.ayfri.kore.features.advancements.triggers.*
import io.github.ayfri.kore.features.predicates.conditions.anyOf
import io.github.ayfri.kore.features.predicates.conditions.entityProperties
import io.github.ayfri.kore.features.predicates.conditions.randomChance
import io.github.ayfri.kore.features.predicates.conditions.timeCheck
import io.github.ayfri.kore.features.predicates.sub.*
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.*
import io.github.ayfri.kore.generated.Advancements

fun DataPack.advancementTests() {
	val advancement = advancement("test") {
		display(Items.DIAMOND_SWORD, "Hello", "World") {
			frame = AdvancementFrameType.CHALLENGE
		}

		parent = Advancements.Story.ROOT

		@Suppress("DEPRECATION")
		criteria(
			name = "test",
			triggerCondition = ConsumeItem("test", item = itemStack(Items.ENCHANTED_GOLDEN_APPLE))
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

			loots(LootTables.Chests.IGLOO_CHEST)
			recipes(Recipes.POLISHED_BLACKSTONE_BRICK_STAIRS_FROM_POLISHED_BLACKSTONE_BRICKS_STONECUTTING)
		}

		sendsTelemetryEvent = false
	}

	advancements.last() assertsIs """
		{
			"display": {
				"icon": {
					"id": "minecraft:diamond_sword"
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
							"items": "minecraft:enchanted_golden_apple"
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
				"function": "features_tests:gradient",
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

	advancement("icon_components") {
		display(Items.AIR, "Hello", "World") {
			icon(Items.DIAMOND_SWORD, 10) {
				enchantments {
					enchantment(Enchantments.SHARPNESS, 5)
				}
			}
		}
	}

	advancements.last() assertsIs """
		{
			"display": {
				"icon": {
					"id": "minecraft:diamond_sword",
					"components": {
						"enchantments": {
							"minecraft:sharpness": 5
						}
					},
					"count": 10
				},
				"title": "Hello",
				"description": "World",
				"frame": "task"
			},
			"criteria": {}
		}
	""".trimIndent()

	advancement("consume_item") {
		criteria {
			consumeItem("consume_item") {
				item {
					items = listOf(Items.ENCHANTED_GOLDEN_APPLE)
					components {
						damage(3)
					}
				}

				conditions {
					randomChance(0.5f)
					timeCheck(10f..20f)
				}
			}
		}
	}

	advancements.last() assertsIs """
		{
			"criteria": {
				"consume_item": {
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
							}
						],
						"item": {
							"items": "minecraft:enchanted_golden_apple",
							"components": {
								"damage": 3
							}
						}
					}
				}
			}
		}
	""".trimIndent()

	advancement("crafter_recipe_crafter") {
		criteria {
			crafterRecipeCrafted(
				"crafter_recipe_crafter",
				Recipes.POLISHED_BLACKSTONE_BRICK_STAIRS_FROM_POLISHED_BLACKSTONE_BRICKS_STONECUTTING
			) {
				conditions {
					randomChance(0.5f)
					timeCheck(10f..20f)
				}

				ingredient {
					items = listOf(Items.ENCHANTED_GOLDEN_APPLE)
					components {
						damage(3)
					}
				}
			}
		}
	}

	advancements.last() assertsIs """
		{
			"criteria": {
				"crafter_recipe_crafter": {
					"trigger": "minecraft:crafter_recipe_crafted",
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
							}
						],
						"ingredients": [
							{
								"items": "minecraft:enchanted_golden_apple",
								"components": {
									"damage": 3
								}
							}
						],
						"recipe_id": "minecraft:polished_blackstone_brick_stairs_from_polished_blackstone_bricks_stonecutting"
					}
				}
			}
		}
	""".trimIndent()

	advancement("effects_changed") {
		criteria {
			effectsChanged("effects_changed") {
				effect(Effects.SPEED) {
					amplifier = rangeOrInt(1..3)
					duration = rangeOrInt(100..200)
				}
				source {
					conditions {
						entityProperties {
							type(EntityTypes.WITCH)
						}
					}
				}
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"effects_changed": {
					"trigger": "minecraft:effects_changed",
					"conditions": {
						"effects": {
							"minecraft:speed": {
								"amplifier": {
									"min": 1,
									"max": 3
								},
								"duration": {
									"min": 100,
									"max": 200
								}
							}
						},
						"source": [
							{
								"condition": "minecraft:entity_properties",
								"predicate": {
									"type": "minecraft:witch"
								}
							}
						]
					}
				}
			}
		}
	""".trimIndent()

	advancement("enter_block") {
		criteria {
			enterBlock("enter_block") {
				block = Blocks.REDSTONE_LAMP
				states {
					this["lit"] = "true"
				}
			}
		}
	}

	advancements.last() assertsIs """
		{
			"criteria": {
				"enter_block": {
					"trigger": "minecraft:enter_block",
					"conditions": {
						"block": "minecraft:redstone_lamp",
						"states": {
							"lit": "true"
						}
					}
				}
			}
		}
	""".trimIndent()

	advancement("fall_after_explosion") {
		criteria {
			fallAfterExplosion("fall_after_explosion") {
				distance {
					x(10f, 20f)
					absolute(10f)
				}

				startPosition {
					block {
						blocks(Blocks.GOLD_BLOCK)
						states {
							this["lit"] = "true"
						}
					}

					light = rangeOrInt(10)
				}

				cause {
					conditionEntity {
						type(EntityTypes.PLAYER)
					}
				}
			}
		}
	}

	advancements.last() assertsIs """
		{
			"criteria": {
				"fall_after_explosion": {
					"trigger": "minecraft:fall_after_explosion",
					"conditions": {
						"start_position": {
							"block": {
								"blocks": "minecraft:gold_block",
								"state": {
									"lit": "true"
								}
							},
							"light": 10
						},
						"distance": {
							"absolute": {
								"min": 10.0,
								"max": 10.0
							},
							"x": {
								"min": 10.0,
								"max": 20.0
							}
						},
						"cause": {
							"type": "minecraft:player"
						}
					}
				}
			}
		}
	""".trimIndent()
}
