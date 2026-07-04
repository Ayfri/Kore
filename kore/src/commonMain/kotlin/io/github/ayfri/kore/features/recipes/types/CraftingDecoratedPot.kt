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
 * Recipe for crafting a decorated pot from four pottery sherds arranged in a diamond pattern.
 *
 * Each face slot ([back], [front], [left], [right]) accepts a sherd or brick ingredient.
 * Produces `data/<namespace>/recipe/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_decorated_pot
 */
@Serializable
data class CraftingDecoratedPot(
	/** The sherd placed in the back slot. */
	var back: InlinableList<ItemOrTagArgument>,
	/** The sherd placed in the front slot. */
	var front: InlinableList<ItemOrTagArgument>,
	/** The sherd placed in the left slot. */
	var left: InlinableList<ItemOrTagArgument>,
	override var result: CraftingResult,
	/** The sherd placed in the right slot. */
	var right: InlinableList<ItemOrTagArgument>,
) : Recipe(), ResultedRecipe {
	override val type = RecipeTypes.CRAFTING_DECORATED_POT
}

/**
 * Adds a `crafting_decorated_pot` recipe to the data pack.
 *
 * Use [CraftingDecoratedPot.back], [CraftingDecoratedPot.front], [CraftingDecoratedPot.left],
 * and [CraftingDecoratedPot.right] inside the block to set the sherd ingredients.
 * Produces `data/<namespace>/recipe/<name>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_decorated_pot
 */
fun Recipes.craftingDecoratedPot(name: String, block: CraftingDecoratedPot.() -> Unit): RecipeArgument {
	val recipe = RecipeFile(
		name, CraftingDecoratedPot(
			back = listOf(),
			front = listOf(),
			left = listOf(),
			result = CraftingResult(id = ""),
			right = listOf(),
		).apply(block)
	)
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}

/** Sets the back sherd to one or more specific items. */
fun CraftingDecoratedPot.back(vararg items: ItemArgument) = apply { back = items.toList() }

/** Sets the back sherd to an item tag. */
fun CraftingDecoratedPot.back(tag: ItemTagArgument) = apply { back = listOf(tag) }

/** Sets the front sherd to one or more specific items. */
fun CraftingDecoratedPot.front(vararg items: ItemArgument) = apply { front = items.toList() }

/** Sets the front sherd to an item tag. */
fun CraftingDecoratedPot.front(tag: ItemTagArgument) = apply { front = listOf(tag) }

/** Sets the left sherd to one or more specific items. */
fun CraftingDecoratedPot.left(vararg items: ItemArgument) = apply { left = items.toList() }

/** Sets the left sherd to an item tag. */
fun CraftingDecoratedPot.left(tag: ItemTagArgument) = apply { left = listOf(tag) }

/** Sets the right sherd to one or more specific items. */
fun CraftingDecoratedPot.right(vararg items: ItemArgument) = apply { right = items.toList() }

/** Sets the right sherd to an item tag. */
fun CraftingDecoratedPot.right(tag: ItemTagArgument) = apply { right = listOf(tag) }
