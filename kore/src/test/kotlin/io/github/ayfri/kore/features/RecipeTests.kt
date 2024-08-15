package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.components.types.damage
import io.github.ayfri.kore.arguments.components.types.enchantment
import io.github.ayfri.kore.arguments.components.types.enchantments
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.recipeGive
import io.github.ayfri.kore.data.item.builders.itemStack
import io.github.ayfri.kore.features.recipes.recipes
import io.github.ayfri.kore.features.recipes.recipesBuilder
import io.github.ayfri.kore.features.recipes.types.*
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.Enchantments
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.Tags

fun DataPack.recipeTest() {
	recipes {
		blasting("test_blasting") {
			ingredient {
				tag = Tags.Item.STONE_CRAFTING_MATERIALS
			}

			result = itemStack(Items.DIAMOND) {
				damage(4)
			}
			experience = 10.0
			cookingTime = 2000
		}

		craftingShaped("diamond_drows") {
			pattern(
				" S ",
				" S ",
				" D "
			)

			key("D", Items.DIAMOND)
			key("S", Items.STICK)

			result(Items.DIAMOND_SWORD)
		}

		smithingTransform("diamond_to_netherite") {
			template(Items.DIAMOND_BLOCK)
			base(Items.DIAMOND_SWORD)
			addition(Items.NETHERITE_INGOT)
			result(Items.NETHERITE_SWORD)
		}
	}

	recipes.last() assertsIs """
			{
				"type": "minecraft:smithing_transform",
				"template": {
					"item": "minecraft:diamond_block"
				},
				"base": {
					"item": "minecraft:diamond_sword"
				},
				"addition": {
					"item": "minecraft:netherite_ingot"
				},
				"result": {
					"id": "minecraft:netherite_sword"
				}
			}
		""".trimIndent()

	val bowsRecipe = recipesBuilder.craftingShaped("sowb") {
		pattern(
			" ~|",
			"~ |",
			" ~|"
		)

		keys {
			"~" to Items.STRING
			"|" to Items.STICK
		}

		result(Items.BOW)
	}

	recipes.last() assertsIs """
		{
			"type": "minecraft:crafting_shaped",
			"pattern": [
				" ~|",
				"~ |",
				" ~|"
			],
			"key": {
				"~": {
					"item": "minecraft:string"
				},
				"|": {
					"item": "minecraft:stick"
				}
			},
			"result": {
				"id": "minecraft:bow"
			}
		}
	""".trimIndent()

	load {
		recipeGive(allPlayers(), bowsRecipe)
	}

	recipes {
		craftingShaped("sharpness_1") {
			pattern(
				"E E",
				" B ",
				"E E",
			)

			keys {
				"E" to Items.EMERALD
				"B" to Items.BOOK
			}

			result(Items.ENCHANTED_BOOK) {
				enchantments {
					enchantment(Enchantments.SHARPNESS, 1)
				}
			}
		}
	}

	recipes.last() assertsIs """
		{
			"type": "minecraft:crafting_shaped",
			"pattern": [
				"E E",
				" B ",
				"E E"
			],
			"key": {
				"E": {
					"item": "minecraft:emerald"
				},
				"B": {
					"item": "minecraft:book"
				}
			},
			"result": {
				"id": "minecraft:enchanted_book",
				"components": {
					"enchantments": {
						"minecraft:sharpness": 1
					}
				}
			}
		}
	""".trimIndent()
}
