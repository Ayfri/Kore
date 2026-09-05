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
 * Recipe for crafting a firework rocket from fuel, shell, and optional star ingredients.
 *
 * Produces `data/<namespace>/recipe/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_special_firework_rocket
 */
@Serializable
data class CraftingSpecialFireworkRocket(
	/** The propellant item (e.g. gunpowder). */
	var fuel: InlinableList<ItemOrTagArgument>,
	override var result: CraftingResult,
	/** The casing item (e.g. paper). */
	var shell: InlinableList<ItemOrTagArgument>,
	/** Optional firework star(s) to embed in the rocket. */
	var star: InlinableList<ItemOrTagArgument>,
) : Recipe(), ResultedRecipe {
	override val type = RecipeTypes.CRAFTING_SPECIAL("firework_rocket")
}

/**
 * Adds a `crafting_special_firework_rocket` recipe to the data pack.
 *
 * Use [CraftingSpecialFireworkRocket.fuel], [CraftingSpecialFireworkRocket.shell],
 * and [CraftingSpecialFireworkRocket.star] inside the block to configure the ingredients.
 * Produces `data/<namespace>/recipe/<name>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_special_firework_rocket
 */
fun Recipes.craftingSpecialFireworkRocket(
	name: String,
	block: CraftingSpecialFireworkRocket.() -> Unit
): RecipeArgument {
	val recipe = RecipeFile(
		name, CraftingSpecialFireworkRocket(
			fuel = listOf(),
			result = CraftingResult(id = ""),
			shell = listOf(),
			star = listOf(),
		).apply(block)
	)
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}

/** Sets the fuel ingredient to one or more specific items. */
fun CraftingSpecialFireworkRocket.fuel(vararg items: ItemArgument) = apply { fuel = items.toList() }

/** Sets the fuel ingredient to an item tag. */
fun CraftingSpecialFireworkRocket.fuel(tag: ItemTagArgument) = apply { fuel = listOf(tag) }

/** Sets the shell ingredient to one or more specific items. */
fun CraftingSpecialFireworkRocket.shell(vararg items: ItemArgument) = apply { shell = items.toList() }

/** Sets the shell ingredient to an item tag. */
fun CraftingSpecialFireworkRocket.shell(tag: ItemTagArgument) = apply { shell = listOf(tag) }

/** Sets the star ingredient to one or more specific items. */
fun CraftingSpecialFireworkRocket.star(vararg items: ItemArgument) = apply { star = items.toList() }

/** Sets the star ingredient to an item tag. */
fun CraftingSpecialFireworkRocket.star(tag: ItemTagArgument) = apply { star = listOf(tag) }
