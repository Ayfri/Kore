package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
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
 * Crafting table recipe that transforms an item while preserving its data components.
 *
 * The `input` item is combined with a `material` to produce `result`, copying components from the input.
 * Produces `data/<namespace>/recipe/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_transmute
 */
@Serializable
data class CraftingTransmute(
	/** If true, the output count is increased by the material count. */
	var addMaterialCountToResult: Boolean? = null,
	/** Optional recipe book category. */
	var category: RecipeCategory? = null,
	override var group: String? = null,
	/** The item to be transformed; its components are copied to the result. */
	var input: InlinableList<ItemOrTagArgument>,
	/** The catalyst item consumed alongside the input. */
	var material: InlinableList<ItemOrTagArgument>,
	/** How many material items are consumed per craft; can be a range or exact int. */
	var materialCount: IntRangeOrIntJson? = null,
	override var result: CraftingResult,
	var showNotification: Boolean? = null,
) : Recipe(), ResultedRecipe {
	override val type = RecipeTypes.CRAFTING_TRANSMUTE
}

/**
 * Adds a `crafting_transmute` recipe to the data pack.
 *
 * Use [CraftingTransmute.input] and [CraftingTransmute.material] inside the block to set the ingredients.
 * Produces `data/<namespace>/recipe/<name>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_transmute
 */
fun Recipes.craftingTransmute(name: String, block: CraftingTransmute.() -> Unit): RecipeArgument {
	val recipe = RecipeFile(
		name, CraftingTransmute(
			input = listOf(),
			material = listOf(),
			result = CraftingResult(id = ""),
		).apply(block)
	)
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}

/** Sets the input (item to be transformed) to one or more specific items. */
fun CraftingTransmute.input(vararg items: ItemArgument) = apply { input = items.toList() }

/** Sets the input (item to be transformed) to an item tag. */
fun CraftingTransmute.input(tag: ItemTagArgument) = apply { input = listOf(tag) }

/** Sets the material (catalyst) to one or more specific items. */
fun CraftingTransmute.material(vararg items: ItemArgument) = apply { material = items.toList() }

/** Sets the material (catalyst) to an item tag. */
fun CraftingTransmute.material(tag: ItemTagArgument) = apply { material = listOf(tag) }
