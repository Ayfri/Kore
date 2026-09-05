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
 * Pattern-based crafting table recipe where ingredient positions matter.
 *
 * Produces `data/<namespace>/recipe/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_shaped
 */
@Serializable
data class CraftingShaped(
	override var group: String? = null,
	/** Up to 3 rows defining the crafting pattern; each character maps to a [key] entry. */
	var pattern: List<String> = emptyList(),
	/** Maps each pattern character to its ingredient(s). */
	var key: MutableMap<String, InlinableList<ItemOrTagArgument>> = mutableMapOf(),
	override var result: CraftingResult,
	var showNotification: Boolean? = null,
) : Recipe(), ResultedRecipe {
	override val type = RecipeTypes.CRAFTING_SHAPED
}

/**
 * Adds a `crafting_shaped` recipe to the data pack.
 *
 * Use [CraftingShaped.pattern] and [CraftingShaped.key] (or [CraftingShaped.keys]) inside the block to define the layout.
 * Produces `data/<namespace>/recipe/<name>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_shaped
 */
fun Recipes.craftingShaped(name: String, block: CraftingShaped.() -> Unit): RecipeArgument {
	val recipe = RecipeFile(name, CraftingShaped(result = CraftingResult("")).apply(block))
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}

/** Maps [key] character to a list of accepted items. */
fun CraftingShaped.key(key: String, ingredients: List<ItemArgument>) = this.key.put(key, ingredients.toList())

/** Maps [key] character to one or more accepted items. */
fun CraftingShaped.key(key: String, vararg ingredients: ItemArgument) = this.key.put(key, ingredients.toList())

/** Maps [key] character to an item tag. */
fun CraftingShaped.key(key: String, tag: ItemTagArgument) =
	this.key.put(key, listOf(tag))

/** DSL receiver for the [CraftingShaped.keys] block; maps pattern characters to ingredients with `"X" to item`. */
data class Keys(val key: MutableMap<String, List<ItemOrTagArgument>>) {
	infix fun String.to(item: ItemArgument) = key.put(this, mutableListOf(item))
	infix fun String.to(tag: ItemTagArgument) = key.put(this, mutableListOf(tag))
	infix fun String.to(ingredients: List<ItemOrTagArgument>) = key.put(this, ingredients.toMutableList())
}

/** Configures all pattern key mappings in a single block using `"X" to item` syntax. */
fun CraftingShaped.keys(block: Keys.() -> Unit) = Keys(key).apply(block)

/** Appends a single row to the crafting pattern. */
fun CraftingShaped.patternLine(line: String) {
	pattern += line
}

/** Appends one or more rows to the crafting pattern. */
fun CraftingShaped.pattern(vararg lines: String) {
	pattern += lines
}
