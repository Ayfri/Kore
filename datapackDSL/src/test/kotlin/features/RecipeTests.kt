package features

import DataPack
import arguments.types.literals.allPlayers
import commands.recipeGive
import features.recipes.recipes
import features.recipes.recipesBuilder
import features.recipes.types.*
import functions.load
import generated.Items
import generated.Tags
import utils.assertsIs

fun DataPack.recipeTest() {
	recipes {
		blasting("test_blasting") {
			ingredient {
				tag = Tags.Items.STONE_CRAFTING_MATERIALS
			}

			result = Items.DIAMOND
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
			base(Items.DIAMOND_SWORD)
			addition(Items.NETHERITE_INGOT)
			result = Items.NETHERITE_SWORD
		}
	}

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
				"item": "minecraft:bow"
			},
			"type": "minecraft:crafting_shaped"
		}
	""".trimIndent()

	load {
		recipeGive(allPlayers(), bowsRecipe)
	}
}
