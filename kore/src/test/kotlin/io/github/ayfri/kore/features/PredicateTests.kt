package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.WEAPON
import io.github.ayfri.kore.arguments.components.entity.axolotlVariant
import io.github.ayfri.kore.arguments.components.item.damage
import io.github.ayfri.kore.arguments.components.item.unbreakable
import io.github.ayfri.kore.arguments.components.matchers.customData
import io.github.ayfri.kore.arguments.components.matchers.enchantments
import io.github.ayfri.kore.arguments.enums.AxolotlVariants
import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.predicates.conditions.*
import io.github.ayfri.kore.features.predicates.predicate
import io.github.ayfri.kore.features.predicates.providers.int
import io.github.ayfri.kore.features.predicates.providers.intRange
import io.github.ayfri.kore.features.predicates.providers.scoreNumber
import io.github.ayfri.kore.features.predicates.sub.*
import io.github.ayfri.kore.features.predicates.sub.entityspecific.gamemodes
import io.github.ayfri.kore.features.predicates.sub.entityspecific.playerTypeSpecific
import io.github.ayfri.kore.features.predicates.sub.item.enchantment
import io.github.ayfri.kore.features.predicates.types.EntityType
import io.github.ayfri.kore.generated.*
import io.github.ayfri.kore.utils.set

