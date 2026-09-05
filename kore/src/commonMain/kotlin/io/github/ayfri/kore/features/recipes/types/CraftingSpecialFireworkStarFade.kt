package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.tagged.ItemTagArgument
import io.github.ayfri.kore.features.recipes.RecipeFile
import io.github.ayfri.kore.features.recipes.RecipeTypes
import io.github.ayfri.kore.features.recipes.Recipes
import io.github.ayfri.kore.features.recipes.data.CraftingResult
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Recipe that adds a fade colour to an existing firework star.
 *
 * Produces `data/<namespace>/recipe/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_special_firework_star_fade
 */
@Serializable
data class CraftingSpecialFireworkStarFade(
	/** The dye ingredient(s) defining the fade colour. */
	var dye: InlinableList<ItemOrTagArgument>,
	override var result: CraftingResult,
	/** The firework star to add the fade to. */
	var target: InlinableList<ItemOrTagArgument>,
) : Recipe(), ResultedRecipe {
	override val type = RecipeTypes.CRAFTING_SPECIAL("firework_star_fade")
}

/**
 * Adds a `crafting_special_firework_star_fade` recipe to the data pack.
 *
 * Use [CraftingSpecialFireworkStarFade.dye] and [CraftingSpecialFireworkStarFade.target] inside the block.
 * Produces `data/<namespace>/recipe/<name>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_special_firework_star_fade
 */
fun Recipes.craftingSpecialFireworkStarFade(
	name: String,
	block: CraftingSpecialFireworkStarFade.() -> Unit
): RecipeArgument {
	val recipe = RecipeFile(
		name, CraftingSpecialFireworkStarFade(
			dye = listOf(),
			result = CraftingResult(id = ""),
			target = listOf(),
		).apply(block)
	)
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}

/** Sets the dye ingredient(s) to one or more specific items. */
fun CraftingSpecialFireworkStarFade.dye(vararg items: ItemArgument) = apply { dye = items.toList() }

/** Sets the dye ingredient to an item tag. */
fun CraftingSpecialFireworkStarFade.dye(tag: ItemTagArgument) = apply { dye = listOf(tag) }

/** Sets the target firework star to one or more specific items. */
fun CraftingSpecialFireworkStarFade.target(vararg items: ItemArgument) = apply { target = items.toList() }

/** Sets the target firework star to an item tag. */
fun CraftingSpecialFireworkStarFade.target(tag: ItemTagArgument) = apply { target = listOf(tag) }
