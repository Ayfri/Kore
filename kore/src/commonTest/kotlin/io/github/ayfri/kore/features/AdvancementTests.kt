package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.item.*
import io.github.ayfri.kore.arguments.components.matchers.enchantmentPredicate
import io.github.ayfri.kore.arguments.components.contains
import io.github.ayfri.kore.arguments.components.matchers.effects
import io.github.ayfri.kore.arguments.components.matchers.enchantments
import io.github.ayfri.kore.arguments.components.matchers.potions
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntStart
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.commands.advancement
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.advancements.*
import io.github.ayfri.kore.features.advancements.triggers.*
import io.github.ayfri.kore.features.advancements.triggers.InventorySlotsPredicate
import io.github.ayfri.kore.features.predicates.conditions.anyOf
import io.github.ayfri.kore.features.predicates.conditions.entityProperties
import io.github.ayfri.kore.features.predicates.conditions.randomChance
import io.github.ayfri.kore.features.predicates.conditions.timeCheck
import io.github.ayfri.kore.features.predicates.sub.*
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.*
import io.github.ayfri.kore.generated.Advancements
import io.github.ayfri.kore.generated.Enchantments
import io.github.ayfri.kore.generated.arguments.tagged.LootTableTagArgument
import io.github.ayfri.kore.generated.arguments.tagged.PotionTagArgument
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
import io.github.ayfri.kore.utils.pretty
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

