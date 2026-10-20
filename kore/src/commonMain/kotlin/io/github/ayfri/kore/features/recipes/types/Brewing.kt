package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.components.ComponentsPatch
import io.github.ayfri.kore.arguments.components.matchers.PotionContentsComponentMatcher
import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.recipes.RecipeFile
import io.github.ayfri.kore.features.recipes.RecipeTypes
import io.github.ayfri.kore.features.recipes.Recipes
import io.github.ayfri.kore.features.recipes.data.CraftingResult
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * An ingredient of a [Brewing] recipe: one or more items, optionally restricted to a potion contents.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#brewing
 */
@Serializable
data class PotionIngredient(
	/** The item(s) or item tag accepted by the slot. */
	var item: InlinableList<ItemOrTagArgument>,
	/** Optional `potion_contents` component predicate the item has to match. */
	var potionContents: PotionContentsComponentMatcher? = null,
)

/**
 * Brewing stand recipe turning an [input] potion and a [reagent] ingredient into the [output] item.
 *
 * Produces `data/<namespace>/recipe/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#brewing
 */
@Serializable
data class Brewing(
	/** The potion bottle placed in the bottom slots of the brewing stand. */
	var input: PotionIngredient,
	/** The item produced in the bottle slot. */
	var output: CraftingResult,
	/** The ingredient placed in the top slot of the brewing stand. */
	var reagent: PotionIngredient,
) : Recipe() {
	override val type = RecipeTypes.BREWING
}

/**
 * Adds a `brewing` recipe to the data pack.
 *
 * Use [Brewing.input], [Brewing.reagent] and [Brewing.output] inside the block.
 * Produces `data/<namespace>/recipe/<name>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#brewing
 */
fun Recipes.brewing(name: String, namespace: String? = null, block: Brewing.() -> Unit): RecipeArgument {
	val recipe = RecipeFile(
		name, Brewing(
			input = PotionIngredient(listOf()),
			output = CraftingResult(id = ""),
			reagent = PotionIngredient(listOf()),
		).apply(block)
	)
	return register(recipe, namespace)
}

/**
 * Sets the input to [items], optionally restricted to the potion contents built by [potionContents].
 *
 * ```
 * input(Items.LINGERING_POTION) { potions(Potions.AWKWARD) }
 * ```
 */
fun Brewing.input(vararg items: ItemOrTagArgument, potionContents: (PotionContentsComponentMatcher.() -> Unit)? = null) = apply {
	input = PotionIngredient(items.toList(), potionContents?.let { PotionContentsComponentMatcher().apply(it) })
}

/** Sets the output to [item] with an optional count and component patch. */
fun Brewing.output(item: ItemArgument, count: Short? = null, init: (ComponentsPatch.() -> Unit)? = null) = apply {
	output = CraftingResult(item.asId(), count, init?.let { ComponentsPatch().apply(it) })
}

/** Sets the output using a [CraftingResult] builder block. */
fun Brewing.output(block: CraftingResult.() -> Unit) = apply { output = CraftingResult("").apply(block) }

/** Sets the reagent to [items], optionally restricted to the potion contents built by [potionContents]. */
fun Brewing.reagent(vararg items: ItemOrTagArgument, potionContents: (PotionContentsComponentMatcher.() -> Unit)? = null) = apply {
	reagent = PotionIngredient(items.toList(), potionContents?.let { PotionContentsComponentMatcher().apply(it) })
}
