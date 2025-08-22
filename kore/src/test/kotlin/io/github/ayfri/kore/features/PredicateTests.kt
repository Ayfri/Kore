package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.WEAPON
import io.github.ayfri.kore.arguments.components.entity.axolotlVariant
import io.github.ayfri.kore.arguments.components.item.damage
import io.github.ayfri.kore.arguments.components.item.unbreakable
import io.github.ayfri.kore.arguments.components.matchers.enchantments
import io.github.ayfri.kore.arguments.enums.AxolotlVariants
import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.features.predicates.conditions.*
import io.github.ayfri.kore.features.predicates.predicate
import io.github.ayfri.kore.features.predicates.sub.*
import io.github.ayfri.kore.features.predicates.sub.entityspecific.gamemodes
import io.github.ayfri.kore.features.predicates.sub.entityspecific.playerTypeSpecific
import io.github.ayfri.kore.features.predicates.sub.item.enchantment
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.*
import io.github.ayfri.kore.utils.set

fun DataPack.predicateTests() {
	val myPredicate = predicate("test1") {
		matchTool {
			item(Items.DIAMOND_PICKAXE)
		}

		allOf {
			enchantmentActiveCheck(true)
			randomChance(0.5f)
			randomChanceWithEnchantedBonus(unenchantedChance = 3f, enchantedChance = 2, Enchantments.EFFICIENCY)
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
						"condition": "minecraft:enchantment_active_check",
						"active": true
					},
					{
						"condition": "minecraft:random_chance",
						"chance": 0.5
					},
					{
						"condition": "minecraft:random_chance_with_enchanted_bonus",
						"unenchanted_chance": 3.0,
						"enchanted_chance": 2,
						"enchantment": "minecraft:efficiency"
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

	predicate("test2") {
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

			slots {
				this[WEAPON.MAINHAND] = itemStack(Items.DIAMOND_SWORD)
			}

			steppingOn {
				blocks(Blocks.STONE)
			}

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
				"slots": {
					"weapon.mainhand": {
						"items": "minecraft:diamond_sword"
					}
				},
				"stepping_on": {
					"blocks": "minecraft:stone"
				},
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

	predicate("test3") {
		matchTool {
			item(Items.DIAMOND_PICKAXE)
			predicates {
				enchantments(enchantment(Enchantments.EFFICIENCY))
			}
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:match_tool",
			"predicate": {
				"items": "minecraft:diamond_pickaxe",
				"predicates": {
					"minecraft:enchantments": [
						{
							"enchantments": "minecraft:efficiency"
						}
					]
				}
			}
		}
	""".trimIndent()
}
