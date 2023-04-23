package features

import arguments.allPlayers
import commands.recipeGive
import dataPack
import features.recipes.*
import functions.load
import generated.Items
import generated.Tags

fun recipeTest() = dataPack("recipe_tests") {
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

	load {
		recipeGive(allPlayers(), bowsRecipe)
	}
}.generate()
