package io.github.ayfri.kore.website.playground.snippets.recipes

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.recipes.recipes
import io.github.ayfri.kore.features.recipes.types.blasting
import io.github.ayfri.kore.features.recipes.types.craftingShaped
import io.github.ayfri.kore.features.recipes.types.ingredient
import io.github.ayfri.kore.features.recipes.types.key
import io.github.ayfri.kore.features.recipes.types.pattern
import io.github.ayfri.kore.features.recipes.types.result
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.pack.pack

fun playground() = dataPack("custom_recipes") {
	pack { description = textComponent("Crafting and smelting recipes") }

	recipes {
		craftingShaped("diamond_stick") {
			pattern(
				" D ",
				" D ",
				" S "
			)

			key("D", Items.DIAMOND)
			key("S", Items.STICK)

			result(Items.DIAMOND_SWORD)
		}

		blasting("stone_to_diamond") {
			ingredient(Tags.Item.STONE_CRAFTING_MATERIALS)

			result(Items.DIAMOND)
			cookingTime = 200
			experience = 10.0
		}
	}
}