fun DataPack.predicateTests() {
	predicate("all_of") {
		allOf {
			enchantmentActiveCheck(false)
			randomChance(0.25f)
			timeCheck(10f..20f)
			weatherCheck(raining = false, thundering = true)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:all_of",
			"terms": [
				{
					"condition": "minecraft:enchantment_active_check",
					"active": false
				},
				{
					"condition": "minecraft:random_chance",
					"chance": 0.25
				},
				{
					"condition": "minecraft:time_check",
					"value": {
						"min": 10.0,
						"max": 20.0
					}
				},
				{
					"condition": "minecraft:weather_check",
					"raining": false,
					"thundering": true
				}
			]
		}
	""".trimIndent()

	predicate("any_of") {
		anyOf {
			killedByPlayer(true)
			survivesExplosion()
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:any_of",
			"terms": [
				{
					"condition": "minecraft:killed_by_player",
					"inverse": true
				},
				{
					"condition": "minecraft:survives_explosion"
				}
			]
		}
	""".trimIndent()

	predicate("block_state_property") {
		blockStateProperty(Blocks.REDSTONE_LAMP) {
			this["facing"] = "north"
			this["lit"] = "true"
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:block_state_property",
			"block": "minecraft:redstone_lamp",
			"properties": {
				"facing": "north",
				"lit": "true"
			}
		}
	""".trimIndent()

	predicate("damage_source_properties") {
		damageSourceProperties {
			isDirect = true
			directEntity = entity {
				type(EntityTypes.ZOMBIE)
			}
			sourceEntity = entity {
				type(EntityTypes.SKELETON)
			}
			tag(Tags.DamageType.IS_PROJECTILE, expected = true)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:damage_source_properties",
			"predicate": {
				"direct_entity": {
					"type": "minecraft:zombie"
				},
				"is_direct": true,
				"source_entity": {
					"type": "minecraft:skeleton"
				},
				"tags": [
					{
						"id": "#minecraft:is_projectile",
						"expected": true
					}
				]
			}
		}
	""".trimIndent()

	predicate("enchantment_active_check") {
		enchantmentActiveCheck(true)
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:enchantment_active_check",
			"active": true
		}
	""".trimIndent()

	predicate("entity_properties") {
		entityProperties {
			components {
				axolotlVariant(AxolotlVariants.CYAN)
				damage(12)
				!unbreakable()
			}

			effects {
				this[Effects.INVISIBILITY] = effect {
					amplifier = rangeOrInt(1)
				}
			}

			equipment {
				mainHand = itemStack(Items.DIAMOND_SWORD)
			}

			flags {
				isBaby = true
			}

			location {
				block {
					blocks(Blocks.STONE)
				}
			}

			movement {
				x(1.0, 4.0)
				horizontalSpeed(1.0)
			}

			movementAffectedBy {
				canSeeSky = true
			}

			nbt {
				this["foo"] = "bar"
			}

			passenger {
				team = "foo"
			}

			periodicTicks = 20

			predicates {
				customData {
					this["foo"] = "bar"
				}
			}

			slots {
				this[WEAPON.MAINHAND] = itemStack(Items.DIAMOND_SWORD)
			}

			steppingOn {
				block {
					blocks(Blocks.STONE)
					components {
						damage(5)
					}
					predicates {
						customData {
							this["foo"] = "bar"
						}
					}
					state("up", "bottom")
				}
			}

			team = "alpha"
			type(EntityTypes.MARKER)

			playerTypeSpecific {
				gamemodes(Gamemode.SURVIVAL)
			}

			vehicle {
				distance {
					x(1f..4f)
					z(1f)
				}
			}
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"components": {
					"axolotl_variant": "cyan",
					"damage": 12,
					"!unbreakable": {}
				},
				"effects": {
					"minecraft:invisibility": {
						"amplifier": 1
					}
				},
				"equipment": {
					"mainhand": {
						"items": "minecraft:diamond_sword"
					}
				},
				"flags": {
					"is_baby": true
				},
				"location": {
					"block": {
						"blocks": "minecraft:stone"
					}
				},
				"movement": {
					"x": {
						"min": 1.0,
						"max": 4.0
					},
					"horizontal_speed": 1.0
				},
				"movement_affected_by": {
					"can_see_sky": true
				},
				"nbt": {
					"foo": "bar"
				},
				"passenger": {
					"team": "foo"
				},
				"periodic_ticks": 20,
				"predicates": {
					"minecraft:custom_data": {
						"foo": "bar"
					}
				},
				"slots": {
					"weapon.mainhand": {
						"items": "minecraft:diamond_sword"
					}
				},
				"stepping_on": {
					"block": {
						"blocks": "minecraft:stone",
						"components": {
							"damage": 5
						},
						"predicates": {
							"minecraft:custom_data": {
								"foo": "bar"
							}
						},
						"state": {
							"up": "bottom"
						}
					}
				},
				"team": "alpha",
				"type": "minecraft:marker",
				"type_specific": {
					"type": "minecraft:player",
					"gamemode": [
						"survival"
					]
				},
				"vehicle": {
					"distance": {
						"x": {
							"min": 1.0,
							"max": 4.0
						},
						"z": {
							"min": 1.0,
							"max": 1.0
						}
					}
				}
			}
		}
	""".trimIndent()

	predicate("entity_score") {
		entityScores(EntityType.THIS) {
			this["kills"] = intRange(1f, 5f)
			this["deaths"] = int(2)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_scores",
			"entity": "this",
			"scores": {
				"kills": {
					"min": 1.0,
					"max": 5.0
				},
				"deaths": 2
			}
		}
	""".trimIndent()

	predicate("inverted") {
		inverted {
			randomChance(0.1f)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:inverted",
			"term": {
				"condition": "minecraft:random_chance",
				"chance": 0.1
			}
		}
	""".trimIndent()

	predicate("killed_by_player") {
		killedByPlayer(true)
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:killed_by_player",
			"inverse": true
		}
	""".trimIndent()

	predicate("location_check") {
		locationCheck(offsetX = 1, offsetY = 2, offsetZ = 3) {
			biomes(Biomes.PLAINS, Biomes.SAVANNA)
			block {
				blocks(Blocks.GOLD_BLOCK)
				states {
					this["lit"] = "true"
				}
			}
			canSeeSky = true
			dimension = Dimensions.OVERWORLD
			fluids(Fluids.WATER) {
				states {
					this["level"] = "0"
				}
			}
			light(7)
			position {
				x = rangeOrInt(1)
				y = rangeOrInt(2..4)
				z = rangeOrInt(3)
			}
			smokey = true
			weather = Weather.RAINING
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:location_check",
			"offset_x": 1,
			"offset_y": 2,
			"offset_z": 3,
			"predicate": {
				"biomes": [
					"minecraft:plains",
					"minecraft:savanna"
				],
				"block": {
					"blocks": "minecraft:gold_block",
					"state": {
						"lit": "true"
					}
				},
				"can_see_sky": true,
				"dimension": "minecraft:overworld",
				"fluid": {
					"fluids": "minecraft:water",
					"state": {
						"level": "0"
					}
				},
				"light": {
					"light": 7
				},
				"position": {
					"x": 1,
					"y": {
						"min": 2,
						"max": 4
					},
					"z": 3
				},
				"smokey": true,
				"weather": "raining"
			}
		}
	""".trimIndent()

	predicate("match_tool") {
		matchTool {
			item(Items.DIAMOND_PICKAXE, Items.IRON_PICKAXE)
			components {
				damage(5)
				unbreakable()
			}
			predicates {
				enchantments(
					enchantment(Enchantments.EFFICIENCY, levels = rangeOrInt(3)),
					enchantment(Enchantments.UNBREAKING, levels = rangeOrInt(2))
				)
			}
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:match_tool",
			"predicate": {
				"items": [
					"minecraft:diamond_pickaxe",
					"minecraft:iron_pickaxe"
				],
				"components": {
					"damage": 5,
					"unbreakable": {}
				},
				"predicates": {
					"minecraft:enchantments": [
						{
							"enchantments": "minecraft:efficiency",
							"levels": 3
						},
						{
							"enchantments": "minecraft:unbreaking",
							"levels": 2
						}
					]
				}
			}
		}
	""".trimIndent()

	predicate("random_chance") {
		randomChance(0.4f)
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:random_chance",
			"chance": 0.4
		}
	""".trimIndent()

	predicate("random_chance_with_enchanted_bonus") {
		randomChanceWithEnchantedBonus(unenchantedChance = 1f, enchantedChance = 2, Enchantments.FORTUNE)
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:random_chance_with_enchanted_bonus",
			"unenchanted_chance": 1.0,
			"enchanted_chance": 2,
			"enchantment": "minecraft:fortune"
		}
	""".trimIndent()

	predicate("reference") {
		reference("kore:test")
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:reference",
			"name": "kore:test"
		}
	""".trimIndent()

	predicate("survives_explosion") {
		survivesExplosion()
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:survives_explosion"
		}
	""".trimIndent()

	predicate("table_bonus") {
		tableBonus(Enchantments.LOOTING, 0.1f, 0.25f, 0.5f)
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:table_bonus",
			"enchantment": "minecraft:looting",
			"chances": [
				0.1,
				0.25,
				0.5
			]
		}
	""".trimIndent()

	predicate("time_check") {
		timeCheck(5f..15f, period = 24000)
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:time_check",
			"value": {
				"min": 5.0,
				"max": 15.0
			},
			"period": 24000
		}
	""".trimIndent()

	predicate("value_check") {
		valueCheck(scoreNumber("kills", target = EntityType.THIS), intRange(0f, 10f))
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:value_check",
			"value": {
				"type": "minecraft:score",
				"target": {
					"type": "minecraft:context",
					"target": "this"
				},
				"score": "kills"
			},
			"range": {
				"min": 0.0,
				"max": 10.0
			}
		}
	""".trimIndent()

	predicate("weather_check") {
		weatherCheck(raining = true, thundering = false)
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:weather_check",
			"raining": true,
			"thundering": false
		}
	""".trimIndent()
}
