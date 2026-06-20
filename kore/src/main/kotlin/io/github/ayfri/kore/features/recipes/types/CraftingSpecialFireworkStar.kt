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
 * Recipe for crafting a firework star from dye, fuel, optional shape ingredients, and effect modifiers.
 *
 * Produces `data/<namespace>/recipe/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_special_firework_star
 */
@Serializable
data class CraftingSpecialFireworkStar(
	/** The dye ingredient(s) to colour the star. */
	var dye: InlinableList<ItemOrTagArgument>,
	/** The propellant ingredient (e.g. gunpowder). */
	var fuel: InlinableList<ItemOrTagArgument>,
	override var result: CraftingResult,
	/** Maps shape names to their ingredient(s) (e.g. `"burst"` to a fire charge). */
	var shapes: MutableMap<String, InlinableList<ItemOrTagArgument>>,
	/** Trail effect ingredient. */
	var trail: InlinableList<ItemOrTagArgument>,
	/** Twinkle effect ingredient. */
	var twinkle: InlinableList<ItemOrTagArgument>,
) : Recipe(), ResultedRecipe {
	override val type = RecipeTypes.CRAFTING_SPECIAL("firework_star")
}

/**
 * Adds a `crafting_special_firework_star` recipe to the data pack.
 *
 * Use [CraftingSpecialFireworkStar.dye], [CraftingSpecialFireworkStar.fuel],
 * [CraftingSpecialFireworkStar.shape], [CraftingSpecialFireworkStar.trail],
 * and [CraftingSpecialFireworkStar.twinkle] inside the block to configure the ingredients.
 * Produces `data/<namespace>/recipe/<name>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_special_firework_star
 */
fun Recipes.craftingSpecialFireworkStar(name: String, block: CraftingSpecialFireworkStar.() -> Unit): RecipeArgument {
	val recipe = RecipeFile(
		name, CraftingSpecialFireworkStar(
			dye = listOf(),
			fuel = listOf(),
			result = CraftingResult(id = ""),
			shapes = mutableMapOf(),
			trail = listOf(),
			twinkle = listOf(),
		).apply(block)
	)
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}

/** Sets the dye ingredient(s) to one or more specific items. */
fun CraftingSpecialFireworkStar.dye(vararg items: ItemArgument) = apply { dye = items.toList() }

/** Sets the dye ingredient to an item tag. */
fun CraftingSpecialFireworkStar.dye(tag: ItemTagArgument) = apply { dye = listOf(tag) }

/** Sets the fuel ingredient to one or more specific items. */
fun CraftingSpecialFireworkStar.fuel(vararg items: ItemArgument) = apply { fuel = items.toList() }

/** Sets the fuel ingredient to an item tag. */
fun CraftingSpecialFireworkStar.fuel(tag: ItemTagArgument) = apply { fuel = listOf(tag) }

/** Maps [shapeName] to one or more specific items as the shape ingredient. */
fun CraftingSpecialFireworkStar.shape(shapeName: String, vararg items: ItemArgument) =
	apply { shapes[shapeName] = items.toList() }

/** Maps [shapeName] to an item tag as the shape ingredient. */
fun CraftingSpecialFireworkStar.shape(shapeName: String, tag: ItemTagArgument) =
	apply { shapes[shapeName] = listOf(tag) }

/** Sets the trail effect ingredient to one or more specific items. */
fun CraftingSpecialFireworkStar.trail(vararg items: ItemArgument) = apply { trail = items.toList() }

/** Sets the trail effect ingredient to an item tag. */
fun CraftingSpecialFireworkStar.trail(tag: ItemTagArgument) = apply { trail = listOf(tag) }

/** Sets the twinkle effect ingredient to one or more specific items. */
fun CraftingSpecialFireworkStar.twinkle(vararg items: ItemArgument) = apply { twinkle = items.toList() }

/** Sets the twinkle effect ingredient to an item tag. */
fun CraftingSpecialFireworkStar.twinkle(tag: ItemTagArgument) = apply { twinkle = listOf(tag) }