fun DataPack.advancementTests() {
	val advancement = advancement("test") {
		display(Items.DIAMOND_SWORD, "Hello", "World") {
			frame = AdvancementFrameType.CHALLENGE
		}

		parent = Advancements.Story.ROOT

		@Suppress("DEPRECATION")
		criteria(
			name = "test",
			triggerCondition = ConsumeItem(item = itemStackPredicate(Items.ENCHANTED_GOLDEN_APPLE))
		) {
			randomChance(chance = 0.5f)
			timeCheck(WorldClocks.OVERWORLD, 10..20)

			anyOf {
				timeCheck(WorldClocks.OVERWORLD, 10..20)
			}
		}

		requirements("test")
		rewards {
			experience = 10
			function("gradient") {
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
						"player": {
							"type": "minecraft:all_of",
							"terms": [
								{
									"type": "minecraft:random_chance",
									"chance": 0.5
								},
								{
									"type": "minecraft:time_check",
									"clock": "minecraft:overworld",
									"value": {
										"max": 20,
										"min": 10
									}
								},
								{
									"type": "minecraft:any_of",
									"terms": {
										"type": "minecraft:time_check",
										"clock": "minecraft:overworld",
										"value": {
											"max": 20,
											"min": 10
										}
									}
								}
							]
						},
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
				"loot": "minecraft:chests/igloo_chest",
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

	val namespacedAdvancement = advancement("test_namespaced") {
		namespace = "advancements_tests"
	}

	namespacedAdvancement.asId() assertsIs "advancements_tests:test_namespaced"

	allTriggersTests()
	componentsDisplay()
}

private fun DataPack.allTriggersTests() {
	advancement("allay_drop_item_on_block") {
		criteria {
			allayDropItemOnBlock("allay_drop_item_on_block") {
				location {
					block {
						blocks(Blocks.STONE)
					}
				}
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"allay_drop_item_on_block": {
					"trigger": "minecraft:allay_drop_item_on_block",
					"conditions": {
						"location": {
							"type": "minecraft:location_check",
							"predicate": {
								"block": {
									"blocks": "minecraft:stone"
								}
							}
						}
					}
				}
			}
		}
	""".trimIndent()

	advancement("any_block_use") {
		criteria {
			anyBlockUse("any_block_use") {
				location {
					predicate { randomChance(0.5f) }
				}
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"any_block_use": {
					"trigger": "minecraft:any_block_use",
					"conditions": {
						"location": {
							"type": "minecraft:random_chance",
							"chance": 0.5
						}
					}
				}
			}
		}
	""".trimIndent()

	advancement("avoid_vibrations") {
		criteria {
			avoidVibration("avoid_vibration")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"avoid_vibration": {
					"trigger": "minecraft:avoid_vibration"
				}
			}
		}
	""".trimIndent()

	advancement("bee_nest_destroyed") {
		criteria {
			beeNestDestroyed("bee_nest_destroyed", numBeesInside = rangeOrInt(1)) {
				blocks(Blocks.BEE_NEST)
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"bee_nest_destroyed": {
					"trigger": "minecraft:bee_nest_destroyed",
					"conditions": {
						"blocks": "minecraft:bee_nest",
						"num_bees_inside": 1
					}
				}
			}
		}
	""".trimIndent()

	advancement("silk_touch_nest") {
		criteria {
			beeNestDestroyed("silk_touch_nest", numBeesInside = rangeOrInt(3)) {
				blocks(Blocks.BEE_NEST)
				item {
					predicates {
						enchantments(enchantmentPredicate(Enchantments.SILK_TOUCH, levels = rangeOrIntStart(1)))
					}
				}
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"silk_touch_nest": {
					"trigger": "minecraft:bee_nest_destroyed",
					"conditions": {
						"blocks": "minecraft:bee_nest",
						"item": {
							"predicates": {
								"minecraft:enchantments": [
									{
										"enchantments": "minecraft:silk_touch",
										"levels": {
											"min": 1
										}
									}
								]
							}
						},
						"num_bees_inside": 3
					}
				}
			}
		}
	""".trimIndent()

	advancement("bee_nest_state") {
		criteria {
			beeNestDestroyed("bee_nest_state") {
				blocks(Blocks.BEE_NEST, Blocks.BEEHIVE)
				state("honey_level", "5")
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"bee_nest_state": {
					"trigger": "minecraft:bee_nest_destroyed",
					"conditions": {
						"blocks": [
							"minecraft:bee_nest",
							"minecraft:beehive"
						],
						"state": {
							"honey_level": "5"
						}
					}
				}
			}
		}
	""".trimIndent()

	advancement("bred_animals") {
		criteria {
			bredAnimals("bred_animals")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"bred_animals": {
					"trigger": "minecraft:bred_animals"
				}
			}
		}
	""".trimIndent()

	advancement("brewed_potion") {
		criteria {
			brewedPotion("brewed_potion")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"brewed_potion": {
					"trigger": "minecraft:brewed_potion"
				}
			}
		}
	""".trimIndent()

	advancement("brewed_swiftness") {
		criteria {
			brewedPotion("brewed_swiftness") {
				potion(Potions.SWIFTNESS)
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"brewed_swiftness": {
					"trigger": "minecraft:brewed_potion",
					"conditions": {
						"potion": {
							"potions": "minecraft:swiftness"
						}
					}
				}
			}
		}
	""".trimIndent()

	advancement("brewed_tag") {
		criteria {
			brewedPotion("brewed_tag") {
				potion(PotionTagArgument("tradeable", "my_pack"))
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"brewed_tag": {
					"trigger": "minecraft:brewed_potion",
					"conditions": {
						"potion": {
							"potions": "#my_pack:tradeable"
						}
					}
				}
			}
		}
	""".trimIndent()

	advancement("brewed_speed") {
		criteria {
			brewedPotion("brewed_speed") {
				potion {
					potions(Potions.SWIFTNESS, Potions.LONG_SWIFTNESS)
					effects {
						contains(mapOf(Effects.SPEED to mobEffectPredicate { amplifier = rangeOrInt(1) }))
					}
				}
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"brewed_speed": {
					"trigger": "minecraft:brewed_potion",
					"conditions": {
						"potion": {
							"effects": {
								"contains": [
									{
										"minecraft:speed": {
											"amplifier": 1
										}
									}
								]
							},
							"potions": [
								"minecraft:swiftness",
								"minecraft:long_swiftness"
							]
						}
					}
				}
			}
		}
	""".trimIndent()

	advancement("changed_dimension") {
		criteria {
			changedDimension("changed_dimension")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"changed_dimension": {
					"trigger": "minecraft:changed_dimension"
				}
			}
		}
	""".trimIndent()

	advancement("channeled_lightning") {
		criteria {
			channeledLightning("channeled_lightning")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"channeled_lightning": {
					"trigger": "minecraft:channeled_lightning"
				}
			}
		}
	""".trimIndent()

	advancement("construct_beacon") {
		criteria {
			constructBeacon("construct_beacon")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"construct_beacon": {
					"trigger": "minecraft:construct_beacon"
				}
			}
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

				condition {
					randomChance(0.5f)
					timeCheck(WorldClocks.OVERWORLD, 10..20)
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
						"player": {
							"type": "minecraft:all_of",
							"terms": [
								{
									"type": "minecraft:random_chance",
									"chance": 0.5
								},
								{
									"type": "minecraft:time_check",
									"clock": "minecraft:overworld",
									"value": {
										"max": 20,
										"min": 10
									}
								}
							]
						},
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

	advancement("crafter_recipe_crafted") {
		criteria {
			crafterRecipeCrafted(
				"crafter_recipe_crafted",
				Recipes.POLISHED_BLACKSTONE_BRICK_STAIRS_FROM_POLISHED_BLACKSTONE_BRICKS_STONECUTTING
			) {
				condition {
					randomChance(0.5f)
					timeCheck(WorldClocks.OVERWORLD, 10..20)
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
				"crafter_recipe_crafted": {
					"trigger": "minecraft:crafter_recipe_crafted",
					"conditions": {
						"player": {
							"type": "minecraft:all_of",
							"terms": [
								{
									"type": "minecraft:random_chance",
									"chance": 0.5
								},
								{
									"type": "minecraft:time_check",
									"clock": "minecraft:overworld",
									"value": {
										"max": 20,
										"min": 10
									}
								}
							]
						},
						"ingredients": [
							{
								"items": "minecraft:enchanted_golden_apple",
								"components": {
									"damage": 3
								}
							}
						],
						"recipes": "minecraft:polished_blackstone_brick_stairs_from_polished_blackstone_bricks_stonecutting"
					}
				}
			}
		}
	""".trimIndent()

	advancement("cured_zombie_villager") {
		criteria {
			curedZombieVillager("cured_zombie_villager")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"cured_zombie_villager": {
					"trigger": "minecraft:cured_zombie_villager"
				}
			}
		}
	""".trimIndent()

	advancement("default_block_use") {
		criteria {
			defaultBlockUse("default_block_use")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"default_block_use": {
					"trigger": "minecraft:default_block_use"
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
					condition {
						entityProperties {
							entityType(EntityTypes.WITCH)
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
						"source": {
							"type": "minecraft:entity_properties",
							"entity": "this",
							"predicate": {
								"minecraft:entity_type": "minecraft:witch"
							}
						}
					}
				}
			}
		}
	""".trimIndent()

	advancement("enchanted_item") {
		criteria {
			enchantedItem("enchanted_item")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"enchanted_item": {
					"trigger": "minecraft:enchanted_item"
				}
			}
		}
	""".trimIndent()

	advancement("enter_block") {
		criteria {
			enterBlock("enter_block") {
				blocks(Blocks.REDSTONE_LAMP)
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
						"blocks": "minecraft:redstone_lamp",
						"state": {
							"lit": "true"
						}
					}
				}
			}
		}
	""".trimIndent()

	advancement("enter_lava") {
		criteria {
			enterBlock("enter_lava") {
				blocks(Blocks.LAVA)
				state("level", "0")
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"enter_lava": {
					"trigger": "minecraft:enter_block",
					"conditions": {
						"blocks": "minecraft:lava",
						"state": {
							"level": "0"
						}
					}
				}
			}
		}
	""".trimIndent()

	advancement("enter_logs") {
		criteria {
			enterBlock("enter_logs") {
				blocks(Tags.Block.LOGS)
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"enter_logs": {
					"trigger": "minecraft:enter_block",
					"conditions": {
						"blocks": "#minecraft:logs"
					}
				}
			}
		}
	""".trimIndent()

	advancement("entity_hurt_player") {
		criteria {
			entityHurtPlayer("entity_hurt_player")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"entity_hurt_player": {
					"trigger": "minecraft:entity_hurt_player"
				}
			}
		}
	""".trimIndent()

	advancement("deflect_arrow") {
		criteria {
			entityHurtPlayer("deflected_projectile") {
				damage {
					blocked = true
					type {
						tag(Tags.DamageType.IS_PROJECTILE)
					}
				}
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"deflected_projectile": {
					"trigger": "minecraft:entity_hurt_player",
					"conditions": {
						"damage": {
							"blocked": true,
							"type": {
								"tags": [
									{
										"expected": true,
										"id": "#minecraft:is_projectile"
									}
								]
							}
						}
					}
				}
			}
		}
	""".trimIndent()

	advancement("damage_type_list") {
		criteria {
			entityHurtPlayer("not_fire") {
				damage {
					type {
						tag(DamageTypes.LAVA, DamageTypes.IN_FIRE, expected = false)
					}
				}
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"not_fire": {
					"trigger": "minecraft:entity_hurt_player",
					"conditions": {
						"damage": {
							"type": {
								"tags": [
									{
										"expected": false,
										"id": [
											"minecraft:lava",
											"minecraft:in_fire"
										]
									}
								]
							}
						}
					}
				}
			}
		}
	""".trimIndent()

	advancement("entity_killed_player") {
		criteria {
			entityKilledPlayer("entity_killed_player")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"entity_killed_player": {
					"trigger": "minecraft:entity_killed_player"
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

					light(10)
				}

				cause {
					condition {
						entityProperties {
							entityType(EntityTypes.PLAYER)
						}
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
							"light": {
								"light": 10
							}
						},
						"distance": {
							"absolute": 10.0,
							"x": {
								"min": 10.0,
								"max": 20.0
							}
						},
						"cause": {
							"type": "minecraft:entity_properties",
							"entity": "this",
							"predicate": {
								"minecraft:entity_type": "minecraft:player"
							}
						}
					}
				}
			}
		}
	""".trimIndent()

	advancement("fall_from_height") {
		criteria {
			fallFromHeight("fall_from_height")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"fall_from_height": {
					"trigger": "minecraft:fall_from_height"
				}
			}
		}
	""".trimIndent()

	advancement("filled_bucket") {
		criteria {
			filledBucket("filled_bucket")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"filled_bucket": {
					"trigger": "minecraft:filled_bucket"
				}
			}
		}
	""".trimIndent()

	advancement("fishing_rod_hooked") {
		criteria {
			fishingRodHooked("fishing_rod_hooked")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"fishing_rod_hooked": {
					"trigger": "minecraft:fishing_rod_hooked"
				}
			}
		}
	""".trimIndent()

	advancement("hero_of_the_village") {
		criteria {
			heroOfTheVillage("hero_of_the_village")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"hero_of_the_village": {
					"trigger": "minecraft:hero_of_the_village"
				}
			}
		}
	""".trimIndent()

	advancement("impossible") {
		criteria {
			impossible("impossible")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"impossible": {
					"trigger": "minecraft:impossible"
				}
			}
		}
	""".trimIndent()

	advancement("inventory_changed") {
		criteria {
			inventoryChanged("inventory_changed")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"inventory_changed": {
					"trigger": "minecraft:inventory_changed"
				}
			}
		}
	""".trimIndent()

	advancement("item_durability_changed") {
		criteria {
			itemDurabilityChanged("item_durability_changed")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"item_durability_changed": {
					"trigger": "minecraft:item_durability_changed"
				}
			}
		}
	""".trimIndent()

	advancement("item_used_on_block") {
		criteria {
			itemUsedOnBlock("item_used_on_block")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"item_used_on_block": {
					"trigger": "minecraft:item_used_on_block"
				}
			}
		}
	""".trimIndent()

	advancement("killed_by_arrow") {
		criteria {
			killedByArrow("killed_by_arrow") {
				firedFromWeapon {
					items = listOf(Items.BOW)
					components {
						enchantments {
							enchantment(Enchantments.POWER, 5)
						}
					}
				}

				uniqueEntityTypes = rangeOrInt(1..5)

				victim {
					condition {
						entityProperties {
							entityType(EntityTypes.PLAYER)
						}
					}
				}
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"killed_by_arrow": {
					"trigger": "minecraft:killed_by_arrow",
					"conditions": {
						"fired_from_weapon": {
							"items": "minecraft:bow",
							"components": {
								"enchantments": {
									"minecraft:power": 5
								}
							}
						},
						"unique_entity_types": {
							"min": 1,
							"max": 5
						},
						"victims": [
							{
								"type": "minecraft:entity_properties",
								"entity": "this",
								"predicate": {
									"minecraft:entity_type": "minecraft:player"
								}
							}
						]
					}
				}
			}
		}
	""".trimIndent()

	advancement("kill_mob_near_sculk_catalyst") {
		criteria {
			killMobNearSculkCatalyst("kill_mob_near_sculk_catalyst")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"kill_mob_near_sculk_catalyst": {
					"trigger": "minecraft:kill_mob_near_sculk_catalyst"
				}
			}
		}
	""".trimIndent()

	advancement("levitation") {
		criteria {
			levitation("levitation")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"levitation": {
					"trigger": "minecraft:levitation"
				}
			}
		}
	""".trimIndent()

	advancement("lightning_strike") {
		criteria {
			lightningStrike("lightning_strike")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"lightning_strike": {
					"trigger": "minecraft:lightning_strike"
				}
			}
		}
	""".trimIndent()

	advancement("location") {
		criteria {
			location("location")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"location": {
					"trigger": "minecraft:location"
				}
			}
		}
	""".trimIndent()

	advancement("nether_travel") {
		criteria {
			netherTravel("nether_travel")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"nether_travel": {
					"trigger": "minecraft:nether_travel"
				}
			}
		}
	""".trimIndent()

	advancement("placed_block") {
		criteria {
			placedBlock("placed_block")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"placed_block": {
					"trigger": "minecraft:placed_block"
				}
			}
		}
	""".trimIndent()

	advancement("player_generates_container_loot") {
		criteria {
			playerGeneratesContainerLoot("player_generates_container_loot", LootTables.Chests.IGLOO_CHEST) {}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"player_generates_container_loot": {
					"trigger": "minecraft:player_generates_container_loot",
					"conditions": {
						"loot_tables": "minecraft:chests/igloo_chest"
					}
				}
			}
		}
	""".trimIndent()

	advancement("generates_loot_list") {
		criteria {
			playerGeneratesContainerLoot(
				"generates_loot_list",
				LootTables.Archaeology.DESERT_PYRAMID,
				LootTables.Archaeology.DESERT_WELL,
			) {}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"generates_loot_list": {
					"trigger": "minecraft:player_generates_container_loot",
					"conditions": {
						"loot_tables": [
							"minecraft:archaeology/desert_pyramid",
							"minecraft:archaeology/desert_well"
						]
					}
				}
			}
		}
	""".trimIndent()

	advancement("generates_loot_tag") {
		criteria {
			playerGeneratesContainerLoot("generates_loot_tag", LootTableTagArgument("chests", "my_pack")) {}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"generates_loot_tag": {
					"trigger": "minecraft:player_generates_container_loot",
					"conditions": {
						"loot_tables": "#my_pack:chests"
					}
				}
			}
		}
	""".trimIndent()

	advancement("player_hurt_entity") {
		criteria {
			playerHurtEntity("player_hurt_entity")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"player_hurt_entity": {
					"trigger": "minecraft:player_hurt_entity"
				}
			}
		}
	""".trimIndent()

	advancement("player_interacted_with_entity") {
		criteria {
			playerInteractedWithEntity("example") {
				item {
					items = listOf(Items.IRON_INGOT)
				}
				entity {
					conditionEntity {
						entityType(EntityTypes.IRON_GOLEM)
					}
				}
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"example": {
					"trigger": "minecraft:player_interacted_with_entity",
					"conditions": {
						"item": {
							"items": "minecraft:iron_ingot"
						},
						"entity": {
							"type": "minecraft:entity_properties",
							"entity": "this",
							"predicate": {
								"minecraft:entity_type": "minecraft:iron_golem"
							}
						}
					}
				}
			}
		}
	""".trimIndent()

	advancement("player_killed_entity") {
		criteria {
			playerKilledEntity("player_killed_entity")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"player_killed_entity": {
					"trigger": "minecraft:player_killed_entity"
				}
			}
		}
	""".trimIndent()

	advancement("player_sheared_equipment") {
		criteria {
			playerShearedEquipment("player_sheared_equipment") {
				entity {
					entityType(EntityTypes.SHEEP)
				}
				item {
					items = listOf(Items.WHITE_WOOL)
				}
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"player_sheared_equipment": {
					"trigger": "minecraft:player_sheared_equipment",
					"conditions": {
						"entity": {
							"minecraft:entity_type": "minecraft:sheep"
						},
						"item": {
							"items": "minecraft:white_wool"
						}
					}
				}
			}
		}
	""".trimIndent()

	advancement("recipe_crafted") {
		criteria {
			recipeCrafted(
				"recipe_crafted",
				Recipes.POLISHED_BLACKSTONE_BRICK_STAIRS_FROM_POLISHED_BLACKSTONE_BRICKS_STONECUTTING
			)
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"recipe_crafted": {
					"trigger": "minecraft:recipe_crafted",
					"conditions": {
						"recipes": "minecraft:polished_blackstone_brick_stairs_from_polished_blackstone_bricks_stonecutting"
					}
				}
			}
		}
	""".trimIndent()

	advancement("recipe_crafted_two") {
		criteria {
			recipeCrafted("recipe_crafted_two", Recipes.DIAMOND_PICKAXE, Recipes.DIAMOND_AXE) {
				ingredient { items = listOf(Items.DIAMOND) }
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"recipe_crafted_two": {
					"trigger": "minecraft:recipe_crafted",
					"conditions": {
						"ingredients": [
							{
								"items": "minecraft:diamond"
							}
						],
						"recipes": [
							"minecraft:diamond_pickaxe",
							"minecraft:diamond_axe"
						]
					}
				}
			}
		}
	""".trimIndent()

	advancement("recipe_unlocked") {
		criteria {
			recipeUnlocked(
				"recipe_unlocked",
				Recipes.POLISHED_BLACKSTONE_BRICK_STAIRS_FROM_POLISHED_BLACKSTONE_BRICKS_STONECUTTING
			)
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"recipe_unlocked": {
					"trigger": "minecraft:recipe_unlocked",
					"conditions": {
						"recipes": "minecraft:polished_blackstone_brick_stairs_from_polished_blackstone_bricks_stonecutting"
					}
				}
			}
		}
	""".trimIndent()

	advancement("recipe_unlocked_two") {
		criteria {
			recipeUnlocked("recipe_unlocked_two", Recipes.OAK_BOAT, Recipes.BIRCH_BOAT)
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"recipe_unlocked_two": {
					"trigger": "minecraft:recipe_unlocked",
					"conditions": {
						"recipes": [
							"minecraft:oak_boat",
							"minecraft:birch_boat"
						]
					}
				}
			}
		}
	""".trimIndent()

	advancement("reward_loot_list") {
		criteria {
			recipeUnlocked("has_the_recipe", Recipes.OAK_BOAT)
		}
		rewards {
			loots(LootTables.Chests.IGLOO_CHEST, LootTableTagArgument("chests", "my_pack"))
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"has_the_recipe": {
					"trigger": "minecraft:recipe_unlocked",
					"conditions": {
						"recipes": "minecraft:oak_boat"
					}
				}
			},
			"rewards": {
				"loot": [
					"minecraft:chests/igloo_chest",
					"#my_pack:chests"
				]
			}
		}
	""".trimIndent()

	advancement("has_the_recipe") {
		criteria {
			recipeUnlocked("has_the_recipe", Recipes.OAK_BOAT)
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"has_the_recipe": {
					"trigger": "minecraft:recipe_unlocked",
					"conditions": {
						"recipes": "minecraft:oak_boat"
					}
				}
			}
		}
	""".trimIndent()

	advancement("ride_entity_in_lava") {
		criteria {
			rideEntityInLava("ride_entity_in_lava")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"ride_entity_in_lava": {
					"trigger": "minecraft:ride_entity_in_lava"
				}
			}
		}
	""".trimIndent()

	advancement("shot_crossbow") {
		criteria {
			shotCrossbow("shot_crossbow")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"shot_crossbow": {
					"trigger": "minecraft:shot_crossbow"
				}
			}
		}
	""".trimIndent()

	advancement("slept_in_bed") {
		criteria {
			sleptInBed("slept_in_bed")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"slept_in_bed": {
					"trigger": "minecraft:slept_in_bed"
				}
			}
		}
	""".trimIndent()

	advancement("slide_down_block") {
		criteria {
			slideDownBlock("slide_down_block") {}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"slide_down_block": {
					"trigger": "minecraft:slide_down_block"
				}
			}
		}
	""".trimIndent()

	advancement("honey_block_slide") {
		criteria {
			slideDownBlock("honey_block_slide") {
				blocks(Blocks.HONEY_BLOCK)
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"honey_block_slide": {
					"trigger": "minecraft:slide_down_block",
					"conditions": {
						"blocks": "minecraft:honey_block"
					}
				}
			}
		}
	""".trimIndent()

	advancement("slide_down_state") {
		criteria {
			slideDownBlock("slide_down_state") {
				blocks(Blocks.HONEY_BLOCK)
				state("facing", "north")
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"slide_down_state": {
					"trigger": "minecraft:slide_down_block",
					"conditions": {
						"blocks": "minecraft:honey_block",
						"state": {
							"facing": "north"
						}
					}
				}
			}
		}
	""".trimIndent()

	advancement("spear_mobs") {
		criteria {
			spearMobs("spear_mobs") {
				count = 3
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"spear_mobs": {
					"trigger": "minecraft:spear_mobs",
					"conditions": {
						"count": 3
					}
				}
			}
		}
	""".trimIndent()

	advancement("started_riding") {
		criteria {
			startedRiding("started_riding")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"started_riding": {
					"trigger": "minecraft:started_riding"
				}
			}
		}
	""".trimIndent()

	advancement("summoned_entity") {
		criteria {
			summonedEntity("summoned_entity")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"summoned_entity": {
					"trigger": "minecraft:summoned_entity"
				}
			}
		}
	""".trimIndent()

	advancement("tame_animal") {
		criteria {
			tameAnimal("tame_animal")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"tame_animal": {
					"trigger": "minecraft:tame_animal"
				}
			}
		}
	""".trimIndent()

	advancement("target_hit") {
		criteria {
			targetHit("target_hit")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"target_hit": {
					"trigger": "minecraft:target_hit"
				}
			}
		}
	""".trimIndent()

	advancement("thrown_item_picked_up_by_entity") {
		criteria {
			thrownItemPickedUpByEntity("thrown_item_picked_up_by_entity")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"thrown_item_picked_up_by_entity": {
					"trigger": "minecraft:thrown_item_picked_up_by_entity"
				}
			}
		}
	""".trimIndent()

	advancement("thrown_item_picked_up_by_player") {
		criteria {
			thrownItemPickedUpByPlayer("thrown_item_picked_up_by_player")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"thrown_item_picked_up_by_player": {
					"trigger": "minecraft:thrown_item_picked_up_by_player"
				}
			}
		}
	""".trimIndent()

	advancement("tick") {
		criteria {
			tick("tick")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"tick": {
					"trigger": "minecraft:tick"
				}
			}
		}
	""".trimIndent()

	advancement("used_ender_eye") {
		criteria {
			usedEnderEye("used_ender_eye")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"used_ender_eye": {
					"trigger": "minecraft:used_ender_eye"
				}
			}
		}
	""".trimIndent()

	advancement("used_totem") {
		criteria {
			usedTotem("used_totem")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"used_totem": {
					"trigger": "minecraft:used_totem"
				}
			}
		}
	""".trimIndent()

	advancement("using_item") {
		criteria {
			usingItem("using_item")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"using_item": {
					"trigger": "minecraft:using_item"
				}
			}
		}
	""".trimIndent()

	advancement("villager_trade") {
		criteria {
			villagerTrade("villager_trade")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"villager_trade": {
					"trigger": "minecraft:villager_trade"
				}
			}
		}
	""".trimIndent()

	advancement("voluntary_exile") {
		criteria {
			voluntaryExile("voluntary_exile")
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"voluntary_exile": {
					"trigger": "minecraft:voluntary_exile"
				}
			}
		}
	""".trimIndent()

	advancement("round_trip_simple_advancement") {
		display(Items.DIAMOND_SWORD, "Legendary Sword", "A powerful enchanted blade") {
			frame = AdvancementFrameType.CHALLENGE
			background = Textures.Gui.Advancements.Backgrounds.STONE
			showToast = true
			announceToChat = true
		}

		criteria {
			impossible("impossible")
		}
	}
	roundTrip(advancements.last())
}

private fun DataPack.componentsDisplay() {
	advancement("item_with_components_display") {
		display(Items.DIAMOND_SWORD, "Legendary Sword", "A powerful enchanted blade") {
			icon(Items.DIAMOND_SWORD) {
				// Test multiple components
				enchantments {
					enchantment(Enchantments.SHARPNESS, 5)
					enchantment(Enchantments.UNBREAKING, 3)
				}

				attributeModifiers {
					modifier(
						type = Attributes.ATTACK_DAMAGE,
						amount = 10.0,
						name = "extra_damage",
						operation = AttributeModifierOperation.ADD_VALUE
					)
				}

				customName(textComponent("Legendary Diamond Sword", Color.GOLD))

				lore(textComponent("A blade of immense power", Color.GOLD))
			}

			frame = AdvancementFrameType.CHALLENGE
			background = Textures.Gui.Advancements.Backgrounds.STONE
			showToast = true
			announceToChat = true
		}

		criteria {
			impossible("impossible")
		}
	}

	advancements.last() assertsIs """
		{
			"display": {
				"icon": {
					"id": "minecraft:diamond_sword",
					"components": {
						"enchantments": {
							"minecraft:sharpness": 5,
							"minecraft:unbreaking": 3
						},
						"attribute_modifiers": [
							{
								"type": "minecraft:attack_damage",
								"id": "minecraft:extra_damage",
								"amount": 10.0,
								"operation": "add_value"
							}
						],
						"custom_name": {
							"text": "Legendary Diamond Sword",
							"color": "gold"
						},
						"lore": [
							{
								"type": "text",
								"color": "gold",
								"text": "A blade of immense power"
							}
						]
					}
				},
				"title": "Legendary Sword",
				"description": "A powerful enchanted blade",
				"frame": "challenge",
				"background": "minecraft:gui/advancements/backgrounds/stone",
				"show_toast": true,
				"announce_to_chat": true
			},
			"criteria": {
				"impossible": {
					"trigger": "minecraft:impossible"
				}
			}
		}
	""".trimIndent()
}

private fun DataPack.triggerPredicateFieldsTests() {
	advancement("very_very_frightening") {
		criteria {
			channeledLightning("struck_villager") {
				victim {
					conditionEntity {
						entityType(EntityTypes.VILLAGER)
					}
				}
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"struck_villager": {
					"trigger": "minecraft:channeled_lightning",
					"conditions": {
						"victims": [
							{
								"type": "minecraft:entity_properties",
								"entity": "this",
								"predicate": {
									"minecraft:entity_type": "minecraft:villager"
								}
							}
						]
					}
				}
			}
		}
	""".trimIndent()

	advancement("lightning_rod_with_villager_no_fire") {
		criteria {
			lightningStrike("strike") {
				bystander {
					conditionEntity {
						entityType(EntityTypes.VILLAGER)
					}
				}
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"strike": {
					"trigger": "minecraft:lightning_strike",
					"conditions": {
						"bystander": {
							"type": "minecraft:entity_properties",
							"entity": "this",
							"predicate": {
								"minecraft:entity_type": "minecraft:villager"
							}
						}
					}
				}
			}
		}
	""".trimIndent()

	advancement("victim_ids") {
		criteria {
			channeledLightning("struck") {
				victim(PredicateArgument("is_villager", "my_pack"))
				victim {
					conditionEntity {
						entityType(EntityTypes.VILLAGER)
					}
				}
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"struck": {
					"trigger": "minecraft:channeled_lightning",
					"conditions": {
						"victims": [
							"my_pack:is_villager",
							{
								"type": "minecraft:entity_properties",
								"entity": "this",
								"predicate": {
									"minecraft:entity_type": "minecraft:villager"
								}
							}
						]
					}
				}
			}
		}
	""".trimIndent()
	roundTrip(advancements.last())

	advancement("player_id") {
		criteria {
			consumeItem("consumed") {
				condition(PredicateArgument("is_hungry", "my_pack"))
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"consumed": {
					"trigger": "minecraft:consume_item",
					"conditions": {
						"player": "my_pack:is_hungry"
					}
				}
			}
		}
	""".trimIndent()
	roundTrip(advancements.last())

	advancement("player_entity_properties") {
		criteria {
			consumeItem("consumed") {
				conditionEntity {
					entityType(EntityTypes.PLAYER)
				}
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"consumed": {
					"trigger": "minecraft:consume_item",
					"conditions": {
						"player": {
							"type": "minecraft:entity_properties",
							"entity": "this",
							"predicate": {
								"minecraft:entity_type": "minecraft:player"
							}
						}
					}
				}
			}
		}
	""".trimIndent()

	advancement("entity_id") {
		criteria {
			bredAnimals("bred") {
				parent {
					condition(PredicateArgument("is_cow", "my_pack"))
				}
				child {
					condition {
						randomChance(0.5f)
						randomChance(0.25f)
					}
				}
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"bred": {
					"trigger": "minecraft:bred_animals",
					"conditions": {
						"child": {
							"type": "minecraft:all_of",
							"terms": [
								{
									"type": "minecraft:random_chance",
									"chance": 0.5
								},
								{
									"type": "minecraft:random_chance",
									"chance": 0.25
								}
							]
						},
						"parent": "my_pack:is_cow"
					}
				}
			}
		}
	""".trimIndent()
	roundTrip(advancements.last())

	advancement("location_id") {
		criteria {
			anyBlockUse("any") {
				location {
					predicate(PredicateArgument("is_farm", "my_pack"))
				}
			}
			placedBlock("placed") {
				location {
					location {
						biomes(Biomes.PLAINS)
					}
				}
			}
			allayDropItemOnBlock("allay") {
				predicate {
					randomChance(0.5f)
					randomChance(0.25f)
				}
			}
		}
	}
	advancements.last() assertsIs """
		{
			"criteria": {
				"any": {
					"trigger": "minecraft:any_block_use",
					"conditions": {
						"location": "my_pack:is_farm"
					}
				},
				"placed": {
					"trigger": "minecraft:placed_block",
					"conditions": {
						"location": {
							"type": "minecraft:location_check",
							"predicate": {
								"biomes": "minecraft:plains"
							}
						}
					}
				},
				"allay": {
					"trigger": "minecraft:allay_drop_item_on_block",
					"conditions": {
						"location": {
							"type": "minecraft:all_of",
							"terms": [
								{
									"type": "minecraft:random_chance",
									"chance": 0.5
								},
								{
									"type": "minecraft:random_chance",
									"chance": 0.25
								}
							]
						}
					}
				}
			}
		}
	""".trimIndent()
	roundTrip(advancements.last())
}

class AdvancementTests : FunSpec({
	test("advancement") {
		dataPack("features_tests") {
			pretty()
			advancementTests()
		}
	}

	test("root advancement with a display needs a background") {
		dataPack("features_tests") {
			advancement("root_without_background") {
				display(Items.DIAMOND_SWORD, "Hello", "World")
			}

			shouldThrow<IllegalArgumentException> { advancements.last().generateJson(this) }
		}
	}

	test("child advancement can't set a background") {
		dataPack("features_tests") {
			advancement("child_with_background") {
				parent = Advancements.Story.ROOT
				display(Items.DIAMOND_SWORD, "Hello", "World") {
					background = Textures.Gui.Advancements.Backgrounds.STONE
				}
			}

			shouldThrow<IllegalArgumentException> { advancements.last().generateJson(this) }
		}
	}

	test("trigger predicate fields") {
		dataPack("features_tests") {
			pretty()
			triggerPredicateFieldsTests()
		}
	}
})
